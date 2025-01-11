package com.zsxyww.backend.security;

import com.zsxyww.backend.mapper.UserMapper;
import com.zsxyww.backend.model.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * UserDetailsService实现类
 * 用于加载用户信息
 *
 * @author DavisYe
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 先尝试通过用户名查找
        User user = userMapper.findByUsername(username);
        
        // 如果找不到，尝试通过学号查找
        if (user == null) {
            user = userMapper.findByStudentId(username);
        }
        
        // 如果还是找不到，抛出异常
        if (user == null) {
            throw new UsernameNotFoundException("用户名或学号不存在：" + username);
        }
        
        // 查询用户角色
        List<String> roles = userMapper.findUserRoles(user.getId());
        
        // 转换为SecurityUser
        return SecurityUser.fromUser(user, roles);
    }
}