package com.blog.web.api;

import com.blog.api.model.Permission;
import com.blog.core.service.PermissionService;
import com.blog.web.api.dto.ApiResponse;
import com.blog.web.security.annotation.RequirePermission;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 权限管理控制器
 * 提供权限管理相关的REST接口
 */
@RestController
@RequestMapping("/api/permissions")
public class PermissionController {
    
    @Autowired
    private PermissionService permissionService;
    
    /**
     * 获取所有权限
     * GET /api/permissions
     */
    @GetMapping
    @RequirePermission(resource = "system", action = "admin", description = "查看权限列表")
    public ResponseEntity<ApiResponse<List<Permission>>> getAllPermissions() {
        try {
            List<Permission> permissions = permissionService.getAllPermissions();
            return ResponseEntity.ok(ApiResponse.success("获取权限列表成功", permissions));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("获取权限列表失败: " + e.getMessage()));
        }
    }
    
    /**
     * 根据ID获取权限详情
     * GET /api/permissions/{id}
     */
    @GetMapping("/{id}")
    @RequirePermission(resource = "system", action = "admin", description = "查看权限详情")
    public ResponseEntity<ApiResponse<Permission>> getPermissionById(@PathVariable Long id) {
        try {
            Optional<Permission> permissionOpt = permissionService.getPermissionById(id);
            if (permissionOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok(ApiResponse.success("获取权限详情成功", permissionOpt.get()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("获取权限详情失败: " + e.getMessage()));
        }
    }
    
    /**
     * 创建新权限
     * POST /api/permissions
     */
    @PostMapping
    @RequirePermission(resource = "system", action = "admin", description = "创建权限")
    public ResponseEntity<ApiResponse<Permission>> createPermission(@Valid @RequestBody CreatePermissionRequest request) {
        try {
            // 检查权限名是否已存在
            if (permissionService.existsByName(request.getName())) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("权限名已存在"));
            }
            
            Permission permission = new Permission(
                request.getName(), 
                request.getDescription(),
                request.getResource(), 
                request.getAction()
            );
            
            Permission createdPermission = permissionService.createPermission(permission);
            return ResponseEntity.ok(ApiResponse.success("权限创建成功", createdPermission));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("权限创建失败: " + e.getMessage()));
        }
    }
    
    /**
     * 更新权限信息
     * PUT /api/permissions/{id}
     */
    @PutMapping("/{id}")
    @RequirePermission(resource = "system", action = "admin", description = "更新权限")
    public ResponseEntity<ApiResponse<Permission>> updatePermission(
            @PathVariable Long id, 
            @Valid @RequestBody UpdatePermissionRequest request) {
        
        try {
            // 检查权限是否存在
            Optional<Permission> existingPermissionOpt = permissionService.getPermissionById(id);
            if (existingPermissionOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            Permission existingPermission = existingPermissionOpt.get();
            
            // 检查权限名是否被其他权限使用
            if (!existingPermission.getName().equals(request.getName()) && 
                permissionService.existsByName(request.getName())) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("权限名已被其他权限使用"));
            }
            
            Permission updatedPermission = new Permission(
                request.getName(), 
                request.getDescription(),
                request.getResource(), 
                request.getAction()
            );
            updatedPermission.setId(id);
            
            Permission result = permissionService.updatePermission(id, updatedPermission);
            return ResponseEntity.ok(ApiResponse.success("权限更新成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("权限更新失败: " + e.getMessage()));
        }
    }
    
    /**
     * 删除权限
     * DELETE /api/permissions/{id}
     */
    @DeleteMapping("/{id}")
    @RequirePermission(resource = "system", action = "admin", description = "删除权限")
    public ResponseEntity<ApiResponse<Void>> deletePermission(@PathVariable Long id) {
        try {
            permissionService.deletePermission(id);
            return ResponseEntity.ok(ApiResponse.success("权限删除成功", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("权限删除失败: " + e.getMessage()));
        }
    }
    
    /**
     * 根据资源获取权限列表
     * GET /api/permissions/by-resource/{resource}
     */
    @GetMapping("/by-resource/{resource}")
    @RequirePermission(resource = "system", action = "admin", description = "按资源查看权限")
    public ResponseEntity<ApiResponse<List<Permission>>> getPermissionsByResource(@PathVariable String resource) {
        try {
            List<Permission> permissions = permissionService.getPermissionsByResource(resource);
            return ResponseEntity.ok(ApiResponse.success("获取资源权限成功", permissions));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("获取资源权限失败: " + e.getMessage()));
        }
    }
    
    /**
     * 创建权限请求DTO
     */
    public static class CreatePermissionRequest {
        private String name;
        private String resource;
        private String action;
        private String description;
        
        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getResource() { return resource; }
        public void setResource(String resource) { this.resource = resource; }
        
        public String getAction() { return action; }
        public void setAction(String action) { this.action = action; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
    
    /**
     * 更新权限请求DTO
     */
    public static class UpdatePermissionRequest {
        private String name;
        private String resource;
        private String action;
        private String description;
        
        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getResource() { return resource; }
        public void setResource(String resource) { this.resource = resource; }
        
        public String getAction() { return action; }
        public void setAction(String action) { this.action = action; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
}