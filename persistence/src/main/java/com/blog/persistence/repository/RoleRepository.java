package com.blog.persistence.repository;

import com.blog.api.model.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RoleRepository {
    
    Role save(Role role);
    
    Optional<Role> findById(Long id);
    
    Optional<Role> findByName(String name);
    
    List<Role> findAll();
    
    Page<Role> findAll(Pageable pageable);
    
    void deleteById(Long id);
    
    boolean existsByName(String name);
    
    void assignPermission(Long roleId, Long permissionId);
    
    void removePermission(Long roleId, Long permissionId);
    
    Set<String> getRolePermissions(Long roleId);
    
    List<Role> findByIsSystemFalse();
    
    boolean isSystemRole(Long roleId);
}