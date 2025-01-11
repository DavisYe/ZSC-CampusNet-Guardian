package com.zsxyww.backend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 认证响应DTO
 * 包含token和用户基本信息
 *
 * @author DavisYe
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    
    /**
     * 访问令牌
     */
    private String token;
    
    /**
     * token类型
     */
    private String tokenType;
    
    /**
     * 过期时间（毫秒）
     */
    private Long expiresIn;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 学号
     */
    private String studentId;
    
    /**
     * 真实姓名
     */
    private String realName;
    
    /**
     * 头像URL
     */
    private String avatar;
    
    /**
     * 角色列表
     */
    private List<String> roles;
    
    /**
     * 权限列表
     */
    private List<String> permissions;
}