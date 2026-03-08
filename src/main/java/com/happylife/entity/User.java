package com.happylife.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @Author NIUNIU
 * @Date 2026/03/01 11:00
 * @Version 1.0
 * @Description 用户实体
 */
@Data
@Accessors(chain = true)
@TableName("t_user")
@Schema(title = "用户")
public class User {

    @TableId(type = IdType.AUTO)
    @Schema(title = "用户 ID")
    private Long id;

    @Schema(title = "微信 OpenID")
    private String openid;

    @Schema(title = "昵称")
    private String nickname;

    @Schema(title = "头像 URL")
    private String avatarUrl;

    @Schema(title = "创建时间")
    private LocalDateTime createTime;

    @Schema(title = "更新时间")
    private LocalDateTime updateTime;
}
