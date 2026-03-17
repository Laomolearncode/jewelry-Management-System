package com.jewelry.pims.security;

import com.jewelry.pims.domain.SystemEntities;
import com.jewelry.pims.mapper.SystemMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class OperateLogInterceptor implements HandlerInterceptor {

    private static final String START_TIME = "START_TIME";

    private final SystemMapper systemMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        request.setAttribute(START_TIME, System.currentTimeMillis());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return;
        }
        Long startTime = (Long) request.getAttribute(START_TIME);
        SystemEntities.AuthUser user = (SystemEntities.AuthUser) request.getAttribute(SecurityConstants.CURRENT_USER);
        Permission permission = handlerMethod.getMethodAnnotation(Permission.class);
        SystemEntities.OperateLog log = new SystemEntities.OperateLog();
        log.setUserId(user == null ? null : user.getId());
        log.setUsername(user == null ? "anonymous" : user.getUsername());
        log.setMethod(request.getMethod());
        log.setPath(request.getRequestURI());
        log.setPermissionCode(permission == null ? null : permission.value());
        log.setStatusCode(response.getStatus());
        log.setDurationMs(startTime == null ? 0L : System.currentTimeMillis() - startTime);
        log.setRequestBody(request.getQueryString());
        log.setResponseBody(ex == null ? "OK" : ex.getMessage());
        log.setIp(request.getRemoteAddr());
        systemMapper.insertOperateLog(log);
    }
}
