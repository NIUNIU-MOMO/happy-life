package com.happylife.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author NIUNIU
 * @Date 2026/03/01 11:05
 * @Version 1.0
 * @Description 帖子实体
 */
@Data
@Accessors(chain = true)
@TableName(value = "t_post", autoResultMap = true)
@Schema(title = "帖子")
public class Post {

    @TableId(type = IdType.AUTO)
    @Schema(title = "帖子 ID")
    private Long id;

    @Schema(title = "用户 ID")
    private Long userId;

    @Schema(title = "标题")
    private String title;

    @Schema(title = "描述")
    private String description;

    @TableField(typeHandler = JacksonTypeHandler.class)
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
    private LocalDate postDate;

    @TableField(typeHandler = JacksonTypeHandler.class)
    @Schema(title = "标签列表")
    private List<String> tags;

    @Schema(title = "浏览次数")
    private Integer viewCount;

    @Schema(title = "收藏次数")
    private Integer likeCount;

    @Schema(title = "创建时间")
    private LocalDateTime createTime;

    @Schema(title = "更新时间")
    private LocalDateTime updateTime;

    @TableLogic
    @Schema(title = "是否删除")
    private Boolean isDeleted;
}
