package com.happylife.controller;

import com.happylife.common.Result;
import com.happylife.service.FileService;
import com.happylife.vo.OssPolicyVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author NIUNIU
 * @Date 2026/03/01 14:50
 * @Version 1.0
 * @Description 文件控制器
 */
@Tag(name = "文件模块")
@RestController
@RequestMapping("/file")
public class FileController {

    @Resource
    private FileService fileService;

    @Operation(summary = "获取 OSS 上传凭证")
    @PostMapping("/upload/policy")
    public Result<OssPolicyVo> getUploadPolicy() {
        return Result.ok(fileService.getUploadPolicy());
    }
}
