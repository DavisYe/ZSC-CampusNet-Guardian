package com.zsxyww.backend.controller;

import com.zsxyww.backend.common.Result;
import com.zsxyww.backend.model.dto.AuthResponse;
import com.zsxyww.backend.model.dto.BatchCreateUserRequest;
import com.zsxyww.backend.model.dto.LoginRequest;
import com.zsxyww.backend.model.dto.RegisterRequest;
import com.zsxyww.backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 认证控制器
 *
 * @author DavisYe
 * @since 1.0.0
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<AuthResponse> login(@RequestBody @Valid LoginRequest request) {
        return Result.success(authService.login(request));
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<AuthResponse> register(@RequestBody @Valid RegisterRequest request) {
        return Result.success(authService.register(request));
    }

    /**
     * 刷新token
     */
    @PostMapping("/refresh")
    public Result<AuthResponse> refreshToken(@RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return Result.success(authService.refreshToken(token));
    }

    /**
     * 退出登录
     */
    @PostMapping("/logout")
    public Result<Void> logout(@RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        authService.logout(token);
        return Result.success();
    }

    /**
     * 批量创建用户
     * 仅管理员可用
     * 用户名为学号，初始密码为学号后6位
     */
    @PostMapping("/batch-create")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Integer> batchCreateUsers(@RequestBody @Valid List<BatchCreateUserRequest> requests) {
        return Result.success(authService.batchCreateUsers(requests));
    }
}