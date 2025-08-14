# 项目结构说明

## 整体架构

这是一个完全基于RESTful API的前后端分离博客系统，采用模块化设计。

```
blog-modular/
├── api/                    # API接口定义层
│   └── src/main/java/com/blog/api/
│       ├── model/          # 数据模型
│       └── service/        # 服务接口
├── core/                   # 核心业务逻辑层
│   └── src/main/java/com/blog/core/
│       └── service/impl/   # 业务逻辑实现
├── persistence/            # 数据持久化层
│   └── src/main/java/com/blog/persistence/
│       ├── entity/         # JPA实体
│       └── repository/     # 数据访问层
├── web/                    # Web层
│   ├── src/main/java/com/blog/web/
│   │   ├── api/           # RESTful API控制器
│   │   └── controller/    # 基础控制器
│   └── src/main/resources/
│       └── static/        # 静态前端文件
├── docker-compose.yml     # Docker数据库服务
├── init.sql              # 数据库初始化脚本
└── pom.xml               # Maven父项目配置
```

## 技术栈

### 后端
- **Spring Boot 3.2.0** - 主框架
- **Spring Data JPA** - 数据访问
- **Hibernate** - ORM框架
- **MySQL 8.0** - 数据库
- **Maven** - 构建工具

### 前端
- **Vanilla JavaScript** - 纯JavaScript
- **Bootstrap 5** - UI框架
- **Bootstrap Icons** - 图标库
- **Fetch API** - HTTP请求

### 基础设施
- **Docker** - 数据库容器化
- **RESTful API** - 接口设计规范

## API接口

### 文章管理
- `GET /api/articles` - 获取所有文章
- `GET /api/articles/{id}` - 获取单个文章
- `POST /api/articles` - 创建文章
- `PUT /api/articles/{id}` - 更新文章
- `DELETE /api/articles/{id}` - 删除文章

### 响应格式
```json
{
  "success": true,
  "message": "操作成功",
  "data": { ... },
  "error": null
}
```

## 前端功能

### 用户界面
- 响应式卡片布局文章列表
- 模态框创建/编辑文章
- 文章详情查看
- 实时加载状态
- 友好的错误提示

### 交互特性
- 单页应用体验
- 异步数据加载
- 表单验证
- 确认删除对话框
- 成功操作提示

## 部署说明

1. 启动数据库：`docker-compose up -d`
2. 构建项目：`mvn clean install`
3. 运行应用：`mvn spring-boot:run -pl web`
4. 访问应用：http://localhost:8081

## 开发特点

- **前后端完全分离** - API和前端独立开发
- **模块化架构** - 清晰的层次结构
- **RESTful设计** - 标准的HTTP方法和状态码
- **现代化前端** - 无框架依赖的纯JavaScript实现
- **容器化数据库** - 开发环境一致性