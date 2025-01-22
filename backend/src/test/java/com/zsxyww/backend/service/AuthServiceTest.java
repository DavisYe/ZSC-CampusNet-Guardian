package com.zsxyww.backend.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zsxyww.backend.config.JwtConfig;
import com.zsxyww.backend.mapper.UserMapper;
import com.zsxyww.backend.mapper.UserRoleMapper;
import com.zsxyww.backend.model.dto.AuthResponse;
import com.zsxyww.backend.model.dto.LoginRequest;
import com.zsxyww.backend.model.dto.RegisterRequest;
import com.zsxyww.backend.model.entity.User;
import com.zsxyww.backend.security.SecurityUser;
import com.zsxyww.backend.service.impl.AuthServiceImpl;
import com.zsxyww.backend.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.data.redis.RedisConnectionFailureException;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private JwtConfig jwtConfig;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserRoleMapper userRoleMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        when(jwtConfig.getExpiration()).thenReturn(3600000L);
        when(jwtConfig.getSecret()).thenReturn("testSecret");
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void login_AccountDisabled_ThrowsException() {
        LoginRequest request = new LoginRequest();
        request.setUsername("disabledUser");
        request.setPassword("password123");

        SecurityUser securityUser = mock(SecurityUser.class);
        when(securityUser.isEnabled()).thenReturn(false);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(securityUser);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);

        assertThrows(DisabledException.class, () -> {
            authService.login(request);
        });
    }

    @Test
    void login_AccountLocked_ThrowsException() {
        LoginRequest request = new LoginRequest();
        request.setUsername("lockedUser");
        request.setPassword("password123");

        when(authenticationManager.authenticate(any()))
            .thenThrow(new LockedException("Account is locked"));

        assertThrows(LockedException.class, () -> {
            authService.login(request);
        });
    }

    @Test
    void login_UserDeleted_ThrowsException() {
        LoginRequest request = new LoginRequest();
        request.setUsername("deletedUser");
        request.setPassword("password123");

        User deletedUser = new User();
        deletedUser.setDeleted(1);
        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(deletedUser);

        assertThrows(DisabledException.class, () -> {
            authService.login(request);
        });
    }

    @Test
    void login_TokenNearExpiry_RefreshesToken() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("password123");

        SecurityUser securityUser = mock(SecurityUser.class);
        when(securityUser.isEnabled()).thenReturn(true);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(securityUser);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);

        String oldToken = "oldToken";
        String newToken = "newToken";
        when(jwtUtil.generateToken(securityUser)).thenReturn(oldToken);
        when(jwtUtil.getExpirationDateFromToken(oldToken)).thenReturn(new Date(System.currentTimeMillis() + 300000)); // 5分钟后过期
        when(jwtUtil.refreshToken(oldToken)).thenReturn(newToken);

        AuthResponse response = authService.login(request);
        assertEquals(newToken, response.getToken());
    }

    @Test
    void login_ConcurrentRefreshToken_HandlesRaceCondition() {
        String token = "testToken";
        SecurityUser securityUser = mock(SecurityUser.class);
        when(securityUser.getUsername()).thenReturn("testuser");
        
        when(valueOperations.setIfAbsent(anyString(), any(), anyLong(), any(TimeUnit.class)))
            .thenReturn(false); // 模拟并发锁已被获取

        assertThrows(RuntimeException.class, () -> {
            authService.refreshToken(token);
        });
    }

    @Test
    void login_TokenBlacklisted_ThrowsException() {
        String blacklistedToken = "blacklistedToken";
        when(valueOperations.get("blacklist:" + blacklistedToken)).thenReturn("true");

        assertThrows(SecurityException.class, () -> {
            authService.refreshToken(blacklistedToken);
        });
    }

    @Test
    void login_RedisCacheHit_ReturnsFromCache() {
        LoginRequest request = new LoginRequest();
        request.setUsername("cachedUser");
        request.setPassword("password123");

        AuthResponse cachedResponse = AuthResponse.builder()
            .token("cachedToken")
            .tokenType("Bearer")
            .expiresIn(3600000L)
            .build();
            
        when(valueOperations.get("auth:cachedUser")).thenReturn(cachedResponse);

        AuthResponse response = authService.login(request);
        assertEquals("cachedToken", response.getToken());
        verify(authenticationManager, never()).authenticate(any());
    }

    @Test
    void login_RedisCacheMiss_LoadsFromDatabase() {
        LoginRequest request = new LoginRequest();
        request.setUsername("newUser");
        request.setPassword("password123");

        when(valueOperations.get("auth:newUser")).thenReturn(null);
        SecurityUser securityUser = mock(SecurityUser.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(securityUser);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtUtil.generateToken(securityUser)).thenReturn("newToken");

        AuthResponse response = authService.login(request);
        assertEquals("newToken", response.getToken());
        verify(valueOperations).set(eq("auth:newUser"), any(AuthResponse.class), anyLong(), any(TimeUnit.class));
    }

    @Test
    void login_RedisCacheEviction_HandlesConsistency() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("password123");

        SecurityUser securityUser = mock(SecurityUser.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(securityUser);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtUtil.generateToken(securityUser)).thenReturn("newToken");

        doThrow(new RedisConnectionFailureException("Connection failed"))
            .when(valueOperations).set(anyString(), any(), anyLong(), any(TimeUnit.class));

        AuthResponse response = authService.login(request);
        assertNotNull(response);
        assertEquals("newToken", response.getToken());
    }

    @Test
    void login_ExpiredToken_ThrowsException() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("password123");

        SecurityUser securityUser = mock(SecurityUser.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(securityUser);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtUtil.generateToken(securityUser)).thenThrow(new ExpiredJwtException(null, null, "Token expired"));

        assertThrows(ExpiredJwtException.class, () -> {
            authService.login(request);
        });
    }

    @Test
    void login_RedisFailure_ThrowsException() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("password123");

        SecurityUser securityUser = mock(SecurityUser.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(securityUser);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtUtil.generateToken(securityUser)).thenReturn("mockToken");
        doThrow(new RedisConnectionFailureException("Redis connection failed"))
            .when(redisTemplate).opsForValue().set(anyString(), any(), anyLong(), any());

        assertThrows(RedisConnectionFailureException.class, () -> {
            authService.login(request);
        });
    }

    @Test
    void login_ValidCredentials_ReturnsAuthResponse() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("password123");

        SecurityUser securityUser = mock(SecurityUser.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(securityUser);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtUtil.generateToken(securityUser)).thenReturn("mockToken");

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("mockToken", response.getToken());
        verify(authenticationManager, times(1)).authenticate(any());
        verify(jwtUtil, times(1)).generateToken(securityUser);
    }

    @Test
    void login_InvalidCredentials_ThrowsException() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("wrongpassword");

        when(authenticationManager.authenticate(any()))
            .thenThrow(new BadCredentialsException("Invalid credentials"));

        assertThrows(BadCredentialsException.class, () -> {
            authService.login(request);
        });
        verify(authenticationManager, times(1)).authenticate(any());
    }

    @Test
    void register_ValidRequest_ReturnsAuthResponse() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setPassword("password");

        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(jwtUtil.generateToken(any(SecurityUser.class))).thenReturn("testToken");

        AuthResponse result = authService.register(request);

        assertNotNull(result);
        assertEquals("testToken", result.getToken());
        verify(userMapper, times(2)).selectOne(any(LambdaQueryWrapper.class)); // 一次检查用户名，一次检查学号
        verify(passwordEncoder, times(1)).encode("password");
        verify(jwtUtil, times(1)).generateToken(any(SecurityUser.class));
    }

    @Test
    void refreshToken_ValidToken_ReturnsNewAuthResponse() {
        String oldToken = "oldToken";
        String newToken = "newToken";
        
        when(jwtUtil.refreshToken(oldToken)).thenReturn(newToken);
        when(jwtUtil.validateToken(oldToken)).thenReturn(true);

        AuthResponse result = authService.refreshToken(oldToken);

        assertNotNull(result);
        assertEquals(newToken, result.getToken());
        verify(jwtUtil, times(1)).refreshToken(oldToken);
        verify(jwtUtil, times(1)).validateToken(oldToken);
    }

    @Test
    void refreshToken_InvalidToken_ThrowsException() {
        String invalidToken = "invalidToken";
        
        when(jwtUtil.validateToken(invalidToken)).thenReturn(false);

        assertThrows(SecurityException.class, () -> {
            authService.refreshToken(invalidToken);
        });
        verify(jwtUtil, times(1)).validateToken(invalidToken);
        verify(jwtUtil, never()).refreshToken(anyString());
    }
}
