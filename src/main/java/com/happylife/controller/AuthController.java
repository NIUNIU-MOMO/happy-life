package com.happylife.controller;

import com.happylife.common.Result;
import com.happylife.dto.LoginParam;
import com.happylife.service.UserService;
import com.happylife.vo.LoginVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author NIUNIU
 * @Date 2026/03/01 12:30
 * @Version 1.0
 * @Description 认证控制器
 */
@Tag(name = "认证模块")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Resource
    private UserService userService;

    @Operation(summary = "微信登录")
    @PostMapping("/login")
    public Result<LoginVo> login(@Valid @RequestBody LoginParam param) {
        return Result.ok(userService.login(param));
    }
}
