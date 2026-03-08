package com.happylife.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.happylife.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author NIUNIU
 * @Date 2026/03/01 12:15
 * @Version 1.0
 * @Description 用户 Mapper
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
