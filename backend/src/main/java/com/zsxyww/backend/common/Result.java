package com.zsxyww.backend.common;

import lombok.Data;
import java.io.Serializable;

/**
 * 通用响应结果类
 * 用于统一接口返回格式
 *
 * @author DavisYe
 * @since 1.0.0
 */
@Data
public class Result<T> implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 状态码
     */
    private Integer code;
    
    /**
     * 返回信息
     */
    private String message;
    
    /**
     * 返回数据
     */
    private T data;
    
    /**
     * 成功标记
     */
    private Boolean success;
    
    /**
     * 时间戳
     */
    private Long timestamp;
    
    /**
     * 私有构造函数
     */
    private Result() {
        this.timestamp = System.currentTimeMillis();
    }
    
    /**
     * 成功返回结果
     */
    public static <T> Result<T> success() {
        return success(null);
    }
    
    /**
     * 成功返回结果
     *
     * @param data 获取的数据
     */
    public static <T> Result<T> success(T data) {
        return success(data, "操作成功");
    }
    
    /**
     * 成功返回结果
     *
     * @param data 获取的数据
     * @param message 提示信息
     */
    public static <T> Result<T> success(T data, String message) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setData(data);
        result.setMessage(message);
        result.setSuccess(true);
        return result;
    }
    
    /**
     * 失败返回结果
     */
    public static <T> Result<T> failed() {
        return failed("操作失败");
    }
    
    /**
     * 失败返回结果
     *
     * @param message 提示信息
     */
    public static <T> Result<T> failed(String message) {
        Result<T> result = new Result<>();
        result.setCode(500);
        result.setMessage(message);
        result.setSuccess(false);
        return result;
    }
    
    /**
     * 失败返回结果
     *
     * @param code 状态码
     * @param message 提示信息
     */
    public static <T> Result<T> failed(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        result.setSuccess(false);
        return result;
    }
    
    /**
     * 参数验证失败返回结果
     */
    public static <T> Result<T> validateFailed() {
        return validateFailed("参数验证失败");
    }
    
    /**
     * 参数验证失败返回结果
     *
     * @param message 提示信息
     */
    public static <T> Result<T> validateFailed(String message) {
        Result<T> result = new Result<>();
        result.setCode(400);
        result.setMessage(message);
        result.setSuccess(false);
        return result;
    }
    
    /**
     * 未登录返回结果
     */
    public static <T> Result<T> unauthorized() {
        Result<T> result = new Result<>();
        result.setCode(401);
        result.setMessage("暂未登录或token已过期");
        result.setSuccess(false);
        return result;
    }
    
    /**
     * 未授权返回结果
     */
    public static <T> Result<T> forbidden() {
        Result<T> result = new Result<>();
        result.setCode(403);
        result.setMessage("没有相关权限");
        result.setSuccess(false);
        return result;
    }
}