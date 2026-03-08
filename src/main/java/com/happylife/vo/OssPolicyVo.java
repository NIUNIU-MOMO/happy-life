package com.happylife.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author NIUNIU
 * @Date 2026/03/01 14:35
 * @Version 1.0
 * @Description OSS 上传凭证
 */
@Data
@Accessors(chain = true)
@Schema(title = "OSS 上传凭证")
public class OssPolicyVo {

    @Schema(title = "AccessKey ID")
    private String accessId;

    @Schema(title = "Base64 编码的 Policy")
    private String policy;

    @Schema(title = "签名")
    private String signature;

    @Schema(title = "上传目录")
    private String dir;

    @Schema(title = "OSS 上传地址")
    private String host;

    @Schema(title = "过期时间")
    private Long expire;
}
