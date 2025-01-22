package com.zsxyww.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zsxyww.backend.model.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户Mapper接口
 *
 * @author DavisYe
 * @since 1.0.0
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}