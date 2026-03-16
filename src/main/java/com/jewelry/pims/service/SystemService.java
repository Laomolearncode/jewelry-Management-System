package com.jewelry.pims.service;

import com.jewelry.pims.domain.SystemEntities;
import com.jewelry.pims.dto.system.SystemDtos;

import java.util.List;

public interface SystemService {

    SystemDtos.UserView createUser(SystemDtos.UserCreateRequest request);

    List<SystemDtos.UserView> listUsers();

    void updateUserStatus(Long id, Integer status);

    List<SystemEntities.Role> listRoles();

    List<SystemEntities.Permission> listPermissions();

    List<SystemEntities.OperateLog> listLogs();
}
