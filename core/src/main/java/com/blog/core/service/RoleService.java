package com.blog.core.service;

import com.blog.api.model.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RoleService {
    
    /**
     * 创建角色
     */
    Role createRole(Role role);
    
    /**
     * 更新角色
     */
    Role updateRole(Long roleId, Role role);
    
    /**
     * 删除角色
     */
    void deleteRole(Long roleId);
    
    /**
     * 根据ID获取角色
     */
    Optional<Role> getRoleById(Long roleId);
    
    /**
     * 根据名称获取角色
     */
    Optional<Role> getRoleByName(String name);
    
    /**
     * 获取所有角色
     */
    List<Role> getAllRoles();
    
    /**
     * 获取角色列表（分页）
     */
    Page<Role> getRoles(Pageable pageable);
    
    /**
     * 检查角色名是否存在
     */
    boolean existsByName(String name);
    
    /**
     * 为角色分配权限
     */
    void assignPermission(Long roleId, Long permissionId);
    
    /**
     * 移除角色权限
     */
    void removePermission(Long roleId, Long permissionId);
    
    /**
     * 获取角色的所有权限
     */
    Set<String> getRolePermissions(Long roleId);
    
    /**
     * 批量为角色分配权限
     */
    void assignPermissions(Long roleId, Set<Long> permissionIds);
    
    /**
     * 获取非系统角色（可删除的角色）
     */
    List<Role> getNonSystemRoles();
    
    /**
     * 检查角色是否为系统角色
     */
    boolean isSystemRole(Long roleId);
}