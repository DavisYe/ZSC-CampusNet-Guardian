package com.zsxyww.backend.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.zsxyww.backend.model.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 权限实体类
 * 用于RBAC权限控制中的具体权限定义
 *
 * @author DavisYe
 * @since 1.0.0
 */
@Data
@TableName("ww_permission")
@EqualsAndHashCode(callSuper = true)
public class Permission extends BaseEntity {
    
    /**
     * 权限名称
     */
    private String name;
    
    /**
     * 权限编码
     * 系统中使用的唯一标识，如：user:create, user:update
     */
    private String code;
    
    /**
     * 权限类型
     * 1: 菜单
     * 2: 按钮
     * 3: API接口
     */
    private Integer type;
    
    /**
     * 父权限ID
     */
    private Long parentId;
    
    /**
     * 权限路径
     * 用于前端路由或后端API路径
     */
    private String path;
    
    /**
     * 权限图标
     * 用于前端菜单显示
     */
    private String icon;
    
    /**
     * 组件路径
     * 前端组件的路径
     */
    private String component;
    
    /**
     * 排序
     */
    private Integer sort;
    
    /**
     * 是否启用
     */
    private Boolean enabled;
    
    /**
     * 权限描述
     */
    private String description;
}