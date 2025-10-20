package com.blog.web.api;

import com.blog.api.model.Role;
import com.blog.core.service.RoleService;
import com.blog.web.api.dto.ApiResponse;
import com.blog.web.security.annotation.RequirePermission;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * 角色管理控制器
 * 提供角色管理相关的REST接口
 */
@RestController
@RequestMapping("/api/roles")
public class RoleController {
    
    @Autowired
    private RoleService roleService;
    
    /**
     * 获取所有角色
     * GET /api/roles
     */
    @GetMapping
    @RequirePermission(resource = "role", action = "read", description = "查看角色列表")
    public ResponseEntity<ApiResponse<List<Role>>> getAllRoles() {
        try {
            List<Role> roles = roleService.getAllRoles();
            return ResponseEntity.ok(ApiResponse.success("获取角色列表成功", roles));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("获取角色列表失败: " + e.getMessage()));
        }
    }
    
    /**
     * 根据ID获取角色详情
     * GET /api/roles/{id}
     */
    @GetMapping("/{id}")
    @RequirePermission(resource = "role", action = "read", description = "查看角色详情")
    public ResponseEntity<ApiResponse<Role>> getRoleById(@PathVariable Long id) {
        try {
            Optional<Role> roleOpt = roleService.getRoleById(id);
            if (roleOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok(ApiResponse.success("获取角色详情成功", roleOpt.get()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("获取角色详情失败: " + e.getMessage()));
        }
    }
    
    /**
     * 创建新角色
     * POST /api/roles
     */
    @PostMapping
    @RequirePermission(resource = "role", action = "write", description = "创建角色")
    public ResponseEntity<ApiResponse<Role>> createRole(@Valid @RequestBody CreateRoleRequest request) {
        try {
            // 检查角色名是否已存在
            if (roleService.existsByName(request.getName())) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("角色名已存在"));
            }
            
            Role role = new Role(request.getName(), request.getDescription(), false);
            Role createdRole = roleService.createRole(role);
            
            return ResponseEntity.ok(ApiResponse.success("角色创建成功", createdRole));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("角色创建失败: " + e.getMessage()));
        }
    }
    
    /**
     * 更新角色信息
     * PUT /api/roles/{id}
     */
    @PutMapping("/{id}")
    @RequirePermission(resource = "role", action = "write", description = "更新角色")
    public ResponseEntity<ApiResponse<Role>> updateRole(
            @PathVariable Long id, 
            @Valid @RequestBody UpdateRoleRequest request) {
        
        try {
            // 检查角色是否存在
            Optional<Role> existingRoleOpt = roleService.getRoleById(id);
            if (existingRoleOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            Role existingRole = existingRoleOpt.get();
            
            // 检查角色名是否被其他角色使用
            if (!existingRole.getName().equals(request.getName()) && 
                roleService.existsByName(request.getName())) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("角色名已被其他角色使用"));
            }
            
            Role updatedRole = new Role(request.getName(), request.getDescription(), existingRole.getIsSystem());
            updatedRole.setId(id);
            
            Role result = roleService.updateRole(id, updatedRole);
            return ResponseEntity.ok(ApiResponse.success("角色更新成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("角色更新失败: " + e.getMessage()));
        }
    }
    
    /**
     * 删除角色
     * DELETE /api/roles/{id}
     */
    @DeleteMapping("/{id}")
    @RequirePermission(resource = "role", action = "delete", description = "删除角色")
    public ResponseEntity<ApiResponse<Void>> deleteRole(@PathVariable Long id) {
        try {
            roleService.deleteRole(id);
            return ResponseEntity.ok(ApiResponse.success("角色删除成功", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("角色删除失败: " + e.getMessage()));
        }
    }
    
    /**
     * 为角色分配权限
     * POST /api/roles/{id}/permissions/{permissionId}
     */
    @PostMapping("/{id}/permissions/{permissionId}")
    @RequirePermission(resource = "role", action = "write", description = "分配角色权限")
    public ResponseEntity<ApiResponse<Void>> assignPermission(
            @PathVariable Long id, 
            @PathVariable Long permissionId) {
        
        try {
            roleService.assignPermission(id, permissionId);
            return ResponseEntity.ok(ApiResponse.success("权限分配成功", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("权限分配失败: " + e.getMessage()));
        }
    }
    
    /**
     * 移除角色权限
     * DELETE /api/roles/{id}/permissions/{permissionId}
     */
    @DeleteMapping("/{id}/permissions/{permissionId}")
    @RequirePermission(resource = "role", action = "write", description = "移除角色权限")
    public ResponseEntity<ApiResponse<Void>> removePermission(
            @PathVariable Long id, 
            @PathVariable Long permissionId) {
        
        try {
            roleService.removePermission(id, permissionId);
            return ResponseEntity.ok(ApiResponse.success("权限移除成功", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("权限移除失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取角色的所有权限
     * GET /api/roles/{id}/permissions
     */
    @GetMapping("/{id}/permissions")
    @RequirePermission(resource = "role", action = "read", description = "查看角色权限")
    public ResponseEntity<ApiResponse<Set<String>>> getRolePermissions(@PathVariable Long id) {
        try {
            Set<String> permissions = roleService.getRolePermissions(id);
            return ResponseEntity.ok(ApiResponse.success("获取角色权限成功", permissions));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("获取角色权限失败: " + e.getMessage()));
        }
    }
    
    /**
     * 创建角色请求DTO
     */
    public static class CreateRoleRequest {
        private String name;
        private String description;
        
        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
    
    /**
     * 更新角色请求DTO
     */
    public static class UpdateRoleRequest {
        private String name;
        private String description;
        
        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
}