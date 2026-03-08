package com.happylife.service;

import com.happylife.vo.OssPolicyVo;

/**
 * @Author NIUNIU
 * @Date 2026/03/01 14:40
 * @Version 1.0
 * @Description 文件服务接口
 */
public interface FileService {

    /**
     * 获取 OSS 上传凭证
     *
     * @return 上传凭证
     */
    OssPolicyVo getUploadPolicy();
}
