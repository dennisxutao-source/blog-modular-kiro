package com.blog.persistence.repository;

import com.blog.api.model.Permission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PermissionRepository {
    
    Permission save(Permission permission);
    
    Optional<Permission> findById(Long id);
    
    Optional<Permission> findByName(String name);
    
    List<Permission> findAll();
    
    Page<Permission> findAll(Pageable pageable);
    
    void deleteById(Long id);
    
    List<Permission> findByResource(String resource);
    
    Optional<Permission> findByResourceAndAction(String resource, String action);
    
    boolean existsByName(String name);
    
    boolean existsByResourceAndAction(String resource, String action);
    
    List<String> findAllResources();
    
    List<String> findActionsByResource(String resource);
}