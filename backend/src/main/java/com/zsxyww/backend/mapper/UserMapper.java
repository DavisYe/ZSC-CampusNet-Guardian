package com.zsxyww.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zsxyww.backend.model.entity.User;
import com.zsxyww.backend.model.entity.UserRole;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户Mapper接口
 *
 * @author DavisYe
 * @since 1.0.0
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    
    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    @Select("SELECT * FROM user WHERE username = #{username} AND deleted = 0")
    User findByUsername(@Param("username") String username);
    
    /**
     * 根据学号查询用户
     *
     * @param studentId 学号
     * @return 用户信息
     */
    @Select("SELECT * FROM user WHERE student_id = #{studentId} AND deleted = 0")
    User findByStudentId(@Param("studentId") String studentId);
    
    /**
     * 查询用户的角色编码列表
     *
     * @param userId 用户ID
     * @return 角色编码列表
     */
    @Select("SELECT r.code FROM role r " +
            "INNER JOIN user_role ur ON r.id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND r.deleted = 0 AND ur.deleted = 0")
    List<String> findUserRoles(@Param("userId") Long userId);

    /**
     * 插入用户角色关系
     *
     * @param userRole 用户角色关系
     * @return 影响行数
     */
    @Insert("INSERT INTO user_role (user_id, role_id, create_time, update_time, deleted) " +
            "VALUES (#{userId}, #{roleId}, NOW(), NOW(), 0)")
    int insertUserRole(UserRole userRole);

    /**
     * 批量插入用户
     *
     * @param users 用户列表
     * @return 影响行数
     */
    @Insert("<script>" +
            "INSERT INTO user (username, password, student_id, real_name, enabled, " +
            "account_non_expired, account_non_locked, credentials_non_expired, " +
            "create_time, update_time, deleted) VALUES " +
            "<foreach collection='users' item='user' separator=','>" +
            "(#{user.username}, #{user.password}, #{user.studentId}, #{user.realName}, " +
            "#{user.enabled}, #{user.accountNonExpired}, #{user.accountNonLocked}, " +
            "#{user.credentialsNonExpired}, NOW(), NOW(), 0)" +
            "</foreach>" +
            "</script>")
    int batchInsertUsers(@Param("users") List<User> users);

    /**
     * 批量插入用户角色关系
     *
     * @param userRoles 用户角色关系列表
     * @return 影响行数
     */
    @Insert("<script>" +
            "INSERT INTO user_role (user_id, role_id, create_time, update_time, deleted) VALUES " +
            "<foreach collection='userRoles' item='userRole' separator=','>" +
            "(#{userRole.userId}, #{userRole.roleId}, NOW(), NOW(), 0)" +
            "</foreach>" +
            "</script>")
    int batchInsertUserRoles(@Param("userRoles") List<UserRole> userRoles);
}