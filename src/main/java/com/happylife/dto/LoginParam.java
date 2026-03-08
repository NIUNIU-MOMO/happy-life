package com.happylife.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @Author NIUNIU
 * @Date 2026/03/01 12:05
 * @Version 1.0
 * @Description 登录请求参数
 */
@Data
@Schema(title = "登录请求参数")
public class LoginParam {

    @NotBlank(message = "登录凭证不能为空")
    @Schema(title = "微信登录凭证 code")
    private String code;

    @Schema(title = "用户昵称")
    private String nickName;

    @Schema(title = "用户头像")
    private String avatarUrl;
}
