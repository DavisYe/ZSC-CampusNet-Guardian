package com.zsxyww.backend.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zsxyww.backend.mapper.RoleMapper;
import com.zsxyww.backend.mapper.UserMapper;
import com.zsxyww.backend.mapper.UserRoleMapper;
import com.zsxyww.backend.model.entity.Role;
import com.zsxyww.backend.model.entity.User;
import com.zsxyww.backend.model.entity.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
    private final UserRoleMapper userRoleMapper;
    private final RoleMapper roleMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 先尝试通过用户名查找
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username)
                .eq(User::getDeleted, 0));
        
        // 如果找不到，尝试通过学号查找
        if (user == null) {
            user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                    .eq(User::getStudentId, username)
                    .eq(User::getDeleted, 0));
        }
        
        // 如果还是找不到，抛出异常
        if (user == null) {
            throw new UsernameNotFoundException("用户名或学号不存在：" + username);
        }
        
        // 查询用户角色
        List<UserRole> userRoles = userRoleMapper.selectList(new LambdaQueryWrapper<UserRole>()
                .eq(UserRole::getUserId, user.getId())
                .eq(UserRole::getDeleted, 0));
        
        // 查询角色编码
        List<String> roles = userRoles.stream()
                .map(userRole -> roleMapper.selectOne(new LambdaQueryWrapper<Role>()
                        .eq(Role::getId, userRole.getRoleId())
                        .eq(Role::getDeleted, 0)))
                .filter(role -> role != null)
                .map(Role::getCode)
                .collect(Collectors.toList());
        
        // 转换为SecurityUser
        return SecurityUser.fromUser(user, roles);
    }
}