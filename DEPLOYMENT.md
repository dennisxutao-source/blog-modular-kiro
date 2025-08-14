# 部署指南

本文档介绍如何在不同环境中部署博客管理系统。

## 🚀 快速部署

### 开发环境部署

#### 1. 环境准备
```bash
# 检查Java版本 (需要JDK 17+)
java -version

# 检查Maven版本 (需要3.6+)
mvn -version

# 检查Docker版本
docker --version
docker-compose --version
```

#### 2. 克隆项目
```bash
git clone <your-repository-url>
cd blog-modular
```

#### 3. 启动数据库
```bash
# 启动MySQL数据库
docker-compose up -d mysql

# 查看数据库状态
docker-compose ps
```

#### 4. 构建项目
```bash
# 清理并构建项目
mvn clean package -DskipTests

# 或者只构建不运行测试
mvn clean install -DskipTests
```

#### 5. 启动应用
```bash
# 方式1：使用Maven运行
mvn spring-boot:run -pl web

# 方式2：直接运行JAR包
java -jar web/target/blog-web-1.0.0.jar

# 方式3：后台运行
nohup java -jar web/target/blog-web-1.0.0.jar > app.log 2>&1 &
```

#### 6. 验证部署
```bash
# 检查应用状态
curl http://localhost:8081/api/articles

# 测试登录
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

## 🐳 Docker部署

### 创建应用Docker镜像

#### 1. 创建Dockerfile
```dockerfile
# web/Dockerfile
FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/blog-web-1.0.0.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
```

#### 2. 构建镜像
```bash
# 构建项目
mvn clean package -DskipTests

# 构建Docker镜像
cd web
docker build -t blog-management:latest .
```

#### 3. 更新docker-compose.yml
```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    container_name: blog-mysql
    environment:
      MYSQL_ROOT_PASSWORD: root123
      MYSQL_DATABASE: blog_db
      MYSQL_USER: blog_user
      MYSQL_PASSWORD: blog_pass
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
      - ./init.sql:/docker-entrypoint-initdb.d/init.sql
    networks:
      - blog-network

  redis:
    image: redis:7-alpine
    container_name: blog-redis
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data
    networks:
      - blog-network

  app:
    image: blog-management:latest
    container_name: blog-app
    ports:
      - "8081:8081"
    environment:
      - SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/blog_db
      - SPRING_REDIS_HOST=redis
    depends_on:
      - mysql
      - redis
    networks:
      - blog-network

volumes:
  mysql_data:
  redis_data:

networks:
  blog-network:
    driver: bridge
```

#### 4. 启动完整服务
```bash
docker-compose up -d
```

## ☁️ 生产环境部署

### 1. 环境配置

#### 创建生产配置文件
```yaml
# web/src/main/resources/application-prod.yml
server:
  port: 8081

spring:
  datasource:
    url: jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/${DB_NAME:blog_db}
    username: ${DB_USER:blog_user}
    password: ${DB_PASSWORD:blog_pass}
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000

  jpa:
    hibernate:
      ddl-auto: validate  # 生产环境使用validate
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQLDialect
        format_sql: false

  data:
    redis:
      host: ${REDIS_HOST:localhost}
      port: ${REDIS_PORT:6379}
      password: ${REDIS_PASSWORD:}
      database: 0
      timeout: 2000ms
      lettuce:
        pool:
          max-active: 8
          max-idle: 8
          min-idle: 0

jwt:
  secret: ${JWT_SECRET:your-production-secret-key-must-be-very-long-and-secure}
  expiration: ${JWT_EXPIRATION:86400000}
  refresh-expiration: ${JWT_REFRESH_EXPIRATION:604800000}

logging:
  level:
    com.blog: INFO
    org.springframework: WARN
    org.hibernate: WARN
  file:
    name: /var/log/blog-management/app.log
  pattern:
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
```

### 2. 环境变量配置

#### 创建环境变量文件
```bash
# .env
DB_HOST=your-db-host
DB_PORT=3306
DB_NAME=blog_db
DB_USER=blog_user
DB_PASSWORD=your-secure-password

REDIS_HOST=your-redis-host
REDIS_PORT=6379
REDIS_PASSWORD=your-redis-password

JWT_SECRET=your-very-long-and-secure-jwt-secret-key-for-production
JWT_EXPIRATION=86400000
JWT_REFRESH_EXPIRATION=604800000
```

### 3. 系统服务配置

#### 创建systemd服务文件
```ini
# /etc/systemd/system/blog-management.service
[Unit]
Description=Blog Management System
After=network.target

[Service]
Type=simple
User=blog
Group=blog
WorkingDirectory=/opt/blog-management
ExecStart=/usr/bin/java -jar -Dspring.profiles.active=prod /opt/blog-management/blog-web-1.0.0.jar
Restart=always
RestartSec=10
StandardOutput=syslog
StandardError=syslog
SyslogIdentifier=blog-management
Environment=JAVA_HOME=/usr/lib/jvm/java-17-openjdk

[Install]
WantedBy=multi-user.target
```

#### 启动服务
```bash
# 重新加载systemd配置
sudo systemctl daemon-reload

# 启动服务
sudo systemctl start blog-management

# 设置开机自启
sudo systemctl enable blog-management

# 查看服务状态
sudo systemctl status blog-management

# 查看日志
sudo journalctl -u blog-management -f
```

### 4. Nginx反向代理配置

```nginx
# /etc/nginx/sites-available/blog-management
server {
    listen 80;
    server_name your-domain.com;

    # 重定向到HTTPS
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name your-domain.com;

    # SSL证书配置
    ssl_certificate /path/to/your/certificate.crt;
    ssl_certificate_key /path/to/your/private.key;
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers ECDHE-RSA-AES256-GCM-SHA512:DHE-RSA-AES256-GCM-SHA512:ECDHE-RSA-AES256-GCM-SHA384:DHE-RSA-AES256-GCM-SHA384;
    ssl_prefer_server_ciphers off;

    # 日志配置
    access_log /var/log/nginx/blog-management.access.log;
    error_log /var/log/nginx/blog-management.error.log;

    # 反向代理配置
    location / {
        proxy_pass http://localhost:8081;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        # 超时配置
        proxy_connect_timeout 30s;
        proxy_send_timeout 30s;
        proxy_read_timeout 30s;
    }

    # 静态文件缓存
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg)$ {
        proxy_pass http://localhost:8081;
        expires 1y;
        add_header Cache-Control "public, immutable";
    }

    # 安全头
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-XSS-Protection "1; mode=block" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header Referrer-Policy "no-referrer-when-downgrade" always;
    add_header Content-Security-Policy "default-src 'self' http: https: data: blob: 'unsafe-inline'" always;
}
```

## 📊 监控和维护

### 1. 健康检查

#### 应用健康检查端点
```bash
# 检查应用状态
curl http://localhost:8081/actuator/health

# 检查数据库连接
curl http://localhost:8081/api/articles
```

### 2. 日志管理

#### 日志轮转配置
```bash
# /etc/logrotate.d/blog-management
/var/log/blog-management/*.log {
    daily
    missingok
    rotate 30
    compress
    delaycompress
    notifempty
    create 644 blog blog
    postrotate
        systemctl reload blog-management
    endscript
}
```

### 3. 数据库备份

#### 自动备份脚本
```bash
#!/bin/bash
# backup-db.sh

DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_DIR="/opt/backups/blog-management"
DB_NAME="blog_db"
DB_USER="blog_user"
DB_PASSWORD="your-password"

mkdir -p $BACKUP_DIR

# 创建数据库备份
mysqldump -u $DB_USER -p$DB_PASSWORD $DB_NAME > $BACKUP_DIR/blog_db_$DATE.sql

# 压缩备份文件
gzip $BACKUP_DIR/blog_db_$DATE.sql

# 删除30天前的备份
find $BACKUP_DIR -name "*.sql.gz" -mtime +30 -delete

echo "Database backup completed: blog_db_$DATE.sql.gz"
```

#### 设置定时备份
```bash
# 添加到crontab
crontab -e

# 每天凌晨2点备份
0 2 * * * /opt/scripts/backup-db.sh
```

## 🔧 故障排除

### 常见问题

#### 1. 应用启动失败
```bash
# 检查Java版本
java -version

# 检查端口占用
lsof -i :8081

# 查看应用日志
tail -f /var/log/blog-management/app.log
```

#### 2. 数据库连接失败
```bash
# 检查MySQL服务状态
systemctl status mysql

# 测试数据库连接
mysql -u blog_user -p -h localhost blog_db

# 检查防火墙设置
ufw status
```

#### 3. 权限问题
```bash
# 检查文件权限
ls -la /opt/blog-management/

# 修复权限
chown -R blog:blog /opt/blog-management/
chmod +x /opt/blog-management/blog-web-1.0.0.jar
```

### 性能优化

#### JVM参数调优
```bash
# 生产环境JVM参数示例
java -Xms512m -Xmx2g \
     -XX:+UseG1GC \
     -XX:MaxGCPauseMillis=200 \
     -XX:+HeapDumpOnOutOfMemoryError \
     -XX:HeapDumpPath=/var/log/blog-management/ \
     -Dspring.profiles.active=prod \
     -jar blog-web-1.0.0.jar
```

## 📞 支持

如果在部署过程中遇到问题，请：

1. 查看应用日志：`tail -f /var/log/blog-management/app.log`
2. 检查系统服务状态：`systemctl status blog-management`
3. 查看数据库连接：`docker-compose logs mysql`
4. 提交Issue到项目仓库