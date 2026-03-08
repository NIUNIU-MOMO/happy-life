package com.happylife.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @Author NIUNIU
 * @Date 2026/03/01 14:05
 * @Version 1.0
 * @Description 收藏请求参数
 */
@Data
@Schema(title = "收藏请求参数")
public class FavoriteParam {

    @NotBlank(message = "操作类型不能为空")
    @Schema(title = "操作类型：favorite / unfavorite")
    private String action;
}
