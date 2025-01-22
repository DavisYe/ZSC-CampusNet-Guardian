package com.zsxyww.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zsxyww.backend.model.entity.KnowledgeArticle;
import org.apache.ibatis.annotations.Mapper;

/**
 * 知识库文章Mapper接口
 *
 * @author DavisYe
 * @since 1.0.0
 */
@Mapper
public interface KnowledgeArticleMapper extends BaseMapper<KnowledgeArticle> {
}