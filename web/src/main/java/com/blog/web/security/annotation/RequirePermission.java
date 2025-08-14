package com.blog.web.security.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限控制注解
 * 用于方法级别的权限控制
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequirePermission {
    
    /**
     * 需要的权限资源
     */
    String resource();
    
    /**
     * 需要的权限操作
     */
    String action();
    
    /**
     * 权限描述（可选）
     */
    String description() default "";
}