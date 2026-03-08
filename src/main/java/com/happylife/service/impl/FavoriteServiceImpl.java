package com.happylife.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.happylife.common.ServiceException;
import com.happylife.entity.Favorite;
import com.happylife.entity.Post;
import com.happylife.mapper.FavoriteMapper;
import com.happylife.mapper.PostMapper;
import com.happylife.service.FavoriteService;
import com.happylife.util.UserContext;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * @Author NIUNIU
 * @Date 2026/03/01 14:15
 * @Version 1.0
 * @Description 收藏服务实现
 */
@Slf4j
@Service("favoriteService")
public class FavoriteServiceImpl implements FavoriteService {

    @Resource
    private FavoriteMapper favoriteMapper;

    @Resource
    private PostMapper postMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void toggleFavorite(Long postId, String action) {
        Long userId = UserContext.getUserId();
        Post post = postMapper.selectById(postId);
        if (null == post) {
            throw new ServiceException("帖子不存在");
        }

        if (Objects.equals("favorite", action)) {
            Long count = favoriteMapper.selectCount(
                    new LambdaQueryWrapper<Favorite>()
                            .eq(Favorite::getUserId, userId)
                            .eq(Favorite::getPostId, postId));
            if (count > 0) {
                return;
            }
            Favorite favorite = new Favorite().setUserId(userId).setPostId(postId);
            favoriteMapper.insert(favorite);
            postMapper.updateById(new Post().setId(postId).setLikeCount(post.getLikeCount() + 1));
        } else if (Objects.equals("unfavorite", action)) {
            favoriteMapper.delete(
                    new LambdaQueryWrapper<Favorite>()
                            .eq(Favorite::getUserId, userId)
                            .eq(Favorite::getPostId, postId));
            int newCount = Math.max(0, post.getLikeCount() - 1);
            postMapper.updateById(new Post().setId(postId).setLikeCount(newCount));
        }
    }
}
