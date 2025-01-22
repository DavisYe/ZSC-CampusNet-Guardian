package com.zsxyww.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zsxyww.backend.config.JwtConfig;
import com.zsxyww.backend.exception.BusinessException;
import com.zsxyww.backend.mapper.UserMapper;
import com.zsxyww.backend.mapper.UserRoleMapper;
import com.zsxyww.backend.model.dto.*;
import com.zsxyww.backend.model.entity.User;
import com.zsxyww.backend.model.entity.UserRole;
import com.zsxyww.backend.security.SecurityUser;
import com.zsxyww.backend.service.AuthService;
import com.zsxyww.backend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 认证服务实现类
 *
 * @author DavisYe
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final JwtConfig jwtConfig;
    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String TOKEN_BLACKLIST_PREFIX = "token:blacklist:";

    @Override
    public AuthResponse login(LoginRequest request) {
        // 进行身份认证
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        
        // 认证成功后，将认证信息存入SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        // 生成JWT token
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        String token = jwtUtil.generateToken(securityUser);
        
        // 构建认证响应
        return buildAuthResponse(securityUser, token);
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // 验证密码是否一致
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException("两次输入的密码不一致");
        }
        
        // 验证用户名是否已存在
        if (userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, request.getUsername())
                .eq(User::getDeleted, 0)) != null) {
            throw new BusinessException("用户名已存在");
        }
        
        // 验证学号是否已存在
        if (userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getStudentId, request.getStudentId())
                .eq(User::getDeleted, 0)) != null) {
            throw new BusinessException("学号已被注册");
        }
        
        // 创建用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setStudentId(request.getStudentId());
        user.setRealName(request.getRealName());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setEnabled(true);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        
        userMapper.insert(user);
        
        // 分配普通用户角色
        UserRole userRole = new UserRole();
        userRole.setUserId(user.getId());
        userRole.setRoleId(3L); // 普通用户角色ID为3
        userRoleMapper.insert(userRole);
        
        // 登录新用户
        return login(new LoginRequest() {{
            setUsername(request.getUsername());
            setPassword(request.getPassword());
        }});
    }

    @Override
    @Transactional
    public int batchCreateUsers(List<BatchCreateUserRequest> requests) {
        List<User> users = new ArrayList<>();
        List<UserRole> userRoles = new ArrayList<>();
        
        for (BatchCreateUserRequest request : requests) {
            // 验证学号是否已存在
            if (userMapper.selectOne(new LambdaQueryWrapper<User>()
                    .eq(User::getStudentId, request.getStudentId())
                    .eq(User::getDeleted, 0)) != null) {
                throw new BusinessException("学号已被注册：" + request.getStudentId());
            }
            
            // 验证用户名是否已存在
            if (userMapper.selectOne(new LambdaQueryWrapper<User>()
                    .eq(User::getUsername, request.getUsername())
                    .eq(User::getDeleted, 0)) != null) {
                throw new BusinessException("用户名已存在：" + request.getUsername());
            }
            
            // 创建用户
            User user = new User();
            user.setUsername(request.getUsername());
            // 使用学号后6位作为初始密码
            String initialPassword = request.getStudentId().substring(Math.max(0, request.getStudentId().length() - 6));
            user.setPassword(passwordEncoder.encode(initialPassword));
            user.setStudentId(request.getStudentId());
            user.setRealName(request.getUsername()); // 默认使用用户名作为真实姓名
            user.setEnabled(true);
            user.setAccountNonExpired(true);
            user.setAccountNonLocked(true);
            user.setCredentialsNonExpired(true);
            
            users.add(user);
        }
        
        // 批量插入用户
        for (User user : users) {
            userMapper.insert(user);
        }
        
        // 为每个用户分配普通用户角色
        for (User user : users) {
            UserRole userRole = new UserRole();
            userRole.setUserId(user.getId());
            userRole.setRoleId(3L); // 普通用户角色ID为3
            userRoles.add(userRole);
        }
        
        // 批量插入用户角色关系
        for (UserRole userRole : userRoles) {
            userRoleMapper.insert(userRole);
        }
        
        return users.size();
    }

    @Override
    public AuthResponse refreshToken(String oldToken) {
        // 验证旧token是否有效
        if (!jwtUtil.validateToken(oldToken)) {
            throw new BusinessException("无效的token");
        }
        
        // 检查token是否在黑名单中
        String blacklistKey = TOKEN_BLACKLIST_PREFIX + oldToken;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(blacklistKey))) {
            throw new BusinessException("token已失效");
        }
        
        // 获取用户信息
        SecurityUser securityUser = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        // 生成新token
        String newToken = jwtUtil.generateToken(securityUser);
        
        // 将旧token加入黑名单
        redisTemplate.opsForValue().set(blacklistKey, "1", jwtConfig.getExpiration(), TimeUnit.MILLISECONDS);
        
        return buildAuthResponse(securityUser, newToken);
    }

    @Override
    public void logout(String token) {
        // 将token加入黑名单
        String blacklistKey = TOKEN_BLACKLIST_PREFIX + token;
        redisTemplate.opsForValue().set(blacklistKey, "1", jwtConfig.getExpiration(), TimeUnit.MILLISECONDS);
        
        // 清除SecurityContext
        SecurityContextHolder.clearContext();
    }

    /**
     * 构建认证响应
     */
    private AuthResponse buildAuthResponse(SecurityUser securityUser, String token) {
        List<String> roles = securityUser.getAuthorities().stream()
                .map(authority -> authority.getAuthority().replace("ROLE_", ""))
                .toList();
        
        return AuthResponse.builder()
                .token(token)
                .tokenType(jwtConfig.getTokenPrefix().trim())
                .expiresIn(jwtConfig.getExpiration())
                .userId(securityUser.getId())
                .username(securityUser.getUsername())
                .studentId(securityUser.getStudentId())
                .realName(securityUser.getUsername())
                .roles(roles)
                .build();
    }
}