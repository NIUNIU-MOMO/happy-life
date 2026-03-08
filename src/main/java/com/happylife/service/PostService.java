package com.happylife.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.happylife.dto.PostCreateParam;
import com.happylife.dto.PostQueryParam;
import com.happylife.vo.PostDetailVo;
import com.happylife.vo.PostVo;

import java.util.List;

/**
 * @Author NIUNIU
 * @Date 2026/03/01 13:30
 * @Version 1.0
 * @Description 帖子服务接口
 */
public interface PostService {

    /**
     * 发布帖子
     *
     * @param param 创建参数
     * @return 帖子 ID
     */
    Long createPost(PostCreateParam param);

    /**
     * 时间线分页查询
     *
     * @param param 查询参数
     * @return 分页帖子列表
     */
    IPage<PostVo> getTimelinePosts(PostQueryParam param);

    /**
     * 地图区域查询
     *
     * @param param 查询参数
     * @return 帖子列表
     */
    List<PostVo> getMapPosts(PostQueryParam param);

    /**
     * 获取帖子详情
     *
     * @param id 帖子 ID
     * @return 帖子详情
     */
    PostDetailVo getPostDetail(Long id);

    /**
     * 删除帖子
     *
     * @param id 帖子 ID
     */
    void deletePost(Long id);
}
