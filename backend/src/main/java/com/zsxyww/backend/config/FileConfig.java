package com.zsxyww.backend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 文件配置类
 *
 * @author DavisYe
 * @since 1.0.0
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "upload")
public class FileConfig {
    
    /**
     * 文件上传路径
     */
    private String path;
    
    /**
     * 文件大小限制
     */
    private String maxSize;
    
    /**
     * 允许上传的文件类型
     */
    private String[] allowedTypes = {
        "image/jpeg",
        "image/png",
        "image/gif",
        "image/bmp",
        "image/webp"
    };
    
    /**
     * 文件访问URL前缀
     */
    private String urlPrefix = "/uploads/";
    
    /**
     * 获取完整的文件上传路径
     */
    public String getFullPath() {
        if (path.startsWith("./")) {
            return System.getProperty("user.dir") + path.substring(1);
        }
        return path;
    }
}