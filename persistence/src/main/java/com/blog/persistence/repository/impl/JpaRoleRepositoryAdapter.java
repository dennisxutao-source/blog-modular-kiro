package com.blog.persistence.repository.impl;

import com.blog.api.model.Role;
import com.blog.persistence.entity.PermissionEntity;
import com.blog.persistence.entity.RoleEntity;
import com.blog.persistence.repository.JpaPermissionRepository;
import com.blog.persistence.repository.JpaRoleRepository;
import com.blog.persistence.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class JpaRoleRepositoryAdapter implements RoleRepository {
    
    @Autowired
    private JpaRoleRepository jpaRoleRepository;
    
    @Autowired
    private JpaPermissionRepository jpaPermissionRepository;
    
    @Override
    @Transactional
    public Role save(Role role) {
        RoleEntity entity = toEntity(role);
        RoleEntity savedEntity = jpaRoleRepository.save(entity);
        return toModel(savedEntity);
    }
    
    @Override
    public Optional<Role> findById(Long id) {
        return jpaRoleRepository.findById(id)
                .map(this::toModel);
    }
    
    @Override
    public Optional<Role> findByName(String name) {
        return jpaRoleRepository.findByName(name)
                .map(this::toModel);
    }
    
    @Override
    public List<Role> findAll() {
        return jpaRoleRepository.findAll().stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }
    
    @Override
    public Page<Role> findAll(Pageable pageable) {
        return jpaRoleRepository.findAll(pageable)
                .map(this::toModel);
    }
    
    @Override
    @Transactional
    public void deleteById(Long id) {
        jpaRoleRepository.deleteById(id);
    }
    
    @Override
    public boolean existsByName(String name) {
        return jpaRoleRepository.existsByName(name);
    }
    
    @Override
    @Transactional
    public void assignPermission(Long roleId, Long permissionId) {
        RoleEntity role = jpaRoleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("角色不存在"));
        PermissionEntity permission = jpaPermissionRepository.findById(permissionId)
                .orElseThrow(() -> new RuntimeException("权限不存在"));
        
        role.getPermissions().add(permission);
        jpaRoleRepository.save(role);
    }
    
    @Override
    @Transactional
    public void removePermission(Long roleId, Long permissionId) {
        RoleEntity role = jpaRoleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("角色不存在"));
        PermissionEntity permission = jpaPermissionRepository.findById(permissionId)
                .orElseThrow(() -> new RuntimeException("权限不存在"));
        
        role.getPermissions().remove(permission);
        jpaRoleRepository.save(role);
    }
    
    @Override
    public Set<String> getRolePermissions(Long roleId) {
        return jpaRoleRepository.findById(roleId)
                .map(role -> role.getPermissions().stream()
                        .map(PermissionEntity::getName)
                        .collect(Collectors.toSet()))
                .orElse(Set.of());
    }
    
    @Override
    public List<Role> findByIsSystemFalse() {
        return jpaRoleRepository.findAll().stream()
                .filter(role -> !role.getIsSystem())
                .map(this::toModel)
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean isSystemRole(Long roleId) {
        return jpaRoleRepository.findById(roleId)
                .map(RoleEntity::getIsSystem)
                .orElse(false);
    }
    
    private RoleEntity toEntity(Role role) {
        RoleEntity entity = new RoleEntity();
        entity.setId(role.getId());
        entity.setName(role.getName());
        entity.setDescription(role.getDescription());
        entity.setIsSystem(role.getIsSystem());
        entity.setCreatedAt(role.getCreatedAt());
        return entity;
    }
    
    private Role toModel(RoleEntity entity) {
        Role role = new Role();
        role.setId(entity.getId());
        role.setName(entity.getName());
        role.setDescription(entity.getDescription());
        role.setIsSystem(entity.getIsSystem());
        role.setCreatedAt(entity.getCreatedAt());
        
        // 设置权限信息
        if (entity.getPermissions() != null) {
            Set<String> permissions = entity.getPermissions().stream()
                    .map(PermissionEntity::getName)
                    .collect(Collectors.toSet());
            role.setPermissions(permissions);
        }
        
        return role;
    }
}