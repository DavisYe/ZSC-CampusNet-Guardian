package com.zsxyww.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

/**
 * 环境配置类
 * 用于加载环境变量配置
 *
 * @author DavisYe
 * @since 1.0.0
 * @deprecated 使用 {@link JwtConfig} 替代
 */
@Deprecated
@Data
@Configuration
@ConfigurationProperties(prefix = "security.jwt")
public class EnvConfig {
    @NotBlank(message = "JWT secret不能为空")
    private String secret = "defaultSecretKey";
    
    @Positive(message = "过期时间必须为正数")
    private long expiration = 86400;
}