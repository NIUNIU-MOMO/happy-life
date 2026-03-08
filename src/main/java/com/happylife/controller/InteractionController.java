package com.happylife.controller;

import com.happylife.common.Result;
import com.happylife.dto.FavoriteParam;
import com.happylife.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * @Author NIUNIU
 * @Date 2026/03/01 14:20
 * @Version 1.0
 * @Description 互动控制器
 */
@Tag(name = "互动模块")
@RestController
@RequestMapping("/posts")
public class InteractionController {

    @Resource
    private FavoriteService favoriteService;

    @Operation(summary = "收藏/取消收藏")
    @PostMapping("/{id}/favorite")
    public Result<Void> toggleFavorite(@PathVariable Long id, @Valid @RequestBody FavoriteParam param) {
        favoriteService.toggleFavorite(id, param.getAction());
        return Result.ok();
    }
}
