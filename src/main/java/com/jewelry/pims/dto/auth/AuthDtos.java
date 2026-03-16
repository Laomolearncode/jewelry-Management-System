package com.jewelry.pims.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

public final class AuthDtos {

    private AuthDtos() {
    }

    @Data
    public static class LoginRequest {
        @NotBlank(message = "用户名不能为空")
        private String username;

        @NotBlank(message = "密码不能为空")
        private String password;
    }

    @Data
    public static class LoginResponse {
        private String token;
        private Long expireHours;
    }

    @Data
    public static class ProfileResponse {
        private Long id;
        private String username;
        private String realName;
        private List<String> roles;
        private List<String> permissions;
    }
}
