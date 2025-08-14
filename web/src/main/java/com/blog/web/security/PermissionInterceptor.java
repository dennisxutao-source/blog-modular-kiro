package com.blog.web.security;

import com.blog.web.security.annotation.RequirePermission;
import com.blog.web.util.PermissionChecker;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 权限拦截器
 * 处理@RequirePermission注解的权限检查
 */
@Component
public class PermissionInterceptor implements HandlerInterceptor {
    
    @Autowired
    private PermissionChecker permissionChecker;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        
        // 检查方法级别的权限注解
        RequirePermission methodPermission = handlerMethod.getMethodAnnotation(RequirePermission.class);
        
        // 检查类级别的权限注解
        RequirePermission classPermission = handlerMethod.getBeanType().getAnnotation(RequirePermission.class);
        
        RequirePermission permission = methodPermission != null ? methodPermission : classPermission;
        
        if (permission == null) {
            return true; // 没有权限注解，允许访问
        }
        
        // 获取当前认证用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("用户未认证");
        }
        
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        
        // 检查权限
        boolean hasPermission = permissionChecker.hasPermission(
            userPrincipal.getId(), 
            permission.resource(), 
            permission.action()
        );
        
        if (!hasPermission) {
            throw new AccessDeniedException("权限不足：需要 " + permission.resource() + ":" + permission.action());
        }
        
        return true;
    }
}