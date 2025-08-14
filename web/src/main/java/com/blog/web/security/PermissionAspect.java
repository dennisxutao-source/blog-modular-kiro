package com.blog.web.security;

import com.blog.web.security.annotation.RequirePermission;
import com.blog.web.util.PermissionChecker;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * 权限检查切面
 * 使用AOP方式处理@RequirePermission注解
 */
@Aspect
@Component
public class PermissionAspect {
    
    @Autowired
    private PermissionChecker permissionChecker;
    
    @Before("@annotation(requirePermission)")
    public void checkPermission(JoinPoint joinPoint, RequirePermission requirePermission) {
        // 获取当前认证用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("用户未认证");
        }
        
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        
        // 检查权限
        boolean hasPermission = permissionChecker.hasPermission(
            userPrincipal.getId(), 
            requirePermission.resource(), 
            requirePermission.action()
        );
        
        if (!hasPermission) {
            throw new AccessDeniedException("权限不足：需要 " + requirePermission.resource() + ":" + requirePermission.action());
        }
    }
    
    @Before("@within(requirePermission)")
    public void checkClassPermission(JoinPoint joinPoint, RequirePermission requirePermission) {
        checkPermission(joinPoint, requirePermission);
    }
}