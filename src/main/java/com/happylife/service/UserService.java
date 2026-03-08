package com.happylife.service;

import com.happylife.dto.LoginParam;
import com.happylife.vo.LoginVo;

/**
 * @Author NIUNIU
 * @Date 2026/03/01 12:20
 * @Version 1.0
 * @Description 用户服务接口
 */
public interface UserService {

    /**
     * 微信登录
     *
     * @param param 登录参数
     * @return 登录响应
     */
    LoginVo login(LoginParam param);
}
