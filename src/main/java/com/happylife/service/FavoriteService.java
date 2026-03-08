package com.happylife.service;

/**
 * @Author NIUNIU
 * @Date 2026/03/01 14:10
 * @Version 1.0
 * @Description 收藏服务接口
 */
public interface FavoriteService {

    /**
     * 收藏/取消收藏
     *
     * @param postId 帖子 ID
     * @param action 操作类型: favorite / unfavorite
     */
    void toggleFavorite(Long postId, String action);
}
