package com.zsxyww.backend.controller;

import com.zsxyww.backend.common.Result;
import com.zsxyww.backend.model.entity.KnowledgeCategory;
import com.zsxyww.backend.service.KnowledgeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 知识库分类控制器
 *
 * @author DavisYe
 * @since 1.0.0
 */
@RestController
@RequestMapping("/knowledge/categories")
@RequiredArgsConstructor
public class KnowledgeCategoryController {

    private final KnowledgeService knowledgeService;

    /**
     * 创建分类
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<KnowledgeCategory> createCategory(@RequestBody @Valid KnowledgeCategory category) {
        return Result.success(knowledgeService.createCategory(category));
    }

    /**
     * 更新分类
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<KnowledgeCategory> updateCategory(@PathVariable Long id,
                                                  @RequestBody @Valid KnowledgeCategory category) {
        category.setId(id);
        return Result.success(knowledgeService.updateCategory(category));
    }

    /**
     * 删除分类
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        knowledgeService.deleteCategory(id);
        return Result.success();
    }

    /**
     * 获取分类树
     */
    @GetMapping("/tree")
    public Result<List<KnowledgeCategory>> getCategoryTree() {
        return Result.success(knowledgeService.getCategoryTree());
    }
}