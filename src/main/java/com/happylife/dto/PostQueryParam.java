package com.happylife.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author NIUNIU
 * @Date 2026/03/01 13:05
 * @Version 1.0
 * @Description 帖子查询参数
 */
@Data
@Schema(title = "帖子查询参数")
public class PostQueryParam {

    @Schema(title = "页码")
    private Integer page = 1;

    @Schema(title = "每页条数")
    private Integer size = 10;

    @Schema(title = "查询类型：timeline / map")
    private String type = "timeline";

    @Schema(title = "最小纬度 (地图模式)")
    private Double minLat;

    @Schema(title = "最大纬度 (地图模式)")
    private Double maxLat;

    @Schema(title = "最小经度 (地图模式)")
    private Double minLng;

    @Schema(title = "最大经度 (地图模式)")
    private Double maxLng;
}
