package com.example.clubfund.config;

import cn.hutool.core.util.StrUtil;
import com.example.clubfund.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.example.clubfund.utils.UserContext;
import com.example.clubfund.entity.SysUser;
import com.example.clubfund.service.ISysUserService;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private ISysUserService userService; // 用于根据username查id

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Authorization");

        if ("OPTIONS".equals(request.getMethod())) return true;

        if (StrUtil.isBlank(token) || jwtUtils.isTokenExpired(token)) {
            response.setStatus(401);
            return false;
        }

        // --- 新增逻辑开始 ---
        String username = jwtUtils.getUsernameFromToken(token);
        if (username != null) {
            SysUser user = userService.getByUsername(username);
            if (user != null) {
                UserContext.setUserId(user.getId()); // 存入 ThreadLocal
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 极其重要：请求结束必须清理，防止内存泄漏或线程复用导致的数据混乱
        UserContext.remove();
    }
}