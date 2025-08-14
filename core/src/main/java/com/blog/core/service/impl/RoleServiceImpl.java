package com.blog.core.service.impl;

import com.blog.api.model.Role;
import com.blog.core.service.RoleService;
import com.blog.persistence.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class RoleServiceImpl implements RoleService {
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Override
    public Role createRole(Role role) {
        // 检查角色名是否已存在
        if (roleRepository.existsByName(role.getName())) {
            throw new RuntimeException("角色名已存在: " + role.getName());
        }
        
        // 设置默认值
        role.setIsSystem(false); // 新创建的角色默认不是系统角色
        role.setCreatedAt(LocalDateTime.now());
        
        return roleRepository.save(role);
    }
    
    @Override
    public Role updateRole(Long roleId, Role role) {
        Role existingRole = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("角色不存在: " + roleId));
        
        // 系统角色不允许修改名称
        if (existingRole.getIsSystem() && !existingRole.getName().equals(role.getName())) {
            throw new RuntimeException("系统角色不允许修改名称");
        }
        
        // 检查角色名是否被其他角色使用
        if (!existingRole.getName().equals(role.getName()) && 
            roleRepository.existsByName(role.getName())) {
            throw new RuntimeException("角色名已存在: " + role.getName());
        }
        
        // 更新角色信息
        existingRole.setName(role.getName());
        existingRole.setDescription(role.getDescription());
        // 系统角色的isSystem字段不允许修改
        if (!existingRole.getIsSystem()) {
            existingRole.setIsSystem(role.getIsSystem());
        }
        
        return roleRepository.save(existingRole);
    }
    
    @Override
    public void deleteRole(Long roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("角色不存在: " + roleId));
        
        // 系统角色不允许删除
        if (role.getIsSystem()) {
            throw new RuntimeException("系统角色不允许删除");
        }
        
        roleRepository.deleteById(roleId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Role> getRoleById(Long roleId) {
        return roleRepository.findById(roleId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Role> getRoleByName(String name) {
        return roleRepository.findByName(name);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Role> getRoles(Pageable pageable) {
        return roleRepository.findAll(pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return roleRepository.existsByName(name);
    }
    
    @Override
    public void assignPermission(Long roleId, Long permissionId) {
        if (!roleRepository.findById(roleId).isPresent()) {
            throw new RuntimeException("角色不存在: " + roleId);
        }
        roleRepository.assignPermission(roleId, permissionId);
    }
    
    @Override
    public void removePermission(Long roleId, Long permissionId) {
        if (!roleRepository.findById(roleId).isPresent()) {
            throw new RuntimeException("角色不存在: " + roleId);
        }
        roleRepository.removePermission(roleId, permissionId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Set<String> getRolePermissions(Long roleId) {
        return roleRepository.getRolePermissions(roleId);
    }
    
    @Override
    public void assignPermissions(Long roleId, Set<Long> permissionIds) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("角色不存在: " + roleId));
        
        // 先清除现有权限，再分配新权限
        Set<String> currentPermissions = roleRepository.getRolePermissions(roleId);
        // 这里需要实现批量权限分配逻辑
        // 为简化，暂时逐个分配
        for (Long permissionId : permissionIds) {
            roleRepository.assignPermission(roleId, permissionId);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Role> getNonSystemRoles() {
        return roleRepository.findByIsSystemFalse();
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isSystemRole(Long roleId) {
        return roleRepository.isSystemRole(roleId);
    }
}