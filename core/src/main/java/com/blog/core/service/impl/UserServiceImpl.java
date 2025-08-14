package com.blog.core.service.impl;

import com.blog.api.model.User;
import com.blog.core.service.UserService;
import com.blog.persistence.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public User createUser(User user) {
        // 检查用户名和邮箱是否已存在
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("用户名已存在: " + user.getUsername());
        }
        
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("邮箱已存在: " + user.getEmail());
        }
        
        // 加密密码
        if (user.getPasswordHash() != null) {
            user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        }
        
        // 设置默认值
        user.setStatus(User.UserStatus.ACTIVE);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        
        return userRepository.save(user);
    }
    
    @Override
    public User updateUser(Long userId, User user) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在: " + userId));
        
        // 检查用户名是否被其他用户使用
        if (!existingUser.getUsername().equals(user.getUsername()) && 
            userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("用户名已存在: " + user.getUsername());
        }
        
        // 检查邮箱是否被其他用户使用
        if (!existingUser.getEmail().equals(user.getEmail()) && 
            userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("邮箱已存在: " + user.getEmail());
        }
        
        // 更新用户信息
        existingUser.setUsername(user.getUsername());
        existingUser.setEmail(user.getEmail());
        existingUser.setFullName(user.getFullName());
        existingUser.setAvatarUrl(user.getAvatarUrl());
        
        // 如果提供了新密码，则加密并更新
        if (user.getPasswordHash() != null && !user.getPasswordHash().isEmpty()) {
            existingUser.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        }
        
        if (user.getStatus() != null) {
            existingUser.setStatus(user.getStatus());
        }
        
        existingUser.setUpdatedAt(LocalDateTime.now());
        
        return userRepository.save(existingUser);
    }
    
    @Override
    public void deleteUser(Long userId) {
        if (!userRepository.findById(userId).isPresent()) {
            throw new RuntimeException("用户不存在: " + userId);
        }
        userRepository.deleteById(userId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<User> getUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    @Override
    public void assignRole(Long userId, Long roleId) {
        if (!userRepository.findById(userId).isPresent()) {
            throw new RuntimeException("用户不存在: " + userId);
        }
        userRepository.assignRole(userId, roleId);
    }
    
    @Override
    public void removeRole(Long userId, Long roleId) {
        if (!userRepository.findById(userId).isPresent()) {
            throw new RuntimeException("用户不存在: " + userId);
        }
        userRepository.removeRole(userId, roleId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Set<String> getUserRoles(Long userId) {
        return userRepository.getUserRoles(userId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean hasPermission(Long userId, String resource, String action) {
        return userRepository.hasPermission(userId, resource, action);
    }
    
    @Override
    public void updateLastLoginTime(Long userId) {
        userRepository.updateLastLoginTime(userId);
    }
    
    @Override
    public void lockUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在: " + userId));
        user.setStatus(User.UserStatus.LOCKED);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }
    
    @Override
    public void unlockUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在: " + userId));
        user.setStatus(User.UserStatus.ACTIVE);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }
    
    @Override
    public void activateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在: " + userId));
        user.setStatus(User.UserStatus.ACTIVE);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }
    
    @Override
    public void deactivateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在: " + userId));
        user.setStatus(User.UserStatus.INACTIVE);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }
}