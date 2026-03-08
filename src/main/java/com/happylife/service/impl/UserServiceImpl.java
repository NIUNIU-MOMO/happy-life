package com.happylife.service.impl;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.happylife.common.ServiceException;
import com.happylife.config.WechatProperties;
import com.happylife.dto.LoginParam;
import com.happylife.entity.User;
import com.happylife.mapper.UserMapper;
import com.happylife.service.UserService;
import com.happylife.util.JwtUtil;
import com.happylife.vo.LoginVo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author NIUNIU
 * @Date 2026/03/01 12:25
 * @Version 1.0
 * @Description 用户服务实现
 */
@Slf4j
@Service("userService")
public class UserServiceImpl implements UserService {

    private static final String WX_LOGIN_URL = "https://api.weixin.qq.com/sns/jscode2session";

    @Resource
    private UserMapper userMapper;

    @Resource
    private WechatProperties wechatProperties;

    @Resource
    private JwtUtil jwtUtil;

    @Override
    public LoginVo login(LoginParam param) {
        String openid = getOpenid(param.getCode());
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getOpenid, openid));

        if (null == user) {
            user = new User()
                    .setOpenid(openid)
                    .setNickname(param.getNickName())
                    .setAvatarUrl(param.getAvatarUrl());
            userMapper.insert(user);
        }

        Map<String, Object> claims = new HashMap<>(2);
        claims.put("userId", user.getId());
        claims.put("openid", openid);
        String token = jwtUtil.generateToken(claims);

        return new LoginVo()
                .setToken(token)
                .setUserId(user.getId())
                .setNickname(user.getNickname())
                .setAvatarUrl(user.getAvatarUrl());
    }

    // =================================================================================================================

    /**
     * 调用微信接口获取 openid
     *
     * @param code 微信登录凭证
     * @return openid
     */
    private String getOpenid(String code) {
        String url = WX_LOGIN_URL
                + "?appid=" + wechatProperties.getAppId()
                + "&secret=" + wechatProperties.getAppSecret()
                + "&js_code=" + code
                + "&grant_type=authorization_code";
        try {
            String result = HttpUtil.get(url);
            JSONObject json = JSONObject.parseObject(result);
            String openid = json.getString("openid");
            if (null == openid) {
                log.error("微信登录失败：{}", result);
                throw new ServiceException("微信登录失败");
            }
            return openid;
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("调用微信接口异常：", e);
            throw new ServiceException("微信登录异常");
        }
    }
}
