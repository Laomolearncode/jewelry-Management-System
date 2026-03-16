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
public class SystemController {

    private final SystemService systemService;

    @PostMapping("/users")
    @Permission("system:user:create")
    public ApiResponse<SystemDtos.UserView> createUser(@Valid @RequestBody SystemDtos.UserCreateRequest request) {
        return ApiResponse.ok("创建成功", systemService.createUser(request));
    }

    @GetMapping("/users")
    @Permission("system:user:view")
    public ApiResponse<List<SystemDtos.UserView>> listUsers() {
        return ApiResponse.ok(systemService.listUsers());
    }

    @PutMapping("/users/{id}/status")
    @Permission("system:user:update")
    public ApiResponse<Void> updateUserStatus(@PathVariable Long id, @RequestBody SystemDtos.UserStatusUpdateRequest request) {
        systemService.updateUserStatus(id, request.getStatus());
        return ApiResponse.ok("状态更新成功", null);
    }

    @GetMapping("/roles")
    @Permission("system:role:view")
    public ApiResponse<List<SystemEntities.Role>> listRoles() {
        return ApiResponse.ok(systemService.listRoles());
    }

    @GetMapping("/permissions")
    @Permission("system:permission:view")
    public ApiResponse<List<SystemEntities.Permission>> listPermissions() {
        return ApiResponse.ok(systemService.listPermissions());
    }

    @GetMapping("/logs")
    @Permission("system:log:view")
    public ApiResponse<List<SystemEntities.OperateLog>> listLogs() {
        return ApiResponse.ok(systemService.listLogs());
    }
}
