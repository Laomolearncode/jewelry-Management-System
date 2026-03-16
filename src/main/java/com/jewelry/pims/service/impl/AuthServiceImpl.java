package com.jewelry.pims.service.impl;

import com.jewelry.pims.common.BusinessException;
import com.jewelry.pims.domain.SystemEntities;
import com.jewelry.pims.dto.auth.AuthDtos;
import com.jewelry.pims.mapper.SystemMapper;
import com.jewelry.pims.security.AuthContext;
import com.jewelry.pims.security.PasswordUtil;
import com.jewelry.pims.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final SystemMapper systemMapper;

    @Value("${app.auth.token-expire-hours}")
    private Long tokenExpireHours;

    @Override
    public AuthDtos.LoginResponse login(AuthDtos.LoginRequest request) {
        SystemEntities.User user = systemMapper.findUserByUsername(request.getUsername());
        if (user == null || !PasswordUtil.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new BusinessException("用户已被禁用");
        }
        systemMapper.deleteTokensByUserId(user.getId());
        SystemEntities.UserToken token = new SystemEntities.UserToken();
        token.setUserId(user.getId());
        token.setToken(UUID.randomUUID().toString().replace("-", ""));
        token.setExpireAt(LocalDateTime.now().plusHours(tokenExpireHours));
        systemMapper.insertToken(token);

        AuthDtos.LoginResponse response = new AuthDtos.LoginResponse();
        response.setToken(token.getToken());
        response.setExpireHours(tokenExpireHours);
        return response;
    }

    @Override
    public void logout(String token) {
        systemMapper.deleteToken(token);
    }

    @Override
    public AuthDtos.ProfileResponse profile() {
        SystemEntities.AuthUser authUser = AuthContext.get();
        if (authUser == null) {
            throw new BusinessException("未登录");
        }
        AuthDtos.ProfileResponse response = new AuthDtos.ProfileResponse();
        BeanUtils.copyProperties(authUser, response);
        return response;
    }

    @Override
    public SystemEntities.AuthUser parseToken(String token) {
        SystemEntities.UserToken userToken = systemMapper.findValidToken(token);
        if (userToken == null) {
            return null;
        }
        SystemEntities.User user = systemMapper.findUserById(userToken.getUserId());
        if (user == null || user.getStatus() == null || user.getStatus() != 1) {
            return null;
        }
        SystemEntities.AuthUser authUser = new SystemEntities.AuthUser();
        authUser.setId(user.getId());
        authUser.setUsername(user.getUsername());
        authUser.setRealName(user.getRealName());
        authUser.setRoles(systemMapper.listRoleCodesByUserId(user.getId()));
        authUser.setPermissions(systemMapper.listPermissionCodesByUserId(user.getId()));
        return authUser;
    }
}
