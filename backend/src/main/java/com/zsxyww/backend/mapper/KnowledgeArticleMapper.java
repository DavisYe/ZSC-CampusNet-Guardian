package com.zsxyww.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zsxyww.backend.model.entity.KnowledgeArticle;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 知识库文章Mapper接口
 *
 * @author DavisYe
 * @since 1.0.0
 */
@Mapper
public interface KnowledgeArticleMapper extends BaseMapper<KnowledgeArticle> {
    
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
     */
    @Select("SELECT * FROM knowledge_article " +
            "WHERE deleted = 0 AND status = 1 " +
            "ORDER BY is_recommend DESC, " +
            "(view_count * 0.4 + like_count * 0.3 + favorite_count * 0.3) DESC " +
            "LIMIT #{limit}")
    List<KnowledgeArticle> selectRecommendArticles(@Param("limit") Integer limit);
}