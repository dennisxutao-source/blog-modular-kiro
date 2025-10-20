package com.blog.web.api;

import com.blog.api.model.User;
import com.blog.core.service.UserService;
import com.blog.web.api.dto.ApiResponse;
import com.blog.web.api.dto.UserDto;
import com.blog.web.security.annotation.RequirePermission;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;

/**
 * 用户管理控制器
 * 提供用户管理相关的REST接口
 */
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 获取用户列表（分页）
     * GET /api/users
     */
    @GetMapping
    @RequirePermission(resource = "user", action = "read", description = "查看用户列表")
    public ResponseEntity<ApiResponse<Page<UserDto>>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<User> users = userService.getUsers(pageable);
            
            // 转换为DTO
            Page<UserDto> userDtos = users.map(UserDto::fromUser);
            
            return ResponseEntity.ok(ApiResponse.success("获取用户列表成功", userDtos));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("获取用户列表失败: " + e.getMessage()));
        }
    }
    
    /**
     * 根据ID获取用户详情
     * GET /api/users/{id}
     */
    @GetMapping("/{id}")
    @RequirePermission(resource = "user", action = "read", description = "查看用户详情")
    public ResponseEntity<ApiResponse<UserDto>> getUserById(@PathVariable Long id) {
        try {
            Optional<User> userOpt = userService.getUserById(id);
            if (userOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            UserDto userDto = UserDto.fromUser(userOpt.get());
            return ResponseEntity.ok(ApiResponse.success("获取用户详情成功", userDto));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("获取用户详情失败: " + e.getMessage()));
        }
    }
    
    /**
     * 创建新用户
     * POST /api/users
     */
    @PostMapping
    @RequirePermission(resource = "user", action = "write", description = "创建用户")
    public ResponseEntity<ApiResponse<UserDto>> createUser(@Valid @RequestBody CreateUserRequest request) {
        try {
            // 检查用户名是否已存在
            if (userService.existsByUsername(request.getUsername())) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("用户名已存在"));
            }
            
            // 检查邮箱是否已存在
            if (userService.existsByEmail(request.getEmail())) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("邮箱已存在"));
            }
            
            // 创建用户
            User user = new User(
                request.getUsername(),
                request.getEmail(),
                request.getPassword(),
                request.getFullName()
            );
            
            User createdUser = userService.createUser(user);
            UserDto userDto = UserDto.fromUser(createdUser);
            
            return ResponseEntity.ok(ApiResponse.success("用户创建成功", userDto));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("用户创建失败: " + e.getMessage()));
        }
    }
    
    /**
     * 更新用户信息
     * PUT /api/users/{id}
     */
    @PutMapping("/{id}")
    @RequirePermission(resource = "user", action = "write", description = "更新用户")
    public ResponseEntity<ApiResponse<UserDto>> updateUser(
            @PathVariable Long id, 
            @Valid @RequestBody UpdateUserRequest request) {
        
        try {
            // 检查用户是否存在
            Optional<User> existingUserOpt = userService.getUserById(id);
            if (existingUserOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            User existingUser = existingUserOpt.get();
            
            // 检查用户名是否被其他用户使用
            if (!existingUser.getUsername().equals(request.getUsername()) && 
                userService.existsByUsername(request.getUsername())) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("用户名已被其他用户使用"));
            }
            
            // 检查邮箱是否被其他用户使用
            if (!existingUser.getEmail().equals(request.getEmail()) && 
                userService.existsByEmail(request.getEmail())) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("邮箱已被其他用户使用"));
            }
            
            // 更新用户信息
            User updatedUser = new User(
                request.getUsername(),
                request.getEmail(),
                existingUser.getPasswordHash(), // 保持原密码
                request.getFullName()
            );
            updatedUser.setId(id);
            
            User result = userService.updateUser(id, updatedUser);
            UserDto userDto = UserDto.fromUser(result);
            
            return ResponseEntity.ok(ApiResponse.success("用户更新成功", userDto));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("用户更新失败: " + e.getMessage()));
        }
    }
    
    /**
     * 删除用户
     * DELETE /api/users/{id}
     */
    @DeleteMapping("/{id}")
    @RequirePermission(resource = "user", action = "delete", description = "删除用户")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok(ApiResponse.success("用户删除成功", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("用户删除失败: " + e.getMessage()));
        }
    }
    
    /**
     * 为用户分配角色
     * POST /api/users/{id}/roles/{roleId}
     */
    @PostMapping("/{id}/roles/{roleId}")
    @RequirePermission(resource = "user", action = "write", description = "分配用户角色")
    public ResponseEntity<ApiResponse<Void>> assignRole(
            @PathVariable Long id, 
            @PathVariable Long roleId) {
        
        try {
            userService.assignRole(id, roleId);
            return ResponseEntity.ok(ApiResponse.success("角色分配成功", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("角色分配失败: " + e.getMessage()));
        }
    }
    
    /**
     * 移除用户角色
     * DELETE /api/users/{id}/roles/{roleId}
     */
    @DeleteMapping("/{id}/roles/{roleId}")
    @RequirePermission(resource = "user", action = "write", description = "移除用户角色")
    public ResponseEntity<ApiResponse<Void>> removeRole(
            @PathVariable Long id, 
            @PathVariable Long roleId) {
        
        try {
            userService.removeRole(id, roleId);
            return ResponseEntity.ok(ApiResponse.success("角色移除成功", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("角色移除失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取用户的所有角色
     * GET /api/users/{id}/roles
     */
    @GetMapping("/{id}/roles")
    @RequirePermission(resource = "user", action = "read", description = "查看用户角色")
    public ResponseEntity<ApiResponse<Set<String>>> getUserRoles(@PathVariable Long id) {
        try {
            Set<String> roles = userService.getUserRoles(id);
            return ResponseEntity.ok(ApiResponse.success("获取用户角色成功", roles));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("获取用户角色失败: " + e.getMessage()));
        }
    }
    
    /**
     * 激活用户
     * PUT /api/users/{id}/activate
     */
    @PutMapping("/{id}/activate")
    @RequirePermission(resource = "user", action = "write", description = "激活用户")
    public ResponseEntity<ApiResponse<Void>> activateUser(@PathVariable Long id) {
        try {
            userService.activateUser(id);
            return ResponseEntity.ok(ApiResponse.success("用户激活成功", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("用户激活失败: " + e.getMessage()));
        }
    }
    
    /**
     * 停用用户
     * PUT /api/users/{id}/deactivate
     */
    @PutMapping("/{id}/deactivate")
    @RequirePermission(resource = "user", action = "write", description = "停用用户")
    public ResponseEntity<ApiResponse<Void>> deactivateUser(@PathVariable Long id) {
        try {
            userService.deactivateUser(id);
            return ResponseEntity.ok(ApiResponse.success("用户停用成功", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("用户停用失败: " + e.getMessage()));
        }
    }
    
    /**
     * 锁定用户
     * PUT /api/users/{id}/lock
     */
    @PutMapping("/{id}/lock")
    @RequirePermission(resource = "user", action = "write", description = "锁定用户")
    public ResponseEntity<ApiResponse<Void>> lockUser(@PathVariable Long id) {
        try {
            userService.lockUser(id);
            return ResponseEntity.ok(ApiResponse.success("用户锁定成功", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("用户锁定失败: " + e.getMessage()));
        }
    }
    
    /**
     * 解锁用户
     * PUT /api/users/{id}/unlock
     */
    @PutMapping("/{id}/unlock")
    @RequirePermission(resource = "user", action = "write", description = "解锁用户")
    public ResponseEntity<ApiResponse<Void>> unlockUser(@PathVariable Long id) {
        try {
            userService.unlockUser(id);
            return ResponseEntity.ok(ApiResponse.success("用户解锁成功", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("用户解锁失败: " + e.getMessage()));
        }
    }
    
    /**
     * 创建用户请求DTO
     */
    public static class CreateUserRequest {
        private String username;
        private String email;
        private String password;
        private String fullName;
        
        // Getters and Setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
    }
    
    /**
     * 更新用户请求DTO
     */
    public static class UpdateUserRequest {
        private String username;
        private String email;
        private String fullName;
        
        // Getters and Setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
    }
}