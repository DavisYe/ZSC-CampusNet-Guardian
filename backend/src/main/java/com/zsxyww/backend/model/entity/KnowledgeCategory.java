package com.zsxyww.backend.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.zsxyww.backend.model.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 知识库分类实体类
 * 用于对知识库文章进行分类管理
 *
 * @author DavisYe
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("knowledge_category")
public class KnowledgeCategory extends BaseEntity {
    
    /**
     * 分类名称
     */
    private String name;
    
    /**
     * 分类编码
     * 系统中使用的唯一标识
     */
    private String code;
    
    /**
     * 父分类ID
     */
    private Long parentId;
    
    /**
     * 分类描述
     */
    private String description;
    
    /**
     * 排序
     */
    private Integer sort;
    
    /**
     * 图标
     */
    private String icon;
    
    /**
     * 是否启用
     */
    private Boolean enabled;
}