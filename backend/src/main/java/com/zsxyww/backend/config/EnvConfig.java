package com.zsxyww.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Data
@Configuration
@ConfigurationProperties(prefix = "security.jwt")
public class EnvConfig {
    @NotBlank(message = "JWT secret不能为空")
    private String secret = "defaultSecretKey";
    
    @Positive(message = "过期时间必须为正数")
    private long expiration = 86400;
} 