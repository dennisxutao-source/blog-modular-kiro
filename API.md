# API 接口文档

博客管理系统 RESTful API 接口文档

## 基础信息

- **Base URL**: `http://localhost:8081`
- **Content-Type**: `application/json`
- **认证方式**: JWT Bearer Token

## 认证说明

除了公开接口外，所有API都需要在请求头中携带JWT令牌：

```
Authorization: Bearer <access_token>
```

## 响应格式

所有API响应都遵循统一格式：

### 成功响应
```json
{
  "success": true,
  "message": "操作成功",
  "data": { ... },
  "timestamp": 1755137593000
}
```

### 错误响应
```json
{
  "success": false,
  "error": "错误类型",
  "message": "错误描述",
  "timestamp": 1755137593000
}
```

## 认证接口

### 用户登录
**POST** `/api/auth/login`

登录获取访问令牌和刷新令牌。

**请求体**:
```json
{
  "username": "admin",
  "password": "admin123"
}
```

**响应**:
```json
{
  "success": true,
  "message": "登录成功",
  "data": {
    "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
    "refreshToken": "eyJhbGciOiJIUzUxMiJ9...",
    "tokenType": "Bearer",
    "user": {
      "id": 1,
      "username": "admin",
      "email": "admin@blog.com",
      "fullName": "系统管理员",
      "status": "ACTIVE",
      "roles": ["ADMIN"]
    }
  }
}
```

### 用户注册
**POST** `/api/auth/register`

注册新用户账户。

**请求体**:
```json
{
  "username": "newuser",
  "email": "user@example.com",
  "password": "password123",
  "fullName": "新用户"
}
```

**响应**:
```json
{
  "success": true,
  "message": "注册成功",
  "data": {
    "id": 2,
    "username": "newuser",
    "email": "user@example.com",
    "fullName": "新用户",
    "status": "ACTIVE",
    "roles": []
  }
}
```

### 刷新令牌
**POST** `/api/auth/refresh`

使用刷新令牌获取新的访问令牌。

**请求体**:
```json
{
  "refreshToken": "eyJhbGciOiJIUzUxMiJ9..."
}
```

### 用户登出
**POST** `/api/auth/logout`

**需要认证**: ✅

登出当前用户，清除认证状态。

### 获取当前用户信息
**GET** `/api/auth/me`

**需要认证**: ✅

获取当前登录用户的详细信息。

## 文章管理接口

### 获取文章列表
**GET** `/api/articles`

获取所有文章列表。

**响应**:
```json
{
  "success": true,
  "message": "获取文章列表成功",
  "data": [
    {
      "id": 1,
      "title": "文章标题",
      "content": "文章内容",
      "author": "作者",
      "createdAt": "2025-08-14T10:00:00",
      "updatedAt": "2025-08-14T10:00:00"
    }
  ]
}
```

### 获取文章详情
**GET** `/api/articles/{id}`

根据ID获取单篇文章详情。

**路径参数**:
- `id` (Long): 文章ID

### 创建文章
**POST** `/api/articles`

**需要认证**: ✅  
**需要权限**: `article:write`

创建新文章。

**请求体**:
```json
{
  "title": "新文章标题",
  "content": "文章内容",
  "author": "作者姓名"
}
```

### 更新文章
**PUT** `/api/articles/{id}`

**需要认证**: ✅  
**需要权限**: `article:write`

更新指定文章。

**路径参数**:
- `id` (Long): 文章ID

**请求体**:
```json
{
  "title": "更新后的标题",
  "content": "更新后的内容",
  "author": "作者姓名"
}
```

### 删除文章
**DELETE** `/api/articles/{id}`

**需要认证**: ✅  
**需要权限**: `article:delete`

删除指定文章。

**路径参数**:
- `id` (Long): 文章ID

## 权限系统

### 权限格式
权限采用 `resource:action` 格式，例如：
- `article:read` - 读取文章
- `article:write` - 创建/编辑文章
- `article:delete` - 删除文章
- `user:read` - 查看用户
- `user:write` - 创建/编辑用户
- `user:delete` - 删除用户
- `system:admin` - 系统管理

### 默认角色权限

#### ADMIN角色权限
- `article:read`
- `article:write`
- `article:delete`
- `user:read`
- `user:write`
- `user:delete`
- `role:read`
- `role:write`
- `role:delete`
- `system:admin`

## 错误码说明

| HTTP状态码 | 错误类型 | 说明 |
|-----------|---------|------|
| 200 | OK | 请求成功 |
| 201 | Created | 资源创建成功 |
| 400 | Bad Request | 请求参数错误 |
| 401 | Unauthorized | 未认证或认证失败 |
| 403 | Forbidden | 权限不足 |
| 404 | Not Found | 资源不存在 |
| 500 | Internal Server Error | 服务器内部错误 |

## 使用示例

### 完整的认证流程示例

1. **用户登录**
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

2. **使用令牌访问受保护资源**
```bash
curl -X POST http://localhost:8081/api/articles \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..." \
  -d '{
    "title": "我的第一篇文章",
    "content": "这是文章内容",
    "author": "管理员"
  }'
```

3. **刷新令牌**
```bash
curl -X POST http://localhost:8081/api/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "eyJhbGciOiJIUzUxMiJ9..."
  }'
```

## 开发说明

### 添加新接口权限控制
在Controller方法上添加权限注解：

```java
@RequirePermission(resource = "article", action = "write")
@PostMapping
public ResponseEntity<Article> createArticle(@RequestBody Article article) {
    // 方法实现
}
```

### 自定义权限检查
使用Spring Security表达式：

```java
@PreAuthorize("hasPermission(null, 'article:write')")
public void someMethod() {
    // 方法实现
}
```