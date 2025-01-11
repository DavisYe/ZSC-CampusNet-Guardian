package com.zsxyww.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zsxyww.backend.model.entity.KnowledgeArticle;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 知识库文章Mapper接口
 *
 * @author DavisYe
 * @since 1.0.0
 */
@Mapper
public interface KnowledgeArticleMapper extends BaseMapper<KnowledgeArticle> {
    
    /**
     * 分页查询文章列表
     *
     * @param page 分页参数
     * @param categoryId 分类ID
     * @param keyword 搜索关键词
     */
    @Select("<script>" +
            "SELECT * FROM knowledge_article WHERE deleted = 0 " +
            "<if test='categoryId != null'> AND category_id = #{categoryId} </if>" +
            "<if test='keyword != null and keyword != \"\"'>" +
            " AND (title LIKE CONCAT('%', #{keyword}, '%') " +
            " OR content LIKE CONCAT('%', #{keyword}, '%')" +
            " OR tags LIKE CONCAT('%', #{keyword}, '%')" +
            " OR keywords LIKE CONCAT('%', #{keyword}, '%'))" +
            "</if>" +
            " ORDER BY is_top DESC, sort DESC, create_time DESC" +
            "</script>")
    IPage<KnowledgeArticle> selectArticleList(Page<KnowledgeArticle> page,
                                            @Param("categoryId") Long categoryId,
                                            @Param("keyword") String keyword);
    
    /**
     * 增加浏览次数
     */
    @Update("UPDATE knowledge_article SET view_count = view_count + 1 " +
            "WHERE id = #{articleId} AND deleted = 0")
    int incrementViewCount(@Param("articleId") Long articleId);
    
    /**
     * 增加点赞次数
     */
    @Update("UPDATE knowledge_article SET like_count = like_count + 1 " +
            "WHERE id = #{articleId} AND deleted = 0")
    int incrementLikeCount(@Param("articleId") Long articleId);
    
    /**
     * 增加收藏次数
     */
    @Update("UPDATE knowledge_article SET favorite_count = favorite_count + 1 " +
            "WHERE id = #{articleId} AND deleted = 0")
    int incrementFavoriteCount(@Param("articleId") Long articleId);
    
    /**
     * 获取推荐文章
     * 根据浏览量、点赞数和收藏数综合排序
     */
    @Select("SELECT * FROM knowledge_article " +
            "WHERE deleted = 0 AND status = 1 " +
            "ORDER BY is_recommend DESC, " +
            "(view_count * 0.4 + like_count * 0.3 + favorite_count * 0.3) DESC " +
            "LIMIT #{limit}")
    List<KnowledgeArticle> selectRecommendArticles(@Param("limit") Integer limit);
    
    /**
     * 获取相关文章
     * 根据标签和关键词匹配
     */
    @Select("SELECT * FROM knowledge_article " +
            "WHERE deleted = 0 AND status = 1 " +
            "AND id != #{articleId} " +
            "AND (tags LIKE CONCAT('%', #{tags}, '%') " +
            "OR keywords LIKE CONCAT('%', #{keywords}, '%')) " +
            "ORDER BY create_time DESC " +
            "LIMIT #{limit}")
    List<KnowledgeArticle> selectRelatedArticles(@Param("articleId") Long articleId,
                                                @Param("tags") String tags,
                                                @Param("keywords") String keywords,
                                                @Param("limit") Integer limit);
}