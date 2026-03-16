package com.jewelry.pims.service.impl;

import com.jewelry.pims.common.BusinessException;
import com.jewelry.pims.domain.SystemEntities;
import com.jewelry.pims.dto.system.SystemDtos;
import com.jewelry.pims.mapper.SystemMapper;
import com.jewelry.pims.security.PasswordUtil;
import com.jewelry.pims.service.SystemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SystemServiceImpl implements SystemService {

    private final SystemMapper systemMapper;

    @Override
    @Transactional
    public SystemDtos.UserView createUser(SystemDtos.UserCreateRequest request) {
        if (systemMapper.countUserByUsername(request.getUsername()) > 0) {
            throw new BusinessException("用户名已存在");
        }
        SystemEntities.User user = new SystemEntities.User();
        user.setUsername(request.getUsername());
        user.setPassword(PasswordUtil.encode(request.getPassword()));
        user.setRealName(request.getRealName());
        user.setPhone(request.getPhone());
        user.setStatus(1);
        systemMapper.insertUser(user);
        for (Long roleId : request.getRoleIds()) {
            systemMapper.insertUserRole(user.getId(), roleId);
        }
        return toView(systemMapper.findUserById(user.getId()));
    }

    @Override
    public List<SystemDtos.UserView> listUsers() {
        List<SystemDtos.UserView> result = new ArrayList<>();
        for (SystemEntities.User user : systemMapper.listUsers()) {
            result.add(toView(user));
        }
        return result;
    }

    @Override
    public void updateUserStatus(Long id, Integer status) {
        if (systemMapper.updateUserStatus(id, status) == 0) {
            throw new BusinessException("用户不存在");
        }
    }

    @Override
    public List<SystemEntities.Role> listRoles() {
        return systemMapper.listRoles();
    }

    @Override
    public List<SystemEntities.Permission> listPermissions() {
        return systemMapper.listPermissions();
    }

    @Override
    public List<SystemEntities.OperateLog> listLogs() {
        return systemMapper.listOperateLogs();
    }

    private SystemDtos.UserView toView(SystemEntities.User user) {
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        SystemDtos.UserView view = new SystemDtos.UserView();
        view.setId(user.getId());
        view.setUsername(user.getUsername());
        view.setRealName(user.getRealName());
        view.setPhone(user.getPhone());
        view.setStatus(user.getStatus());
        view.setCreatedAt(user.getCreatedAt());
        view.setRoles(systemMapper.listRoleCodesByUserId(user.getId()));
        return view;
    }
}
