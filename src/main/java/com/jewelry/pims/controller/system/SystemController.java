package com.jewelry.pims.controller.system;

import com.jewelry.pims.common.ApiResponse;
import com.jewelry.pims.domain.SystemEntities;
import com.jewelry.pims.dto.system.SystemDtos;
import com.jewelry.pims.security.Permission;
import com.jewelry.pims.service.SystemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/system")
@RequiredArgsConstructor
/**
 * 系统管理接口。
 */
public class SystemController {

    private final SystemService systemService;

    /**
     * 创建系统用户并绑定角色。
     */
    @PostMapping("/users")
    @Permission("system:user:create")
    public ApiResponse<SystemDtos.UserView> createUser(@Valid @RequestBody SystemDtos.UserCreateRequest request) {
        return ApiResponse.ok("创建成功", systemService.createUser(request));
    }

    /**
     * 查询系统用户列表。
     */
    @GetMapping("/users")
    @Permission("system:user:view")
    public ApiResponse<List<SystemDtos.UserView>> listUsers() {
        return ApiResponse.ok(systemService.listUsers());
    }

    /**
     * 修改指定用户的启用状态。
     */
    @PutMapping("/users/{id}/status")
    @Permission("system:user:update")
    public ApiResponse<Void> updateUserStatus(@PathVariable Long id, @RequestBody SystemDtos.UserStatusUpdateRequest request) {
        systemService.updateUserStatus(id, request.getStatus());
        return ApiResponse.ok("状态更新成功", null);
    }

    /**
     * 查询系统角色列表。
     */
    @GetMapping("/roles")
    @Permission("system:role:view")
    public ApiResponse<List<SystemEntities.Role>> listRoles() {
        return ApiResponse.ok(systemService.listRoles());
    }

    /**
     * 查询权限点列表。
     */
    @GetMapping("/permissions")
    @Permission("system:permission:view")
    public ApiResponse<List<SystemEntities.Permission>> listPermissions() {
        return ApiResponse.ok(systemService.listPermissions());
    }

    /**
     * 查询最近的操作日志记录。
     */
    @GetMapping("/logs")
    @Permission("system:log:view")
    public ApiResponse<List<SystemEntities.OperateLog>> listLogs() {
        return ApiResponse.ok(systemService.listLogs());
    }
}
