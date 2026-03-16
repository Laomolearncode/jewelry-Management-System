package com.jewelry.pims.security;

import com.jewelry.pims.common.BusinessException;
import com.jewelry.pims.domain.SystemEntities;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class PermissionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }
        Permission permission = handlerMethod.getMethodAnnotation(Permission.class);
        if (permission == null) {
            return true;
        }
        SystemEntities.AuthUser authUser = (SystemEntities.AuthUser) request.getAttribute(SecurityConstants.CURRENT_USER);
        if (authUser == null) {
            throw new BusinessException("未登录");
        }
        if (authUser.getPermissions() == null || !authUser.getPermissions().contains(permission.value())) {
            throw new BusinessException("无权访问该接口");
        }
        return true;
    }
}
