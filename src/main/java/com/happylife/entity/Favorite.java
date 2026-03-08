package com.happylife.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @Author NIUNIU
 * @Date 2026/03/01 11:10
 * @Version 1.0
 * @Description 收藏实体
 */
@Data
@Accessors(chain = true)
@TableName("t_favorite")
@Schema(title = "收藏")
public class Favorite {

    @TableId(type = IdType.AUTO)
    @Schema(title = "收藏 ID")
    private Long id;

    @Schema(title = "用户 ID")
    private Long userId;

    @Schema(title = "帖子 ID")
    private Long postId;

    @Schema(title = "创建时间")
    private LocalDateTime createTime;
}
