package com.jewelry.pims.dto.system;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

public final class SystemDtos {

    private SystemDtos() {
    }

    @Data
    public static class UserCreateRequest {
        @NotBlank(message = "用户名不能为空")
        private String username;
        @NotBlank(message = "密码不能为空")
        private String password;
        @NotBlank(message = "姓名不能为空")
        private String realName;
        private String phone;
        @NotEmpty(message = "角色不能为空")
        private List<Long> roleIds;
    }

    @Data
    public static class UserStatusUpdateRequest {
        private Integer status;
    }

    @Data
    public static class UserView {
        private Long id;
        private String username;
        private String realName;
        private String phone;
        private Integer status;
        private LocalDateTime createdAt;
        private List<String> roles;
    }

    @Data
    public static class RoleView {
        private Long id;
        private String roleCode;
        private String roleName;
        private String description;
    }

    @Data
    public static class PermissionView {
        private Long id;
        private String permissionCode;
        private String permissionName;
        private String description;
    }
}
