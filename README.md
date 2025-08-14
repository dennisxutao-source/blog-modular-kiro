# 博客管理系统 (Blog Management System)

一个基于Spring Boot的现代化、模块化博客管理系统，采用RBAC权限模型，支持JWT认证和细粒度权限控制。

## 🚀 项目特性

- **模块化架构**：采用多模块Maven项目结构，职责分离清晰
- **RBAC权限模型**：基于角色的访问控制，支持细粒度权限管理
- **JWT认证**：无状态认证，支持访问令牌和刷新令牌
- **权限注解**：支持方法级权限控制，使用简单
- **RESTful API**：标准化的API设计，支持前后端分离
- **Docker支持**：容器化部署，开发环境一键启动

## 📁 项目结构

```
blog-modular/
├── api/                    # API接口定义模块
│   └── src/main/java/com/blog/api/
│       ├── model/          # 领域模型
│       └── service/        # 服务接口
├── core/                   # 核心业务逻辑模块
│   └── src/main/java/com/blog/core/
│       └── service/impl/   # 业务逻辑实现
├── persistence/            # 数据持久化模块
│   └── src/main/java/com/blog/persistence/
│       ├── entity/         # JPA实体
│       └── repository/     # 数据访问层
├── web/                    # Web层模块
│   └── src/main/java/com/blog/web/
│       ├── api/            # REST控制器
│       ├── config/         # 配置类
│       ├── security/       # 安全相关
│       └── util/           # 工具类
├── docker-compose.yml      # Docker编排文件
├── init.sql               # 数据库初始化脚本
└── README.md              # 项目文档
```

## 🛠 技术栈

### 后端技术
- **Spring Boot 3.2.0** - 主框架
- **Spring Security** - 安全框架
- **Spring Data JPA** - 数据访问
- **Spring AOP** - 面向切面编程
- **JWT (JJWT)** - JSON Web Token
- **MySQL 8.0** - 关系型数据库
- **Redis** - 缓存数据库
- **Maven** - 项目管理工具

### 开发工具
- **Docker & Docker Compose** - 容器化
- **Hibernate** - ORM框架
- **Jackson** - JSON处理
- **Validation** - 参数验证

## 🏗 系统架构

### 分层架构
```
┌─────────────────┐
│   Web Layer     │  ← REST API、安全配置、权限控制
├─────────────────┤
│  Service Layer  │  ← 业务逻辑、事务管理
├─────────────────┤
│Repository Layer │  ← 数据访问、实体映射
├─────────────────┤
│  Database Layer │  ← MySQL、Redis
└─────────────────┘
```

### 权限模型
```
User ←→ UserRole ←→ Role ←→ RolePermission ←→ Permission
```

## 🚀 快速开始

### 环境要求
- JDK 17+
- Maven 3.6+
- Docker & Docker Compose

### 1. 克隆项目
```bash
git clone https://github.com/dennisxutao-source/blog-modular-kiro.git
cd blog-modular-kiro
```

### 2. 启动数据库服务
```bash
docker-compose up -d mysql
```

### 3. 构建项目
```bash
mvn clean package -DskipTests
```

### 4. 启动应用
```bash
java -jar web/target/blog-web-1.0.0.jar
```

### 5. 访问应用
- API文档：http://localhost:8081
- 管理后台：http://localhost:8081/admin.html (开发中)

## 🔐 认证与权限

### 默认账户
- **管理员账户**：
  - 用户名：`admin`
  - 密码：`admin123`
  - 角色：`ADMIN`

### API认证
所有需要认证的API都需要在请求头中携带JWT令牌：
```
Authorization: Bearer <access_token>
```

### 权限注解使用
```java
@RequirePermission(resource = "article", action = "write")
public ResponseEntity<Article> createArticle(@RequestBody Article article) {
    // 只有拥有 article:write 权限的用户才能访问
}
```

## 📚 API文档

### 认证相关
- `POST /api/auth/login` - 用户登录
- `POST /api/auth/register` - 用户注册
- `POST /api/auth/refresh` - 刷新令牌
- `POST /api/auth/logout` - 用户登出
- `GET /api/auth/me` - 获取当前用户信息

### 文章管理
- `GET /api/articles` - 获取文章列表
- `GET /api/articles/{id}` - 获取文章详情
- `POST /api/articles` - 创建文章 (需要 article:write 权限)
- `PUT /api/articles/{id}` - 更新文章 (需要 article:write 权限)
- `DELETE /api/articles/{id}` - 删除文章 (需要 article:delete 权限)

### 请求示例

#### 用户登录
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

#### 创建文章
```bash
curl -X POST http://localhost:8081/api/articles \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <access_token>" \
  -d '{
    "title": "文章标题",
    "content": "文章内容"
  }'
```

## 🗄 数据库设计

### 核心表结构
- `users` - 用户表
- `roles` - 角色表
- `permissions` - 权限表
- `user_roles` - 用户角色关联表
- `role_permissions` - 角色权限关联表
- `articles` - 文章表

### 权限设计
系统预定义权限包括：
- `article:read` - 读取文章
- `article:write` - 创建/编辑文章
- `article:delete` - 删除文章
- `user:read` - 查看用户
- `user:write` - 创建/编辑用户
- `user:delete` - 删除用户
- `role:read` - 查看角色
- `role:write` - 创建/编辑角色
- `role:delete` - 删除角色
- `system:admin` - 系统管理

## 🔧 配置说明

### 应用配置 (application.yml)
```yaml
server:
  port: 8081

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/blog_db
    username: blog_user
    password: blog_pass
  
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

jwt:
  secret: <your-secret-key>
  expiration: 86400000  # 24小时
  refresh-expiration: 604800000  # 7天
```

### Docker配置
```yaml
# docker-compose.yml
services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: root123
      MYSQL_DATABASE: blog_db
      MYSQL_USER: blog_user
      MYSQL_PASSWORD: blog_pass
    ports:
      - "3306:3306"
```

## 🧪 开发指南

### 添加新的权限控制
1. 在数据库中添加新权限
2. 在Controller方法上添加注解：
```java
@RequirePermission(resource = "resource_name", action = "action_name")
```

### 扩展业务功能
1. 在 `api` 模块定义接口和模型
2. 在 `core` 模块实现业务逻辑
3. 在 `persistence` 模块添加数据访问
4. 在 `web` 模块添加REST接口

## 📋 开发进度

### ✅ 已完成
- [x] 项目基础架构搭建
- [x] 用户认证系统 (JWT)
- [x] RBAC权限模型
- [x] 权限注解和拦截器
- [x] 文章基础CRUD
- [x] 数据库初始化

### 🚧 开发中
- [ ] 用户管理API
- [ ] 管理后台界面
- [ ] 文章分类和标签
- [ ] 评论系统
- [ ] 文件上传功能

### 📅 计划中
- [ ] 系统监控和日志
- [ ] 缓存优化
- [ ] 单元测试
- [ ] API文档生成
- [ ] 前端管理界面

## 🤝 贡献指南

1. Fork 项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 📞 联系方式

如有问题或建议，请通过以下方式联系：
- 提交 Issue
- 发送邮件至：[517130992@qq.com]

---

⭐ 如果这个项目对你有帮助，请给它一个星标！