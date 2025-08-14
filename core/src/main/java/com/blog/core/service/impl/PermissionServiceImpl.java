package com.blog.core.service.impl;

import com.blog.api.model.Permission;
import com.blog.core.service.PermissionService;
import com.blog.persistence.repository.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PermissionServiceImpl implements PermissionService {
    
    @Autowired
    private PermissionRepository permissionRepository;
    
    @Override
    public Permission createPermission(Permission permission) {
        // 检查权限名是否已存在
        if (permissionRepository.existsByName(permission.getName())) {
            throw new RuntimeException("权限名已存在: " + permission.getName());
        }
        
        // 检查资源和操作组合是否已存在
        if (permissionRepository.existsByResourceAndAction(permission.getResource(), permission.getAction())) {
            throw new RuntimeException("资源和操作组合已存在: " + permission.getResource() + ":" + permission.getAction());
        }
        
        // 设置创建时间
        permission.setCreatedAt(LocalDateTime.now());
        
        return permissionRepository.save(permission);
    }
    
    @Override
    public Permission updatePermission(Long permissionId, Permission permission) {
        Permission existingPermission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new RuntimeException("权限不存在: " + permissionId));
        
        // 检查权限名是否被其他权限使用
        if (!existingPermission.getName().equals(permission.getName()) && 
            permissionRepository.existsByName(permission.getName())) {
            throw new RuntimeException("权限名已存在: " + permission.getName());
        }
        
        // 检查资源和操作组合是否被其他权限使用
        if ((!existingPermission.getResource().equals(permission.getResource()) || 
             !existingPermission.getAction().equals(permission.getAction())) &&
            permissionRepository.existsByResourceAndAction(permission.getResource(), permission.getAction())) {
            throw new RuntimeException("资源和操作组合已存在: " + permission.getResource() + ":" + permission.getAction());
        }
        
        // 更新权限信息
        existingPermission.setName(permission.getName());
        existingPermission.setDescription(permission.getDescription());
        existingPermission.setResource(permission.getResource());
        existingPermission.setAction(permission.getAction());
        
        return permissionRepository.save(existingPermission);
    }
    
    @Override
    public void deletePermission(Long permissionId) {
        if (!permissionRepository.findById(permissionId).isPresent()) {
            throw new RuntimeException("权限不存在: " + permissionId);
        }
        
        // 注意：删除权限前应该检查是否有角色正在使用此权限
        // 这里为简化暂时直接删除，实际应用中需要更谨慎的处理
        
        permissionRepository.deleteById(permissionId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Permission> getPermissionById(Long permissionId) {
        return permissionRepository.findById(permissionId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Permission> getPermissionByName(String name) {
        return permissionRepository.findByName(name);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Permission> getAllPermissions() {
        return permissionRepository.findAll();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Permission> getPermissions(Pageable pageable) {
        return permissionRepository.findAll(pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Permission> getPermissionsByResource(String resource) {
        return permissionRepository.findByResource(resource);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Permission> getPermissionByResourceAndAction(String resource, String action) {
        return permissionRepository.findByResourceAndAction(resource, action);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return permissionRepository.existsByName(name);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByResourceAndAction(String resource, String action) {
        return permissionRepository.existsByResourceAndAction(resource, action);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<String> getAllResources() {
        return permissionRepository.findAllResources();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<String> getActionsByResource(String resource) {
        return permissionRepository.findActionsByResource(resource);
    }
}