package com.zsxyww.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zsxyww.backend.model.entity.KnowledgeCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 知识库分类Mapper接口
 *
 * @author DavisYe
 * @since 1.0.0
 */
@Mapper
public interface KnowledgeCategoryMapper extends BaseMapper<KnowledgeCategory> {
    
    /**
     * 获取所有一级分类
     */
    @Select("SELECT * FROM knowledge_category WHERE parent_id IS NULL AND deleted = 0 ORDER BY sort")
    List<KnowledgeCategory> selectRootCategories();
    
    /**
     * 获取指定分类的子分类
     *
     * @param parentId 父分类ID
     */
    @Select("SELECT * FROM knowledge_category WHERE parent_id = #{parentId} AND deleted = 0 ORDER BY sort")
    List<KnowledgeCategory> selectChildCategories(Long parentId);
    
    /**
     * 检查分类下是否有文章
     *
     * @param categoryId 分类ID
     * @return 文章数量
     */
    @Select("SELECT COUNT(*) FROM knowledge_article WHERE category_id = #{categoryId} AND deleted = 0")
    int countArticles(Long categoryId);
    
    /**
     * 检查分类下是否有子分类
     *
     * @param categoryId 分类ID
     * @return 子分类数量
     */
    @Select("SELECT COUNT(*) FROM knowledge_category WHERE parent_id = #{categoryId} AND deleted = 0")
    int countChildren(Long categoryId);
}