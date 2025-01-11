package com.zsxyww.backend.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.zsxyww.backend.model.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色实体类
 * 用于RBAC权限控制中的角色定义
 *
 * @author DavisYe
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("role")
public class Role extends BaseEntity {
    
    /**
     * 角色名称
     */
    private String name;
    
    /**
     * 角色编码
     * 系统中使用的唯一标识，如：ROLE_ADMIN, ROLE_USER
     */
    private String code;
    
    /**
     * 角色描述
     */
    private String description;
    
    /**
     * 排序
     */
    private Integer sort;
    
    /**
     * 是否启用
     */
    private Boolean enabled;
}