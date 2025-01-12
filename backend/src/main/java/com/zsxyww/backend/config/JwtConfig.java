package com.zsxyww.backend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * JWT配置类
 * 用于配置JWT的相关参数
 *
 * @author DavisYe
 * @since 1.0.0
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "spring.security.jwt")
public class JwtConfig {
    
    /**
     * JWT密钥
     */
    private String secret;
    
    /**
     * token有效期（毫秒）
     */
    private Long expiration;
    
    /**
     * token前缀
     */
    private String tokenPrefix = "Bearer ";
    
    /**
     * token在header中的key
     */
    private String header = "Authorization";
    
    /**
     * token在请求参数中的key
     */
    private String parameterName = "token";
    
    /**
     * 不需要认证的路径
     */
    private String[] ignoreUrls = {
        "/auth/login",
        "/auth/register",
        "/auth/captcha",
        "/knowledge/articles/list",
        "/knowledge/articles/detail/**",
        "/knowledge/categories/tree",
        "/uploads/**",
        "/swagger-ui/**",
        "/swagger-ui",
        "/v3/api-docs/**",
        "/v3/api-docs",
        "/doc.html",
        "/webjars/**",
        "swagger-resources",
        "swagger-resources/**"
    };
}