package com.happylife.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

/**
 * @Author NIUNIU
 * @Date 2026/03/01 13:00
 * @Version 1.0
 * @Description 发布帖子请求参数
 */
@Data
@Schema(title = "发布帖子请求参数")
public class PostCreateParam {

    @NotBlank(message = "标题不能为空")
    @Schema(title = "标题")
    private String title;

    @Schema(title = "描述")
    private String description;

    @Schema(title = "图片列表")
    private List<String> images;

    @Schema(title = "视频 URL")
    private String videoUrl;

    @Schema(title = "位置名称")
    private String locationName;

    @Schema(title = "纬度")
    private Double latitude;

    @Schema(title = "经度")
    private Double longitude;

    @Schema(title = "帖子日期 (YYYY-MM-DD)")
    private String date;

    @Schema(title = "标签列表")
    private List<String> tags;
}
