package com.jewelry.pims.controller.auth;

import com.jewelry.pims.common.ApiResponse;
import com.jewelry.pims.dto.auth.AuthDtos;
import com.jewelry.pims.security.SecurityConstants;
import com.jewelry.pims.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
/**
 * 认证相关接口。
 */
public class AuthController {

    private final AuthService authService;

    /**
     * 用户登录并返回访问令牌。
     */
    @PostMapping("/login")
    public ApiResponse<AuthDtos.LoginResponse> login(@Valid @RequestBody AuthDtos.LoginRequest request) {
        return ApiResponse.ok("登录成功", authService.login(request));
    }

    /**
     * 用户退出登录并注销当前令牌。
     */
    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestHeader(SecurityConstants.AUTH_HEADER) String authorization) {
        authService.logout(authorization.substring(SecurityConstants.BEARER_PREFIX.length()));
        return ApiResponse.ok("退出成功", null);
    }

    /**
     * 查询当前登录用户的基本信息和权限。
     */
    @GetMapping("/profile")
    public ApiResponse<AuthDtos.ProfileResponse> profile() {
        return ApiResponse.ok(authService.profile());
    }
}
