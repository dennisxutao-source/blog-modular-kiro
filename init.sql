-- 创建博客数据库
CREATE DATABASE IF NOT EXISTS blog_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE blog_db;

-- 创建文章表
CREATE TABLE IF NOT EXISTS articles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    author VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 创建用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100),
    avatar_url VARCHAR(500),
    status ENUM('ACTIVE', 'INACTIVE', 'LOCKED') NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    last_login_at TIMESTAMP NULL,
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_status (status)
);

-- 创建角色表
CREATE TABLE IF NOT EXISTS roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(200),
    is_system BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_name (name)
);

-- 创建权限表
CREATE TABLE IF NOT EXISTS permissions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(200),
    resource VARCHAR(50) NOT NULL,
    action VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_resource_action (resource, action)
);

-- 创建用户角色关联表
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- 创建角色权限关联表
CREATE TABLE IF NOT EXISTS role_permissions (
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE
);

-- 插入默认权限数据
INSERT INTO permissions (name, description, resource, action) VALUES 
('article:read', '查看文章', 'article', 'read'),
('article:write', '创建和编辑文章', 'article', 'write'),
('article:delete', '删除文章', 'article', 'delete'),
('user:read', '查看用户信息', 'user', 'read'),
('user:write', '创建和编辑用户', 'user', 'write'),
('user:delete', '删除用户', 'user', 'delete'),
('role:read', '查看角色信息', 'role', 'read'),
('role:write', '创建和编辑角色', 'role', 'write'),
('role:delete', '删除角色', 'role', 'delete'),
('system:admin', '系统管理', 'system', 'admin');

-- 插入默认角色数据
INSERT INTO roles (name, description, is_system) VALUES 
('ADMIN', '系统管理员，拥有所有权限', TRUE),
('EDITOR', '编辑者，可以管理文章内容', TRUE),
('USER', '普通用户，只能查看内容', TRUE);

-- 为管理员角色分配所有权限
INSERT INTO role_permissions (role_id, permission_id) 
SELECT r.id, p.id FROM roles r, permissions p WHERE r.name = 'ADMIN';

-- 为编辑者角色分配文章相关权限
INSERT INTO role_permissions (role_id, permission_id) 
SELECT r.id, p.id FROM roles r, permissions p 
WHERE r.name = 'EDITOR' AND p.resource = 'article';

-- 为普通用户角色分配读取权限
INSERT INTO role_permissions (role_id, permission_id) 
SELECT r.id, p.id FROM roles r, permissions p 
WHERE r.name = 'USER' AND p.action = 'read';

-- 创建默认管理员用户 (密码: admin123)
INSERT INTO users (username, email, password_hash, full_name, status) VALUES 
('admin', 'admin@blog.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9P8jW9TukLv.Saa', '系统管理员', 'ACTIVE');

-- 为默认管理员分配管理员角色
INSERT INTO user_roles (user_id, role_id) 
SELECT u.id, r.id FROM users u, roles r 
WHERE u.username = 'admin' AND r.name = 'ADMIN';

-- 插入示例数据
INSERT INTO articles (title, content, author) VALUES 
('欢迎使用博客系统', '这是第一篇文章，欢迎使用我们的博客系统！系统支持文章的创建、编辑、删除等功能。', '系统管理员'),
('Spring Boot 入门指南', 'Spring Boot 是一个基于 Spring 框架的快速开发框架，它简化了 Spring 应用的配置和部署。本文将介绍如何快速上手 Spring Boot 开发。', '技术小编'),
('Docker 容器化部署', 'Docker 是一个开源的容器化平台，可以帮助开发者快速部署和管理应用程序。本文介绍了如何使用 Docker 部署 Spring Boot 应用。', '运维工程师');