package com.zsxyww.backend.service;

import com.zsxyww.backend.model.dto.AuthResponse;
import com.zsxyww.backend.model.dto.BatchCreateUserRequest;
import com.zsxyww.backend.model.dto.LoginRequest;
import com.zsxyww.backend.model.dto.RegisterRequest;

import java.util.List;

/**
 * 认证服务接口
 *
 * @author DavisYe
 * @since 1.0.0
 */
public interface AuthService {
    
    /**
     * 用户登录
     *
     * @param request 登录请求
     * @return 认证响应
     */
    AuthResponse login(LoginRequest request);
    
    /**
     * 用户注册
     *
     * @param request 注册请求
     * @return 认证响应
     */
    AuthResponse register(RegisterRequest request);
    
    /**
     * 刷新token
     *
     * @param oldToken 旧token
     * @return 新的认证响应
     */
    AuthResponse refreshToken(String oldToken);
    
    /**
     * 退出登录
     *
     * @param token 当前token
     */
    void logout(String token);

    /**
     * 批量创建用户
     * 用户名为学号，初始密码为学号后6位
     *
     * @param requests 批量创建用户请求列表
     * @return 创建成功的用户数量
     */
    int batchCreateUsers(List<BatchCreateUserRequest> requests);
}