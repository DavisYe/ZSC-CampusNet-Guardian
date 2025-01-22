package com.zsxyww.backend.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.zsxyww.backend.model.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户-角色关联实体类
 * 用于实现用户和角色的多对多关系
 *
 * @author DavisYe
 * @since 1.0.0
 */
@Data
@TableName("ww_user_role")
@EqualsAndHashCode(callSuper = true)
public class UserRole extends BaseEntity {
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 角色ID
     */
    private Long roleId;
}