package com.zsxyww.backend.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 登录请求DTO
 *
 * @author DavisYe
 * @since 1.0.0
 */
@Data
public class LoginRequest {
    
    /**
     * 用户名或学号
     */
    @NotBlank(message = "用户名/学号不能为空")
    private String username;
    
    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;
}