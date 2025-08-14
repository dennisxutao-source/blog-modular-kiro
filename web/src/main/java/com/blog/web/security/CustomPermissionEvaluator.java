package com.blog.web.security;

import com.blog.web.util.PermissionChecker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * 自定义权限评估器
 * 集成Spring Security权限表达式
 */
@Component
public class CustomPermissionEvaluator implements PermissionEvaluator {
    
    @Autowired
    private PermissionChecker permissionChecker;
    
    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        String permissionStr = permission.toString();
        
        // 解析权限字符串，格式：resource:action
        String[] parts = permissionStr.split(":");
        if (parts.length != 2) {
            return false;
        }
        
        String resource = parts[0];
        String action = parts[1];
        
        return permissionChecker.hasPermission(userPrincipal.getId(), resource, action);
    }
    
    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return hasPermission(authentication, null, permission);
    }
}