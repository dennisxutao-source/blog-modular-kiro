package com.blog.web.util;

import com.blog.core.service.UserService;
import com.blog.web.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class PermissionChecker {
    
    @Autowired
    private UserService userService;
    
    /**
     * 检查当前用户是否有指定权限
     */
    public boolean hasPermission(String resource, String action) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        
        // 从认证信息中获取用户ID
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserPrincipal) {
            UserPrincipal userPrincipal = (UserPrincipal) principal;
            return userService.hasPermission(userPrincipal.getId(), resource, action);
        }
        
        return false;
    }
    
    /**
     * 检查指定用户是否有指定权限
     */
    public boolean hasPermission(Long userId, String resource, String action) {
        return userService.hasPermission(userId, resource, action);
    }
    
    /**
     * 检查当前用户是否有管理员权限
     */
    public boolean isAdmin() {
        return hasPermission("system", "admin");
    }
    
    /**
     * 检查当前用户是否有编辑权限
     */
    public boolean canEdit(String resource) {
        return hasPermission(resource, "write") || isAdmin();
    }
    
    /**
     * 检查当前用户是否有删除权限
     */
    public boolean canDelete(String resource) {
        return hasPermission(resource, "delete") || isAdmin();
    }
    
    /**
     * 检查当前用户是否有读取权限
     */
    public boolean canRead(String resource) {
        return hasPermission(resource, "read") || isAdmin();
    }
    
    /**
     * 获取当前用户ID
     */
    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserPrincipal) {
            UserPrincipal userPrincipal = (UserPrincipal) principal;
            return userPrincipal.getId();
        }
        
        return null;
    }
    
    /**
     * 检查当前用户是否为指定用户（用于检查是否可以编辑自己的内容）
     */
    public boolean isCurrentUser(Long userId) {
        Long currentUserId = getCurrentUserId();
        return currentUserId != null && currentUserId.equals(userId);
    }
}