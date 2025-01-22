package com.zsxyww.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zsxyww.backend.model.entity.KnowledgeCategory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 知识库分类Mapper接口
 *
 * @author DavisYe
 * @since 1.0.0
 */
@Mapper
public interface KnowledgeCategoryMapper extends BaseMapper<KnowledgeCategory> {
}