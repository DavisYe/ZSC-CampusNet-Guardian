package com.zsxyww.backend.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.zsxyww.backend.model.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户实体类
 * 包含用户基本信息和账户状态信息
 *
 * @author DavisYe
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ww_user")
public class User extends BaseEntity {
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 密码（加密存储）
     */
    private String password;
    
    /**
     * 手机号
     */
    private String phone;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 真实姓名
     */
    private String realName;
    
    /**
     * 学号
     */
    private String studentId;
    
    /**
     * 头像URL
     */
    private String avatar;
    
    /**
     * 账户是否启用
     */
    private Boolean enabled;
    
    /**
     * 账户是否未过期
     */
    private Boolean accountNonExpired;
    
    /**
     * 账户是否未锁定
     */
    private Boolean accountNonLocked;
    
    /**
     * 凭证是否未过期
     */
    private Boolean credentialsNonExpired;
    
    /**
     * 最后登录IP
     */
    private String lastLoginIp;
    
    /**
     * 登录次数
     */
    private Integer loginCount;
}