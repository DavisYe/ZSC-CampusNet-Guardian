package com.zsxyww.backend.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zsxyww.backend.exception.BusinessException;
import com.zsxyww.backend.mapper.KnowledgeArticleMapper;
import com.zsxyww.backend.mapper.KnowledgeCategoryMapper;
import com.zsxyww.backend.model.entity.KnowledgeArticle;
import com.zsxyww.backend.model.entity.KnowledgeCategory;
import com.zsxyww.backend.service.KnowledgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 知识库服务实现类
 *
 * @author DavisYe
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class KnowledgeServiceImpl implements KnowledgeService {

    private final KnowledgeCategoryMapper categoryMapper;
    private final KnowledgeArticleMapper articleMapper;

    @Override
    @Transactional
    public KnowledgeCategory createCategory(KnowledgeCategory category) {
        categoryMapper.insert(category);
        return category;
    }

    @Override
    @Transactional
    public KnowledgeCategory updateCategory(KnowledgeCategory category) {
        // 验证分类是否存在
        if (categoryMapper.selectById(category.getId()) == null) {
            throw new BusinessException("分类不存在");
        }
        categoryMapper.updateById(category);
        return category;
    }

    @Override
    @Transactional
    public void deleteCategory(Long categoryId) {
        // 验证分类是否存在
        if (categoryMapper.selectById(categoryId) == null) {
            throw new BusinessException("分类不存在");
        }
        
        // 验证是否有子分类
        if (categoryMapper.countChildren(categoryId) > 0) {
            throw new BusinessException("存在子分类，无法删除");
        }
        
        // 验证是否有文章
        if (categoryMapper.countArticles(categoryId) > 0) {
            throw new BusinessException("分类下存在文章，无法删除");
        }
        
        categoryMapper.deleteById(categoryId);
    }

    @Override
    public List<KnowledgeCategory> getCategoryTree() {
        // 获取所有分类
        List<KnowledgeCategory> allCategories = categoryMapper.selectList(null);
        
        // 构建父子关系
        Map<Long, List<KnowledgeCategory>> parentChildMap = allCategories.stream()
                .filter(category -> category.getParentId() != null)
                .collect(Collectors.groupingBy(KnowledgeCategory::getParentId));
        
        // 获取根分类
        return allCategories.stream()
                .filter(category -> category.getParentId() == null)
                .peek(category -> category.setChildren(buildCategoryTree(category.getId(), parentChildMap)))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public KnowledgeArticle createArticle(KnowledgeArticle article) {
        // 验证分类是否存在
        if (categoryMapper.selectById(article.getCategoryId()) == null) {
            throw new BusinessException("分类不存在");
        }
        
        articleMapper.insert(article);
        return article;
    }

    @Override
    @Transactional
    public KnowledgeArticle updateArticle(KnowledgeArticle article) {
        // 验证文章是否存在
        if (articleMapper.selectById(article.getId()) == null) {
            throw new BusinessException("文章不存在");
        }
        
        // 验证分类是否存在
        if (categoryMapper.selectById(article.getCategoryId()) == null) {
            throw new BusinessException("分类不存在");
        }
        
        articleMapper.updateById(article);
        return article;
    }

    @Override
    @Transactional
    public void deleteArticle(Long articleId) {
        // 验证文章是否存在
        if (articleMapper.selectById(articleId) == null) {
            throw new BusinessException("文章不存在");
        }
        
        articleMapper.deleteById(articleId);
    }

    @Override
    public IPage<KnowledgeArticle> getArticleList(Long categoryId, String keyword, Integer page, Integer size) {
        return articleMapper.selectArticleList(new Page<>(page, size), categoryId, keyword);
    }

    @Override
    public KnowledgeArticle getArticleDetail(Long articleId) {
        KnowledgeArticle article = articleMapper.selectById(articleId);
        if (article == null) {
            throw new BusinessException("文章不存在");
        }
        return article;
    }

    @Override
    @Transactional
    public void incrementViewCount(Long articleId) {
        articleMapper.incrementViewCount(articleId);
    }

    @Override
    @Transactional
    public void likeArticle(Long articleId) {
        articleMapper.incrementLikeCount(articleId);
    }

    @Override
    @Transactional
    public void favoriteArticle(Long articleId) {
        articleMapper.incrementFavoriteCount(articleId);
    }

    @Override
    public List<KnowledgeArticle> getRecommendArticles(Integer limit) {
        return articleMapper.selectRecommendArticles(limit);
    }

    /**
     * 递归构建分类树
     */
    private List<KnowledgeCategory> buildCategoryTree(Long parentId, Map<Long, List<KnowledgeCategory>> parentChildMap) {
        List<KnowledgeCategory> children = parentChildMap.get(parentId);
        if (children == null) {
            return new ArrayList<>();
        }
        
        children.forEach(child -> child.setChildren(buildCategoryTree(child.getId(), parentChildMap)));
        return children;
    }
}