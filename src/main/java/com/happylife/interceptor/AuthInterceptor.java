package com.happylife.interceptor;

import com.happylife.common.ServiceException;
import com.happylife.util.JwtUtil;
import com.happylife.util.UserContext;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @Author NIUNIU
 * @Date 2026/03/01 11:45
 * @Version 1.0
 * @Description JWT 认证拦截器
 */
@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Resource
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = request.getHeader("Authorization");
        if (null == token || !token.startsWith("Bearer ")) {
            throw new ServiceException(401, "未登录");
        }
        try {
            Claims claims = jwtUtil.parseToken(token.substring(7));
            Long userId = claims.get("userId", Long.class);
            UserContext.setUserId(userId);
            return true;
        } catch (Exception e) {
            log.warn("Token 解析失败：{}", e.getMessage());
            throw new ServiceException(401, "登录已过期");
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }
}
