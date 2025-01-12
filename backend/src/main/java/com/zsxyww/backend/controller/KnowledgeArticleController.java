package com.zsxyww.backend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zsxyww.backend.common.Result;
import com.zsxyww.backend.model.entity.KnowledgeArticle;
import com.zsxyww.backend.service.KnowledgeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 知识库文章控制器
 *
 * @author DavisYe
 * @since 1.0.0
 */
@Validated
@RestController
@RequestMapping("/knowledge/articles")
@RequiredArgsConstructor
public class KnowledgeArticleController {

    private final KnowledgeService knowledgeService;

    /**
     * 创建文章
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<KnowledgeArticle> createArticle(@RequestBody @Valid KnowledgeArticle article) {
        return Result.success(knowledgeService.createArticle(article));
    }

    /**
     * 更新文章
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<KnowledgeArticle> updateArticle(@PathVariable Long id,
                                                @RequestBody @Valid KnowledgeArticle article) {
        article.setId(id);
        return Result.success(knowledgeService.updateArticle(article));
    }

    /**
     * 删除文章
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteArticle(@PathVariable Long id) {
        knowledgeService.deleteArticle(id);
        return Result.success();
    }

    /**
     * 获取文章列表
     */
    @GetMapping
    public Result<IPage<KnowledgeArticle>> getArticleList(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") @Min(1) Integer page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) Integer size) {
        return Result.success(knowledgeService.getArticleList(categoryId, keyword, page, size));
    }

    /**
     * 获取文章详情
     */
    @GetMapping("/{id}")
    public Result<KnowledgeArticle> getArticleDetail(@PathVariable Long id) {
        // 增加浏览次数
        knowledgeService.incrementViewCount(id);
        return Result.success(knowledgeService.getArticleDetail(id));
    }

    /**
     * 点赞文章
     */
    @PostMapping("/{id}/like")
    public Result<Void> likeArticle(@PathVariable Long id) {
        knowledgeService.likeArticle(id);
        return Result.success();
    }

    /**
     * 收藏文章
     */
    @PostMapping("/{id}/favorite")
    public Result<Void> favoriteArticle(@PathVariable Long id) {
        knowledgeService.favoriteArticle(id);
        return Result.success();
    }

    /**
     * 获取推荐文章
     */
    @GetMapping("/recommend")
    public Result<List<KnowledgeArticle>> getRecommendArticles(
            @RequestParam(defaultValue = "10") @Min(1) @Max(50) Integer limit) {
        return Result.success(knowledgeService.getRecommendArticles(limit));
    }
}