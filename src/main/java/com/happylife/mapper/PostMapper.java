package com.happylife.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.happylife.entity.Post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author NIUNIU
 * @Date 2026/03/01 13:20
 * @Version 1.0
 * @Description 帖子 Mapper
 */
@Mapper
public interface PostMapper extends BaseMapper<Post> {

    /**
     * 插入帖子并设置 location_point
     *
     * @param post 帖子实体
     * @return 影响行数
     */
    int insertWithPoint(@Param("post") Post post);

    /**
     * 地图区域查询
     *
     * @param minLng 最小经度
     * @param minLat 最小纬度
     * @param maxLng 最大经度
     * @param maxLat 最大纬度
     * @return 帖子列表
     */
    List<Post> selectByMapBounds(@Param("minLng") Double minLng,
                                 @Param("minLat") Double minLat,
                                 @Param("maxLng") Double maxLng,
                                 @Param("maxLat") Double maxLat);
}
