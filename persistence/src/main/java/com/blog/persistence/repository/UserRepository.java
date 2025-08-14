package com.blog.persistence.repository;

import com.blog.api.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.Set;

public interface UserRepository {
    
    User save(User user);
    
    Optional<User> findById(Long id);
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    Page<User> findAll(Pageable pageable);
    
    void deleteById(Long id);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    void assignRole(Long userId, Long roleId);
    
    void removeRole(Long userId, Long roleId);
    
    Set<String> getUserRoles(Long userId);
    
    boolean hasPermission(Long userId, String resource, String action);
    
    void updateLastLoginTime(Long userId);
}