package com.happylife.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.happylife.common.ServiceException;
import com.happylife.dto.PostCreateParam;
import com.happylife.dto.PostQueryParam;
import com.happylife.entity.Favorite;
import com.happylife.entity.Post;
import com.happylife.mapper.FavoriteMapper;
import com.happylife.mapper.PostMapper;
import com.happylife.service.PostService;
import com.happylife.util.UserContext;
import com.happylife.vo.PostDetailVo;
import com.happylife.vo.PostVo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Author NIUNIU
 * @Date 2026/03/01 13:35
 * @Version 1.0
 * @Description 帖子服务实现
 */
@Slf4j
@Service("postService")
public class PostServiceImpl implements PostService {

    @Resource
    private PostMapper postMapper;

    @Resource
    private FavoriteMapper favoriteMapper;

    @Override
    public Long createPost(PostCreateParam param) {
        Long userId = UserContext.getUserId();
        Post post = new Post()
                .setUserId(userId)
                .setTitle(param.getTitle())
                .setDescription(param.getDescription())
                .setImages(param.getImages())
                .setVideoUrl(param.getVideoUrl())
                .setLocationName(param.getLocationName())
                .setLatitude(param.getLatitude())
                .setLongitude(param.getLongitude())
                .setTags(param.getTags());

        if (null != param.getDate()) {
            post.setPostDate(LocalDate.parse(param.getDate(), DateTimeFormatter.ISO_LOCAL_DATE));
        }

        postMapper.insertWithPoint(post);
        return post.getId();
    }

    @Override
    public IPage<PostVo> getTimelinePosts(PostQueryParam param) {
        Long userId = UserContext.getUserId();
        Page<Post> page = new Page<>(param.getPage(), param.getSize());
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<Post>()
                .orderByDesc(Post::getPostDate);
        IPage<Post> postPage = postMapper.selectPage(page, wrapper);

        return postPage.convert(post -> convertToVo(post, userId));
    }

    @Override
    public List<PostVo> getMapPosts(PostQueryParam param) {
        Long userId = UserContext.getUserId();
        List<Post> posts = postMapper.selectByMapBounds(
                param.getMinLng(), param.getMinLat(),
                param.getMaxLng(), param.getMaxLat());
        return CollectionUtils.isEmpty(posts)
                ? new ArrayList<>()
                : posts.stream().map(post -> convertToVo(post, userId)).collect(Collectors.toList());
    }

    @Override
    public PostDetailVo getPostDetail(Long id) {
        Long userId = UserContext.getUserId();
        Post post = postMapper.selectById(id);
        if (null == post) {
            throw new ServiceException("帖子不存在");
        }

        postMapper.updateById(new Post().setId(id).setViewCount(post.getViewCount() + 1));

        boolean isFavorited = checkFavorited(userId, id);
        return new PostDetailVo()
                .setId(post.getId())
                .setTitle(post.getTitle())
                .setDescription(post.getDescription())
                .setImages(post.getImages())
                .setVideoUrl(post.getVideoUrl())
                .setLocationName(post.getLocationName())
                .setLatitude(post.getLatitude())
                .setLongitude(post.getLongitude())
                .setPostDate(null != post.getPostDate() ? post.getPostDate().toString() : null)
                .setTags(post.getTags())
                .setViewCount(post.getViewCount())
                .setLikeCount(post.getLikeCount())
                .setIsFavorited(isFavorited)
                .setCreateTime(null != post.getCreateTime() ? post.getCreateTime().toString() : null);
    }

    @Override
    public void deletePost(Long id) {
        Long userId = UserContext.getUserId();
        Post post = postMapper.selectById(id);
        if (null == post) {
            throw new ServiceException("帖子不存在");
        }
        if (!Objects.equals(post.getUserId(), userId)) {
            throw new ServiceException("无权限删除");
        }
        postMapper.deleteById(id);
    }

    // =================================================================================================================

    /**
     * 转换帖子实体为列表 VO
     *
     * @param post   帖子实体
     * @param userId 当前用户 ID
     * @return 帖子列表 VO
     */
    private PostVo convertToVo(Post post, Long userId) {
        String coverImage = CollectionUtils.isEmpty(post.getImages()) ? null : post.getImages().get(0);
        boolean isFavorited = checkFavorited(userId, post.getId());
        return new PostVo()
                .setId(post.getId())
                .setTitle(post.getTitle())
                .setDescription(post.getDescription())
                .setCoverImage(coverImage)
                .setLocationName(post.getLocationName())
                .setLatitude(post.getLatitude())
                .setLongitude(post.getLongitude())
                .setPostDate(null != post.getPostDate() ? post.getPostDate().toString() : null)
                .setTags(post.getTags())
                .setLikeCount(post.getLikeCount())
                .setIsFavorited(isFavorited);
    }

    /**
     * 检查用户是否收藏了帖子
     *
     * @param userId 用户 ID
     * @param postId 帖子 ID
     * @return 是否收藏
     */
    private boolean checkFavorited(Long userId, Long postId) {
        if (null == userId) {
            return false;
        }
        Long count = favoriteMapper.selectCount(
                new LambdaQueryWrapper<Favorite>()
                        .eq(Favorite::getUserId, userId)
                        .eq(Favorite::getPostId, postId));
        return count > 0;
    }
}
