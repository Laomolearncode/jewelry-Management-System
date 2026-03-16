package com.jewelry.pims.domain;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

public final class SystemEntities {

    private SystemEntities() {
    }

    @Data
    public static class User {
        private Long id;
        private String username;
        private String password;
        private String realName;
        private String phone;
        private Integer status;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data
    public static class Role {
        private Long id;
        private String roleCode;
        private String roleName;
        private String description;
        private Integer status;
    }

    @Data
    public static class Permission {
        private Long id;
        private String permissionCode;
        private String permissionName;
        private String description;
    }

    @Data
    public static class UserToken {
        private Long id;
        private Long userId;
        private String token;
        private LocalDateTime expireAt;
        private LocalDateTime createdAt;
    }

    @Data
    public static class OperateLog {
        private Long id;
        private Long userId;
        private String username;
        private String method;
        private String path;
        private String permissionCode;
        private Integer statusCode;
        private Long durationMs;
        private String requestBody;
        private String responseBody;
        private String ip;
        private LocalDateTime createdAt;
    }

    @Data
    public static class AuthUser {
        private Long id;
        private String username;
        private String realName;
        private List<String> roles;
        private List<String> permissions;
    }
}
