package com.blog.persistence.repository.impl;

import com.blog.api.model.User;
import com.blog.persistence.entity.RoleEntity;
import com.blog.persistence.entity.UserEntity;
import com.blog.persistence.repository.JpaRoleRepository;
import com.blog.persistence.repository.JpaUserRepository;
import com.blog.persistence.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class JpaUserRepositoryAdapter implements UserRepository {
    
    @Autowired
    private JpaUserRepository jpaUserRepository;
    
    @Autowired
    private JpaRoleRepository jpaRoleRepository;
    
    @Override
    @Transactional
    public User save(User user) {
        UserEntity entity = toEntity(user);
        UserEntity savedEntity = jpaUserRepository.save(entity);
        return toModel(savedEntity);
    }
    
    @Override
    public Optional<User> findById(Long id) {
        return jpaUserRepository.findById(id)
                .map(this::toModel);
    }
    
    @Override
    public Optional<User> findByUsername(String username) {
        return jpaUserRepository.findByUsername(username)
                .map(this::toModel);
    }
    
    @Override
    public Optional<User> findByEmail(String email) {
        return jpaUserRepository.findByEmail(email)
                .map(this::toModel);
    }
    
    @Override
    public Page<User> findAll(Pageable pageable) {
        return jpaUserRepository.findAll(pageable)
                .map(this::toModel);
    }
    
    @Override
    @Transactional
    public void deleteById(Long id) {
        jpaUserRepository.deleteById(id);
    }
    
    @Override
    public boolean existsByUsername(String username) {
        return jpaUserRepository.existsByUsername(username);
    }
    
    @Override
    public boolean existsByEmail(String email) {
        return jpaUserRepository.existsByEmail(email);
    }
    
    @Override
    @Transactional
    public void assignRole(Long userId, Long roleId) {
        UserEntity user = jpaUserRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        RoleEntity role = jpaRoleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("角色不存在"));
        
        user.getRoles().add(role);
        jpaUserRepository.save(user);
    }
    
    @Override
    @Transactional
    public void removeRole(Long userId, Long roleId) {
        UserEntity user = jpaUserRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        RoleEntity role = jpaRoleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("角色不存在"));
        
        user.getRoles().remove(role);
        jpaUserRepository.save(user);
    }
    
    @Override
    public Set<String> getUserRoles(Long userId) {
        return jpaUserRepository.findById(userId)
                .map(user -> user.getRoles().stream()
                        .map(RoleEntity::getName)
                        .collect(Collectors.toSet()))
                .orElse(Set.of());
    }
    
    @Override
    public boolean hasPermission(Long userId, String resource, String action) {
        return jpaUserRepository.findById(userId)
                .map(user -> user.getRoles().stream()
                        .flatMap(role -> role.getPermissions().stream())
                        .anyMatch(permission -> 
                                permission.getResource().equals(resource) && 
                                permission.getAction().equals(action)))
                .orElse(false);
    }
    
    @Override
    @Transactional
    public void updateLastLoginTime(Long userId) {
        jpaUserRepository.findById(userId)
                .ifPresent(user -> {
                    user.setLastLoginAt(LocalDateTime.now());
                    jpaUserRepository.save(user);
                });
    }
    
    private UserEntity toEntity(User user) {
        UserEntity entity = new UserEntity();
        entity.setId(user.getId());
        entity.setUsername(user.getUsername());
        entity.setEmail(user.getEmail());
        entity.setPasswordHash(user.getPasswordHash());
        entity.setFullName(user.getFullName());
        entity.setAvatarUrl(user.getAvatarUrl());
        
        if (user.getStatus() != null) {
            entity.setStatus(UserEntity.UserStatus.valueOf(user.getStatus().name()));
        }
        
        entity.setCreatedAt(user.getCreatedAt());
        entity.setUpdatedAt(user.getUpdatedAt());
        entity.setLastLoginAt(user.getLastLoginAt());
        
        return entity;
    }
    
    private User toModel(UserEntity entity) {
        User user = new User();
        user.setId(entity.getId());
        user.setUsername(entity.getUsername());
        user.setEmail(entity.getEmail());
        user.setPasswordHash(entity.getPasswordHash());
        user.setFullName(entity.getFullName());
        user.setAvatarUrl(entity.getAvatarUrl());
        
        if (entity.getStatus() != null) {
            user.setStatus(User.UserStatus.valueOf(entity.getStatus().name()));
        }
        
        user.setCreatedAt(entity.getCreatedAt());
        user.setUpdatedAt(entity.getUpdatedAt());
        user.setLastLoginAt(entity.getLastLoginAt());
        
        // 设置角色信息
        if (entity.getRoles() != null) {
            Set<String> roles = entity.getRoles().stream()
                    .map(RoleEntity::getName)
                    .collect(Collectors.toSet());
            user.setRoles(roles);
        }
        
        return user;
    }
}