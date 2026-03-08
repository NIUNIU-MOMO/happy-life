package com.happylife.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.happylife.common.Result;
import com.happylife.dto.PostCreateParam;
import com.happylife.dto.PostQueryParam;
import com.happylife.service.PostService;
import com.happylife.vo.PostDetailVo;
import com.happylife.vo.PostVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * @Author NIUNIU
 * @Date 2026/03/01 13:50
 * @Version 1.0
 * @Description 帖子控制器
 */
@Tag(name = "帖子模块")
@RestController
@RequestMapping("/posts")
public class PostController {

    @Resource
    private PostService postService;

    @Operation(summary = "发布帖子")
    @PostMapping
    public Result<Long> createPost(@Valid @RequestBody PostCreateParam param) {
        return Result.ok(postService.createPost(param));
    }

    @Operation(summary = "查询帖子列表")
    @GetMapping
    public Result<?> getPosts(PostQueryParam param) {
        if (Objects.equals("map", param.getType())) {
            List<PostVo> posts = postService.getMapPosts(param);
            return Result.ok(posts);
        }
        IPage<PostVo> page = postService.getTimelinePosts(param);
        return Result.ok(page);
    }

    @Operation(summary = "获取帖子详情")
    @GetMapping("/{id}")
    public Result<PostDetailVo> getPostDetail(@PathVariable Long id) {
        return Result.ok(postService.getPostDetail(id));
    }

    @Operation(summary = "删除帖子")
    @DeleteMapping("/{id}")
    public Result<Void> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return Result.ok();
    }
}
