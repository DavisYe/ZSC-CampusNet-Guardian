package com.zsxyww.backend.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.zsxyww.backend.model.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 知识库文章实体类
 * 用于存储知识库的具体内容
 *
 * @author DavisYe
 * @since 1.0.0
 */
@Data
@TableName("ww_knowledge_article")
@EqualsAndHashCode(callSuper = true)
public class KnowledgeArticle extends BaseEntity {
    
    /**
     * 文章标题
     */
    private String title;
    
    /**
     * 文章内容
     * 支持Markdown格式
     */
    private String content;
    
    /**
     * 分类ID
     */
    private Long categoryId;
    
    /**
     * 作者ID
     */
    private Long authorId;
    
    /**
     * 标签
     * 多个标签用逗号分隔
     */
    private String tags;
    
    /**
     * 关键词
     * 多个关键词用逗号分隔，用于搜索
     */
    private String keywords;
    
    /**
     * 浏览次数
     */
    private Integer viewCount;
    
    /**
     * 点赞次数
     */
    private Integer likeCount;
    
    /**
     * 收藏次数
     */
    private Integer favoriteCount;
    
    /**
     * 是否置顶
     */
    private Boolean isTop;
    
    /**
     * 是否推荐
     */
    private Boolean isRecommend;
    
    /**
     * 排序
     */
    private Integer sort;
    
    /**
     * 状态
     * 0: 草稿
     * 1: 已发布
     * 2: 已下架
     */
    private Integer status;
    
    /**
     * 发布时间
     */
    private java.time.LocalDateTime publishTime;
    
    /**
     * 是否允许评论
     */
    private Boolean allowComment;
}