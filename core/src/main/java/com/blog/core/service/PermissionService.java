package com.blog.core.service;

import com.blog.api.model.Permission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PermissionService {
    
    /**
     * 创建权限
     */
    Permission createPermission(Permission permission);
    
    /**
     * 更新权限
     */
    Permission updatePermission(Long permissionId, Permission permission);
    
    /**
     * 删除权限
     */
    void deletePermission(Long permissionId);
    
    /**
     * 根据ID获取权限
     */
    Optional<Permission> getPermissionById(Long permissionId);
    
    /**
     * 根据名称获取权限
     */
    Optional<Permission> getPermissionByName(String name);
    
    /**
     * 获取所有权限
     */
    List<Permission> getAllPermissions();
    
    /**
     * 获取权限列表（分页）
     */
    Page<Permission> getPermissions(Pageable pageable);
    
    /**
     * 根据资源获取权限
     */
    List<Permission> getPermissionsByResource(String resource);
    
    /**
     * 根据资源和操作获取权限
     */
    Optional<Permission> getPermissionByResourceAndAction(String resource, String action);
    
    /**
     * 检查权限名是否存在
     */
    boolean existsByName(String name);
    
    /**
     * 检查资源和操作组合是否存在
     */
    boolean existsByResourceAndAction(String resource, String action);
    
    /**
     * 获取所有资源列表
     */
    List<String> getAllResources();
    
    /**
     * 获取指定资源的所有操作
     */
    List<String> getActionsByResource(String resource);
}