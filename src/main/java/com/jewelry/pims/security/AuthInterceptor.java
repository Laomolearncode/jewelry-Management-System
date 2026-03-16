package com.jewelry.pims.security;

import com.jewelry.pims.common.BusinessException;
import com.jewelry.pims.domain.SystemEntities;
import com.jewelry.pims.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final AuthService authService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        String path = request.getRequestURI();
        if (path.startsWith("/api/auth/login")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-ui.html")) {
            return true;
        }
        String header = request.getHeader(SecurityConstants.AUTH_HEADER);
        if (header == null || !header.startsWith(SecurityConstants.BEARER_PREFIX)) {
            throw new BusinessException("未登录或令牌缺失");
        }
        String token = header.substring(SecurityConstants.BEARER_PREFIX.length());
        SystemEntities.AuthUser authUser = authService.parseToken(token);
        if (authUser == null) {
            throw new BusinessException("登录已失效");
        }
        AuthContext.set(authUser);
        request.setAttribute(SecurityConstants.CURRENT_USER, authUser);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        AuthContext.clear();
    }
}
