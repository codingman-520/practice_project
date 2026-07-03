package com.aicompanion.interceptor;

import com.aicompanion.common.UserContext;
import com.aicompanion.entity.User;
import com.aicompanion.mapper.UserMapper;
import com.aicompanion.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtils jwtUtils;
    private final UserMapper userMapper;

    public JwtInterceptor(JwtUtils jwtUtils, UserMapper userMapper) {
        this.jwtUtils = jwtUtils;
        this.userMapper = userMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Authorization");
        
        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            token = token.substring(7);
            Claims claims = jwtUtils.getClaimsByToken(token);
            if (claims != null) {
                Long userId = claims.get("userId", Long.class);
                
                // 查询最新用户状态与角色
                User user = userMapper.selectById(userId);
                if (user == null) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"code\":401,\"message\":\"Unauthorized\"}");
                    return false;
                }
                
                // 1. 状态校验：若 status == 0（被管理端禁用），熔断返回 403
                if (user.getStatus() != null && user.getStatus() == 0) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"code\":403,\"message\":\"User is disabled\"}");
                    return false;
                }
                
                // 2. 角色校验：非 ADMIN 角色请求管理端接口，直接熔断返回 403
                String requestURI = request.getRequestURI();
                boolean isAdminApi = requestURI.startsWith("/api/dashboard") 
                        || requestURI.startsWith("/api/users") 
                        || requestURI.startsWith("/api/admin");
                if (isAdminApi && !"ADMIN".equals(user.getRole())) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"code\":403,\"message\":\"Access denied for non-ADMIN\"}");
                    return false;
                }
                
                UserContext.setUserId(userId);
                return true;
            }
        }
        
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":401,\"message\":\"Unauthorized\"}");
        return false;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.remove();
    }
}
