package com.zsxyww.backend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zsxyww.backend.model.entity.KnowledgeArticle;
import com.zsxyww.backend.model.entity.KnowledgeCategory;

import java.util.List;

/**
 * 知识库服务接口
 *
 * @author DavisYe
 * @since 1.0.0
 */
public interface KnowledgeService {
    
    /**
     * 创建分类
     *
     * @param category 分类信息
     * @return 创建后的分类
     */
    KnowledgeCategory createCategory(KnowledgeCategory category);
    
    /**
     * 更新分类
     *
     * @param category 分类信息
     * @return 更新后的分类
     */
    KnowledgeCategory updateCategory(KnowledgeCategory category);
    
    /**
     * 删除分类
     *
     * @param categoryId 分类ID
     */
    void deleteCategory(Long categoryId);
    
    /**
     * 获取分类列表
     *
     * @return 分类列表（树形结构）
     */
    List<KnowledgeCategory> getCategoryTree();
    
    /**
     * 创建文章
     *
     * @param article 文章信息
     * @return 创建后的文章
     */
    KnowledgeArticle createArticle(KnowledgeArticle article);
    
    /**
     * 更新文章
     *
     * @param article 文章信息
     * @return 更新后的文章
     */
    KnowledgeArticle updateArticle(KnowledgeArticle article);
    
    /**
     * 删除文章
     *
     * @param articleId 文章ID
     */
    void deleteArticle(Long articleId);
    
    /**
     * 获取文章列表
     *
     * @param categoryId 分类ID
     * @param keyword 搜索关键词
     * @param page 页码
     * @param size 每页大小
     * @return 文章分页列表
     */
    IPage<KnowledgeArticle> getArticleList(Long categoryId, String keyword, Integer page, Integer size);
    
    /**
     * 获取文章详情
     *
     * @param articleId 文章ID
     * @return 文章详情
     */
    KnowledgeArticle getArticleDetail(Long articleId);
    
    /**
     * 增加文章浏览次数
     *
     * @param articleId 文章ID
     */
    void incrementViewCount(Long articleId);
    
    /**
     * 点赞文章
     *
     * @param articleId 文章ID
     */
    void likeArticle(Long articleId);
    
    /**
     * 收藏文章
     *
     * @param articleId 文章ID
     */
    void favoriteArticle(Long articleId);
    
    /**
     * 获取推荐文章
     *
     * @param limit 数量限制
     * @return 推荐文章列表
     */
    List<KnowledgeArticle> getRecommendArticles(Integer limit);
}