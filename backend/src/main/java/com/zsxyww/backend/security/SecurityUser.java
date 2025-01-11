package com.zsxyww.backend.security;

import com.zsxyww.backend.model.entity.User;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 安全用户实体类
 * 实现Spring Security的UserDetails接口
 *
 * @author DavisYe
 * @since 1.0.0
 */
@Data
public class SecurityUser implements UserDetails {

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 学号
     */
    private String studentId;

    /**
     * 权限列表
     */
    private Collection<? extends GrantedAuthority> authorities;

    /**
     * 账号是否未过期
     */
    private boolean accountNonExpired;

    /**
     * 账号是否未锁定
     */
    private boolean accountNonLocked;

    /**
     * 凭证是否未过期
     */
    private boolean credentialsNonExpired;

    /**
     * 账号是否启用
     */
    private boolean enabled;

    /**
     * 从User实体转换为SecurityUser
     */
    public static SecurityUser fromUser(User user, List<String> roles) {
        SecurityUser securityUser = new SecurityUser();
        securityUser.setId(user.getId());
        securityUser.setUsername(user.getUsername());
        securityUser.setPassword(user.getPassword());
        securityUser.setStudentId(user.getStudentId());
        securityUser.setAccountNonExpired(user.getAccountNonExpired());
        securityUser.setAccountNonLocked(user.getAccountNonLocked());
        securityUser.setCredentialsNonExpired(user.getCredentialsNonExpired());
        securityUser.setEnabled(user.getEnabled());
        
        // 转换角色为GrantedAuthority
        List<GrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
        securityUser.setAuthorities(authorities);
        
        return securityUser;
    }
}