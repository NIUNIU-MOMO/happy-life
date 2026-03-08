package com.happylife.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author NIUNIU
 * @Date 2026/03/01 12:10
 * @Version 1.0
 * @Description 登录响应
 */
@Data
@Accessors(chain = true)
@Schema(title = "登录响应")
public class LoginVo {

    @Schema(title = "JWT Token")
    private String token;

    @Schema(title = "用户 ID")
    private Long userId;

    @Schema(title = "昵称")
    private String nickname;

    @Schema(title = "头像")
    private String avatarUrl;
}
