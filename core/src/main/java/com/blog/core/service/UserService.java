package com.blog.core.service;

import com.blog.api.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.Set;

public interface UserService {
    
    /**
     * 创建用户
     */
    User createUser(User user);
    
    /**
     * 更新用户信息
     */
    User updateUser(Long userId, User user);
    
    /**
     * 删除用户
     */
    void deleteUser(Long userId);
    
    /**
     * 根据ID获取用户
     */
    Optional<User> getUserById(Long userId);
    
    /**
     * 根据用户名获取用户
     */
    Optional<User> getUserByUsername(String username);
    
    /**
     * 根据邮箱获取用户
     */
    Optional<User> getUserByEmail(String email);
    
    /**
     * 获取用户列表（分页）
     */
    Page<User> getUsers(Pageable pageable);
    
    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(String username);
    
    /**
     * 检查邮箱是否存在
     */
    boolean existsByEmail(String email);
    
    /**
     * 为用户分配角色
     */
    void assignRole(Long userId, Long roleId);
    
    /**
     * 移除用户角色
     */
    void removeRole(Long userId, Long roleId);
    
    /**
     * 获取用户的所有角色
     */
    Set<String> getUserRoles(Long userId);
    
    /**
     * 检查用户是否有指定权限
     */
    boolean hasPermission(Long userId, String resource, String action);
    
    /**
     * 更新用户最后登录时间
     */
    void updateLastLoginTime(Long userId);
    
    /**
     * 锁定用户
     */
    void lockUser(Long userId);
    
    /**
     * 解锁用户
     */
    void unlockUser(Long userId);
    
    /**
     * 激活用户
     */
    void activateUser(Long userId);
    
    /**
     * 停用用户
     */
    void deactivateUser(Long userId);
}