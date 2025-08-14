# Git仓库设置指南

本文档介绍如何将博客管理系统项目上传到Git仓库。

## 📋 项目准备情况

✅ **项目已完成**：
- [x] 完整的项目代码和文档
- [x] Git仓库初始化
- [x] .gitignore文件配置
- [x] 详细的README.md文档
- [x] API接口文档
- [x] 部署指南
- [x] 更新日志
- [x] MIT许可证

✅ **提交历史**：
```
96d9b03 📚 Add comprehensive deployment guide
81b94e4 🎉 Initial commit: Blog Management System v0.2.0
```

## 🚀 上传到GitHub

### 1. 创建GitHub仓库

1. 登录 [GitHub](https://github.com)
2. 点击右上角的 "+" 按钮，选择 "New repository"
3. 填写仓库信息：
   - **Repository name**: `blog-management-system`
   - **Description**: `A modern blog management system with JWT authentication and RBAC permission model`
   - **Visibility**: Public (或 Private)
   - **不要**勾选 "Initialize this repository with a README"

### 2. 连接远程仓库

```bash
# 添加远程仓库
git remote add origin https://github.com/YOUR_USERNAME/blog-management-system.git

# 验证远程仓库
git remote -v
```

### 3. 推送代码

```bash
# 推送到主分支
git push -u origin master

# 或者推送到main分支（如果你的默认分支是main）
git branch -M main
git push -u origin main
```

## 🚀 上传到GitLab

### 1. 创建GitLab项目

1. 登录 [GitLab](https://gitlab.com)
2. 点击 "New project" -> "Create blank project"
3. 填写项目信息：
   - **Project name**: `blog-management-system`
   - **Project description**: `A modern blog management system with JWT authentication and RBAC permission model`
   - **Visibility Level**: Public (或 Private)
   - **不要**勾选 "Initialize repository with a README"

### 2. 连接远程仓库

```bash
# 添加远程仓库
git remote add origin https://gitlab.com/YOUR_USERNAME/blog-management-system.git

# 推送代码
git push -u origin master
```

## 🚀 上传到Gitee (码云)

### 1. 创建Gitee仓库

1. 登录 [Gitee](https://gitee.com)
2. 点击右上角的 "+" 按钮，选择 "新建仓库"
3. 填写仓库信息：
   - **仓库名称**: `blog-management-system`
   - **仓库介绍**: `基于Spring Boot的现代化博客管理系统，支持JWT认证和RBAC权限模型`
   - **是否开源**: 开源 (或 私有)
   - **不要**勾选 "使用Readme文件初始化这个仓库"

### 2. 连接远程仓库

```bash
# 添加远程仓库
git remote add origin https://gitee.com/YOUR_USERNAME/blog-management-system.git

# 推送代码
git push -u origin master
```

## 🔧 多个远程仓库管理

如果你想同时推送到多个Git平台：

```bash
# 添加多个远程仓库
git remote add github https://github.com/YOUR_USERNAME/blog-management-system.git
git remote add gitlab https://gitlab.com/YOUR_USERNAME/blog-management-system.git
git remote add gitee https://gitee.com/YOUR_USERNAME/blog-management-system.git

# 分别推送到不同平台
git push github master
git push gitlab master
git push gitee master

# 或者配置一个命令推送到所有平台
git remote set-url --add --push origin https://github.com/YOUR_USERNAME/blog-management-system.git
git remote set-url --add --push origin https://gitlab.com/YOUR_USERNAME/blog-management-system.git
git remote set-url --add --push origin https://gitee.com/YOUR_USERNAME/blog-management-system.git

# 一次推送到所有平台
git push origin master
```

## 📝 仓库设置建议

### 1. 设置仓库主题标签 (GitHub)

在GitHub仓库页面添加以下标签：
```
spring-boot, java, jwt, rbac, blog, rest-api, mysql, docker, maven, security
```

### 2. 创建分支保护规则

```bash
# 创建开发分支
git checkout -b develop
git push -u origin develop

# 创建功能分支示例
git checkout -b feature/user-management
```

### 3. 设置Issue模板

创建 `.github/ISSUE_TEMPLATE/` 目录和模板文件（GitHub）

### 4. 设置Pull Request模板

创建 `.github/pull_request_template.md` 文件

## 🎯 推荐的仓库描述

### GitHub/GitLab描述
```
A modern, modular blog management system built with Spring Boot 3.2.0, featuring JWT authentication, RBAC permission model, and RESTful APIs. Perfect for learning enterprise-level Java development patterns.

🚀 Features: JWT Auth, RBAC Permissions, Modular Architecture, Docker Support
🛠 Tech Stack: Spring Boot, Spring Security, JPA, MySQL, Redis, Maven
```

### Gitee描述
```
基于Spring Boot 3.2.0构建的现代化模块化博客管理系统，具有JWT认证、RBAC权限模型和RESTful API。适合学习企业级Java开发模式。

🚀 特性：JWT认证、RBAC权限、模块化架构、Docker支持
🛠 技术栈：Spring Boot、Spring Security、JPA、MySQL、Redis、Maven
```

## ✅ 上传后的验证

上传完成后，请验证：

1. **代码完整性**：确保所有文件都已上传
2. **README显示**：检查README.md是否正确显示
3. **文档链接**：确保所有文档链接正常工作
4. **许可证**：确认LICENSE文件存在
5. **标签和描述**：设置合适的仓库标签和描述

## 🎉 完成！

恭喜！你的博客管理系统项目现在已经成功上传到Git仓库了。

### 下一步建议：

1. **添加CI/CD**：设置GitHub Actions或GitLab CI
2. **代码质量**：集成SonarQube或CodeClimate
3. **文档网站**：使用GitHub Pages部署文档
4. **版本发布**：创建Release和Tag
5. **社区建设**：添加贡献指南和行为准则

---

🌟 **记得给项目加个星标，分享给更多开发者！**