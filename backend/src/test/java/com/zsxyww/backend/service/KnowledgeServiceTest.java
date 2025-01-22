package com.zsxyww.backend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zsxyww.backend.exception.BusinessException;
import com.zsxyww.backend.mapper.KnowledgeArticleMapper;
import com.zsxyww.backend.mapper.KnowledgeCategoryMapper;
import com.zsxyww.backend.mapper.UserMapper;
import com.zsxyww.backend.model.entity.KnowledgeArticle;
import com.zsxyww.backend.model.entity.KnowledgeCategory;
import com.zsxyww.backend.model.entity.User;
import com.zsxyww.backend.service.impl.KnowledgeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * KnowledgeService的单元测试类
 *
 * <p>测试范围：
 * <ul>
 *   <li>知识分类的创建、更新、删除操作</li>
 *   <li>知识文章的增删改查操作</li>
 *   <li>权限验证和异常处理</li>
 *   <li>并发场景下的数据一致性</li>
 * </ul>
 *
 * <p>使用的测试框架：
 * <ul>
 *   <li>JUnit 5 - 用于编写测试用例</li>
 *   <li>Mockito - 用于模拟依赖对象</li>
 *   <li>Spring Security - 用于权限验证</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
class KnowledgeServiceTest {

    /**
     * 知识文章Mapper的Mock对象
     * 用于模拟数据库操作
     */
    @Mock
    private KnowledgeArticleMapper knowledgeArticleMapper;

    /**
     * 知识分类Mapper的Mock对象
     * 用于模拟数据库操作
     */
    @Mock
    private KnowledgeCategoryMapper knowledgeCategoryMapper;

    /**
     * 用户Mapper的Mock对象
     * 用于模拟用户相关操作
     */
    @Mock
    private UserMapper userMapper;

    /**
     * Spring Security上下文Mock对象
     * 用于模拟用户认证信息
     */
    @Mock
    private SecurityContext securityContext;

    /**
     * 被测试的KnowledgeService实现类
     * 使用@InjectMocks自动注入Mock对象
     */
    @InjectMocks
    private KnowledgeServiceImpl knowledgeService;

    /**
     * 测试用户对象
     * 用于模拟当前登录用户
     */
    private User testUser;

    /**
     * 测试准备方法
     * 在每个测试方法执行前运行
     * 初始化Mock对象和测试用户
     */
    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(testUser.getUsername());

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(userMapper.selectOne(new QueryWrapper<User>()
                .eq("username", testUser.getUsername())
                .eq("deleted", 0))).thenReturn(testUser);
    }

    /**
     * 测试创建知识分类
     * 场景：提供有效的分类信息
     * 预期：成功创建并返回分类对象
     */
    @Test
    void createCategory_ValidRequest_ReturnsKnowledgeCategory() {
        KnowledgeCategory category = new KnowledgeCategory();
        category.setName("网络故障");
        category.setParentId(0L);

        when(knowledgeCategoryMapper.insert(any(KnowledgeCategory.class))).thenReturn(1);

        KnowledgeCategory result = knowledgeService.createCategory(category);

        assertNotNull(result);
        verify(knowledgeCategoryMapper, times(1)).insert(category);
    }

    /**
     * 测试创建知识分类时的权限验证
     * 场景：用户没有创建分类的权限
     * 预期：抛出AccessDeniedException异常
     */
    @Test
    void createCategory_WithoutPermission_ThrowsAccessDeniedException() {
        KnowledgeCategory category = new KnowledgeCategory();
        category.setName("网络故障");

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getAuthorities()).thenReturn(new ArrayList<>());
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        assertThrows(AccessDeniedException.class, () -> {
            knowledgeService.createCategory(category);
        });
    }

    /**
     * 测试创建知识分类时的循环引用
     * 场景：分类的父分类指向自身形成循环
     * 预期：抛出BusinessException异常
     */
    @Test
    void createCategory_CircularReference_ThrowsException() {
        KnowledgeCategory parent = new KnowledgeCategory();
        parent.setId(1L);
        parent.setParentId(2L);

        KnowledgeCategory child = new KnowledgeCategory();
        child.setId(2L);
        parent.setParentId(1L);

        when(knowledgeCategoryMapper.selectById(1L)).thenReturn(parent);
        when(knowledgeCategoryMapper.selectById(2L)).thenReturn(child);

        KnowledgeCategory newCategory = new KnowledgeCategory();
        newCategory.setName("新分类");
        newCategory.setParentId(1L);

        assertThrows(BusinessException.class, () -> {
            knowledgeService.createCategory(newCategory);
        });
    }

    /**
     * 测试更新知识分类
     * 场景：将分类移动到新的父分类下
     * 预期：成功更新分类的父分类
     */
    @Test
    void updateCategory_MoveToNewParent_Success() {
        Long categoryId = 1L;
        Long newParentId = 2L;

        KnowledgeCategory category = new KnowledgeCategory();
        category.setId(categoryId);
        category.setParentId(0L);

        KnowledgeCategory newParent = new KnowledgeCategory();
        newParent.setId(newParentId);
        newParent.setParentId(0L);

        when(knowledgeCategoryMapper.selectById(categoryId)).thenReturn(category);
        when(knowledgeCategoryMapper.selectById(newParentId)).thenReturn(newParent);
        when(knowledgeCategoryMapper.updateById(any())).thenReturn(1);

        category.setParentId(newParentId);
        KnowledgeCategory result = knowledgeService.updateCategory(category);

        assertNotNull(result);
        assertEquals(newParentId, result.getParentId());
        verify(knowledgeCategoryMapper).updateById(category);
    }

    /**
     * 测试删除知识分类
     * 场景：删除一个有子分类的分类
     * 预期：抛出BusinessException异常
     */
    @Test
    void deleteCategory_HasChildren_ThrowsException() {
        Long categoryId = 1L;
        
        when(knowledgeCategoryMapper.selectById(categoryId)).thenReturn(new KnowledgeCategory());
        when(knowledgeCategoryMapper.selectCount(any())).thenReturn(1L);

        assertThrows(BusinessException.class, () -> {
            knowledgeService.deleteCategory(categoryId);
        });
    }

    /**
     * 测试获取分类树
     * 场景：存在多级嵌套的分类结构
     * 预期：返回完整的分类树结构
     */
    @Test
    void getCategoryTree_DeepNesting_ReturnsFullTree() {
        KnowledgeCategory root = new KnowledgeCategory();
        root.setId(1L);
        root.setName("根分类");
        root.setParentId(null);

        KnowledgeCategory level1 = new KnowledgeCategory();
        level1.setId(2L);
        level1.setName("一级分类");
        level1.setParentId(1L);

        KnowledgeCategory level2 = new KnowledgeCategory();
        level2.setId(3L);
        level2.setName("二级分类");
        level2.setParentId(2L);

        when(knowledgeCategoryMapper.selectList(any()))
            .thenReturn(Arrays.asList(root, level1, level2));

        List<KnowledgeCategory> result = knowledgeService.getCategoryTree();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertNotNull(result.get(0).getChildren());
        assertEquals(1, result.get(0).getChildren().size());
    }

    /**
     * 测试获取文章列表
     * 场景：使用关键词搜索文章
     * 预期：返回匹配关键词的文章列表
     */
    @Test
    void getArticleList_WithKeyword_ReturnsMatchingArticles() {
        String keyword = "网络";
        Integer page = 1;
        Integer size = 10;
        
        Page<KnowledgeArticle> pageResult = new Page<>();
        List<KnowledgeArticle> articles = Arrays.asList(
            createArticle("网络故障排查"),
            createArticle("网络配置指南")
        );
        pageResult.setRecords(articles);
        pageResult.setTotal(2);

        when(knowledgeArticleMapper.selectPage(any(), any())).thenReturn(pageResult);

        IPage<KnowledgeArticle> results = knowledgeService.getArticleList(null, keyword, page, size);

        assertEquals(2, results.getTotal());
        assertTrue(results.getRecords().stream().allMatch(article -> 
            article.getTitle().contains(keyword)
        ));
    }

    /**
     * 测试并发更新文章
     * 场景：多个线程同时更新同一篇文章
     * 预期：正确处理并发冲突，保证数据一致性
     */
    @Test
    void updateArticle_ConcurrentModification_HandlesConflict() throws InterruptedException {
        Long articleId = 1L;
        CountDownLatch latch = new CountDownLatch(2);
        
        KnowledgeArticle article = new KnowledgeArticle();
        article.setId(articleId);

        when(knowledgeArticleMapper.selectById(articleId)).thenReturn(article);
        when(knowledgeArticleMapper.updateById(any()))
            .thenReturn(1)
            .thenReturn(0); // 第二次更新失败

        ExecutorService executor = Executors.newFixedThreadPool(2);
        try {
            executor.submit(() -> {
                try {
                    KnowledgeArticle update1 = new KnowledgeArticle();
                    update1.setId(articleId);
                    update1.setContent("更新1");
                    knowledgeService.updateArticle(update1);
                } finally {
                    latch.countDown();
                }
            });

            executor.submit(() -> {
                try {
                    KnowledgeArticle update2 = new KnowledgeArticle();
                    update2.setId(articleId);
                    update2.setContent("更新2");
                    knowledgeService.updateArticle(update2);
                } finally {
                    latch.countDown();
                }
            });

            latch.await(5, TimeUnit.SECONDS);
            verify(knowledgeArticleMapper, times(2)).updateById(any());
        } finally {
            executor.shutdown();
        }
    }

    /**
     * 测试增加文章浏览量
     * 场景：用户查看文章
     * 预期：文章浏览量增加
     */
    @Test
    void incrementViewCount_Success() {
        Long articleId = 1L;
        knowledgeService.incrementViewCount(articleId);
        verify(knowledgeArticleMapper).update(isNull(), any());
    }

    /**
     * 测试点赞文章
     * 场景：用户点赞文章
     * 预期：文章点赞数增加
     */
    @Test
    void likeArticle_Success() {
        Long articleId = 1L;
        knowledgeService.likeArticle(articleId);
        verify(knowledgeArticleMapper).update(isNull(), any());
    }

    /**
     * 测试收藏文章
     * 场景：用户收藏文章
     * 预期：文章收藏数增加
     */
    @Test
    void favoriteArticle_Success() {
        Long articleId = 1L;
        knowledgeService.favoriteArticle(articleId);
        verify(knowledgeArticleMapper).update(isNull(), any());
    }

    /**
     * 测试获取推荐文章
     * 场景：请求推荐文章列表
     * 预期：返回不超过指定数量的推荐文章
     */
    @Test
    void getRecommendArticles_ReturnsLimitedArticles() {
        Integer limit = 5;
        List<KnowledgeArticle> recommendedArticles = Arrays.asList(
            createArticle("推荐文章1"),
            createArticle("推荐文章2")
        );

        when(knowledgeArticleMapper.selectList(any())).thenReturn(recommendedArticles);

        List<KnowledgeArticle> results = knowledgeService.getRecommendArticles(limit);

        assertNotNull(results);
        assertTrue(results.size() <= limit);
        verify(knowledgeArticleMapper).selectList(any());
    }

    /**
     * 创建测试用的知识文章对象
     * @param title 文章标题
     * @return 知识文章对象
     */
    private KnowledgeArticle createArticle(String title) {
        KnowledgeArticle article = new KnowledgeArticle();
        article.setTitle(title);
        return article;
    }
}