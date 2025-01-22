package com.zsxyww.backend.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 批量创建用户请求DTO
 *
 * @author DavisYe
 * @since 1.0.0
 */
@Data
public class BatchCreateUserRequest {
    
    /**
     * 用户名
     */
    @NotNull(message = "用户名不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9_]{4,16}$", message = "用户名必须是4-16位字母、数字或下划线")
    private String username;
    
    /**
     * 学号
     */
    @NotNull(message = "学号不能为空")
    @Pattern(regexp = "^\\d{8,12}$", message = "请输入正确的学号")
    private String studentId;
}