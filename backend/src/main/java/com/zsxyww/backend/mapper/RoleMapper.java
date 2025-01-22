package com.zsxyww.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zsxyww.backend.model.entity.Role;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色Mapper接口
 *
 * @author DavisYe
 * @since 1.0.0
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {
}