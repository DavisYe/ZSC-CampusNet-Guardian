package com.zsxyww.backend.controller;

import com.zsxyww.backend.common.Result;
import com.zsxyww.backend.model.dto.AuthResponse;
import com.zsxyww.backend.model.dto.LoginRequest;
import com.zsxyww.backend.model.dto.RegisterRequest;
import com.zsxyww.backend.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * AuthController的单元测试类
 *
 * <p>测试范围：
 * <ul>
 *   <li>测试用户注册功能</li>
 *   <li>测试用户登录功能</li>
 *   <li>验证认证异常处理</li>
 * </ul>
 *
 * <p>使用的测试框架：
 * <ul>
 *   <li>JUnit 5 - 用于编写测试用例</li>
 *   <li>Mockito - 用于模拟依赖对象</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private RegisterRequest validRegisterRequest;
    private LoginRequest validLoginRequest;
    private AuthResponse authResponse;

    @BeforeEach
    void setUp() {
        // 初始化有效的注册请求
        validRegisterRequest = new RegisterRequest();
        validRegisterRequest.setUsername("testuser");  // 设置用户名
        validRegisterRequest.setPassword("Password123");  // 设置密码
        validRegisterRequest.setConfirmPassword("Password123");  // 设置确认密码
        validRegisterRequest.setStudentId("20230001");  // 设置学号
        validRegisterRequest.setRealName("Test User");  // 设置真实姓名
        validRegisterRequest.setPhone("13800138000");  // 设置手机号
        validRegisterRequest.setEmail("test@example.com");  // 设置邮箱

        // 初始化有效的登录请求
        validLoginRequest = new LoginRequest();
        validLoginRequest.setUsername("testuser");  // 设置登录用户名
        validLoginRequest.setPassword("Password123");  // 设置登录密码

        // 初始化认证响应
        authResponse = AuthResponse.builder()
            .token("mockToken")  // 模拟token
            .tokenType("Bearer")  // token类型
            .expiresIn(3600000L)  // token有效期
            .userId(1L)  // 用户ID
            .username("testuser")  // 用户名
            .studentId("20230001")  // 学号
            .realName("Test User")  // 真实姓名
            .build();
    }

    @Test
    void register_ValidRequest_ReturnsSuccess() {
        // Arrange - 准备测试数据
        // 模拟authService.register方法返回预定义的authResponse
        when(authService.register(any(RegisterRequest.class))).thenReturn(authResponse);

        // Act - 调用被测试方法
        Result<AuthResponse> result = authController.register(validRegisterRequest);

        // Assert - 验证结果
        assertTrue(result.getSuccess(), "注册请求应该成功");
        assertEquals(authResponse, result.getData(), "返回的认证响应应该匹配预期值");
        // 验证service方法被正确调用
        verify(authService, times(1)).register(validRegisterRequest);
    }

    @Test
    void login_ValidCredentials_ReturnsSuccess() {
        // Arrange - 准备测试数据
        // 模拟authService.login方法返回预定义的authResponse
        when(authService.login(any(LoginRequest.class))).thenReturn(authResponse);

        // Act - 调用被测试方法
        Result<AuthResponse> result = authController.login(validLoginRequest);

        // Assert - 验证结果
        assertTrue(result.getSuccess(), "使用有效凭证登录应该成功");
        assertEquals(authResponse, result.getData(), "返回的认证响应应该匹配预期值");
        // 验证service方法被正确调用
        verify(authService, times(1)).login(validLoginRequest);
    }

    @Test
    void login_InvalidCredentials_ThrowsException() {
        // Arrange - 准备测试数据
        // 模拟authService.login方法抛出BadCredentialsException
        when(authService.login(any(LoginRequest.class)))
            .thenThrow(new BadCredentialsException("Invalid credentials"));

        // Act & Assert - 调用被测试方法并验证异常
        assertThrows(BadCredentialsException.class, () -> {
            authController.login(validLoginRequest);
        }, "使用无效凭证登录应该抛出BadCredentialsException");

        // 验证service方法被正确调用
        verify(authService, times(1)).login(validLoginRequest);
    }
}