package com.happylife.service.impl;

import com.happylife.config.OssProperties;
import com.happylife.service.FileService;
import com.happylife.vo.OssPolicyVo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

/**
 * @Author NIUNIU
 * @Date 2026/03/01 14:45
 * @Version 1.0
 * @Description 文件服务实现（OSS 直传签名）
 */
@Slf4j
@Service("fileService")
public class FileServiceImpl implements FileService {

    private static final long EXPIRE_TIME = 300L;

    @Resource
    private OssProperties ossProperties;

    @Override
    public OssPolicyVo getUploadPolicy() {
        String dir = "posts/" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")) + "/";
        String host = "https://" + ossProperties.getBucketName() + "." + ossProperties.getEndpoint();
        long expireEndTime = System.currentTimeMillis() / 1000 + EXPIRE_TIME;

        String policyJson = "{\"expiration\":\"" + getExpiration(expireEndTime) + "\","
                + "\"conditions\":[[\"content-length-range\",0,10485760],"
                + "[\"starts-with\",\"$key\",\"" + dir + "\"]]}";

        String policyBase64 = Base64.getEncoder().encodeToString(policyJson.getBytes(StandardCharsets.UTF_8));
        String signature = hmacSha1(ossProperties.getAccessKeySecret(), policyBase64);

        return new OssPolicyVo()
                .setAccessId(ossProperties.getAccessKeyId())
                .setPolicy(policyBase64)
                .setSignature(signature)
                .setDir(dir)
                .setHost(host)
                .setExpire(expireEndTime);
    }

    // =================================================================================================================

    /**
     * 计算 HMAC-SHA1 签名
     *
     * @param key  密钥
     * @param data 待签名数据
     * @return Base64 编码签名
     */
    private String hmacSha1(String key, String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA1"));
            byte[] rawHmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(rawHmac);
        } catch (Exception e) {
            log.error("签名计算异常：", e);
            throw new RuntimeException("签名计算失败");
        }
    }

    /**
     * 生成 ISO8601 格式过期时间
     *
     * @param expireEndTime 过期时间戳(秒)
     * @return ISO8601 格式字符串
     */
    private String getExpiration(long expireEndTime) {
        java.time.Instant instant = java.time.Instant.ofEpochSecond(expireEndTime);
        return instant.toString();
    }
}
