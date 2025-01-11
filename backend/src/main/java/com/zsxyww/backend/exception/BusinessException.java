package com.zsxyww.backend.exception;

import lombok.Getter;

/**
 * 业务异常类
 * 用于处理业务逻辑异常
 *
 * @author DavisYe
 * @since 1.0.0
 */
@Getter
public class BusinessException extends RuntimeException {
    
    /**
     * 错误码
     */
    private final Integer code;
    
    /**
     * 错误信息
     */
    private final String message;
    
    /**
     * 构造函数
     *
     * @param message 错误信息
     */
    public BusinessException(String message) {
        this(500, message);
    }
    
    /**
     * 构造函数
     *
     * @param code 错误码
     * @param message 错误信息
     */
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }
}