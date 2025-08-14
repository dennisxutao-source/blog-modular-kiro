package com.blog.web.api;

import com.blog.api.model.User;
import com.blog.core.service.UserService;
import com.blog.web.api.dto.*;
import com.blog.web.security.JwtTokenProvider;
import com.blog.web.security.UserPrincipal;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            String accessToken = tokenProvider.generateToken(userPrincipal);
            String refreshToken = tokenProvider.generateRefreshToken(userPrincipal);

            // 更新最后登录时间
            userService.updateLastLoginTime(userPrincipal.getId());

            // 获取用户信息
            Optional<User> userOpt = userService.getUserById(userPrincipal.getId());
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("用户信息获取失败"));
            }

            UserDto userDto = UserDto.fromUser(userOpt.get());
            LoginResponse loginResponse = new LoginResponse(accessToken, refreshToken, userDto);

            return ResponseEntity.ok(ApiResponse.success("登录成功", loginResponse));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("用户名或密码错误"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDto>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            // 检查用户名是否已存在
            if (userService.existsByUsername(registerRequest.getUsername())) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("用户名已存在"));
            }

            // 检查邮箱是否已存在
            if (userService.existsByEmail(registerRequest.getEmail())) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("邮箱已存在"));
            }

            // 创建新用户
            User user = new User(
                    registerRequest.getUsername(),
                    registerRequest.getEmail(),
                    registerRequest.getPassword(), // 这里传入明文密码，在service中会加密
                    registerRequest.getFullName());

            User createdUser = userService.createUser(user);

            // 为新用户分配默认角色（USER）
            // 这里需要先查找USER角色的ID，暂时跳过，后续在角色管理中实现

            UserDto userDto = UserDto.fromUser(createdUser);

            return ResponseEntity.ok(ApiResponse.success("注册成功", userDto));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("注册失败: " + e.getMessage()));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        try {
            String refreshToken = request.getRefreshToken();

            // 验证刷新令牌
            if (!tokenProvider.validateToken(refreshToken) || !tokenProvider.isRefreshToken(refreshToken)) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("无效的刷新令牌"));
            }

            // 从刷新令牌中获取用户信息
            String username = tokenProvider.getUsernameFromToken(refreshToken);
            Optional<User> userOpt = userService.getUserByUsername(username);

            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("用户不存在"));
            }

            User user = userOpt.get();
            UserPrincipal userPrincipal = UserPrincipal.create(convertToUserEntity(user));

            // 生成新的访问令牌和刷新令牌
            String newAccessToken = tokenProvider.generateToken(userPrincipal);
            String newRefreshToken = tokenProvider.generateRefreshToken(userPrincipal);

            TokenResponse tokenResponse = new TokenResponse(newAccessToken, newRefreshToken);

            return ResponseEntity.ok(ApiResponse.success("令牌刷新成功", tokenResponse));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("令牌刷新失败"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        // 清除安全上下文
        SecurityContextHolder.clearContext();

        // 在实际应用中，这里可以将令牌加入黑名单
        // 目前简单返回成功响应

        return ResponseEntity.ok(ApiResponse.success("退出登录成功", null));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDto>> getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("用户未登录"));
            }

            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            Optional<User> userOpt = userService.getUserById(userPrincipal.getId());

            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("用户信息获取失败"));
            }

            UserDto userDto = UserDto.fromUser(userOpt.get());

            return ResponseEntity.ok(ApiResponse.success("获取用户信息成功", userDto));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("获取用户信息失败"));
        }
    }

    // 临时方法，用于将User模型转换为UserEntity
    // 在实际应用中，这个转换应该在repository层处理
    private com.blog.persistence.entity.UserEntity convertToUserEntity(User user) {
        com.blog.persistence.entity.UserEntity entity = new com.blog.persistence.entity.UserEntity();
        entity.setId(user.getId());
        entity.setUsername(user.getUsername());
        entity.setEmail(user.getEmail());
        entity.setPasswordHash(user.getPasswordHash());
        entity.setFullName(user.getFullName());
        entity.setAvatarUrl(user.getAvatarUrl());

        if (user.getStatus() != null) {
            entity.setStatus(com.blog.persistence.entity.UserEntity.UserStatus.valueOf(user.getStatus().name()));
        }

        entity.setCreatedAt(user.getCreatedAt());
        entity.setUpdatedAt(user.getUpdatedAt());
        entity.setLastLoginAt(user.getLastLoginAt());

        return entity;
    }
}