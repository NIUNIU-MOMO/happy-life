package com.happylife.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @Author NIUNIU
 * @Date 2026/03/01 13:15
 * @Version 1.0
 * @Description 帖子详情视图对象
 */
@Data
@Accessors(chain = true)
@Schema(title = "帖子详情")
public class PostDetailVo {

    @Schema(title = "帖子 ID")
    private Long id;

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

    @Schema(title = "帖子日期")
    private String postDate;

    @Schema(title = "标签列表")
    private List<String> tags;

    @Schema(title = "浏览次数")
    private Integer viewCount;

    @Schema(title = "收藏次数")
    private Integer likeCount;

    @Schema(title = "是否已收藏")
    private Boolean isFavorited;

    @Schema(title = "创建时间")
    private String createTime;
}
