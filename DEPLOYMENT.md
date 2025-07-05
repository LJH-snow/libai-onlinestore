# 在线图书销售系统 - 1panel部署指南

## 系统概述

本系统是一个基于Spring Boot + Vue.js的在线图书销售系统，包含以下组件：
- **前端**：Vue 3 + Element Plus + Vite
- **后端**：Spring Boot 3.5.3 + Java 21
- **数据库**：MySQL 8.0
- **容器化**：Docker + Docker Compose

## 部署前准备

### 1. 系统要求
- 服务器：Linux (推荐 Ubuntu 20.04+ 或 CentOS 8+)
- 内存：至少 4GB RAM
- 存储：至少 20GB 可用空间
- 网络：可访问外网

### 2. 安装1panel
```bash
# 下载并安装1panel
curl -sSL https://resource.fit2cloud.com/1panel/package/quick_start.sh | sh

# 启动1panel服务
systemctl start 1panel

# 查看访问信息
1panel info
```

### 3. 安装Docker和Docker Compose
```bash
# 安装Docker
curl -fsSL https://get.docker.com | sh
systemctl start docker
systemctl enable docker

# 安装Docker Compose
curl -L "https://github.com/docker/compose/releases/download/v2.20.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
chmod +x /usr/local/bin/docker-compose
```

## 部署步骤

### 第一步：上传项目文件
1. 登录1panel管理界面
2. 进入"文件管理"
3. 创建项目目录：`/opt/bookstore`
4. 上传所有项目文件到该目录

### 第二步：配置环境变量
创建生产环境配置文件：

```bash
# 创建环境变量文件
cat > /opt/bookstore/.env << EOF
# 数据库配置
MYSQL_ROOT_PASSWORD=your_secure_password
MYSQL_DATABASE=onlinestore
MYSQL_USER=bookstore
MYSQL_PASSWORD=your_secure_password

# 应用配置
SPRING_PROFILES_ACTIVE=prod
JWT_SECRET=your_very_secure_jwt_secret_key_here
JWT_EXPIRATION=86400000

# 域名配置
DOMAIN=your-domain.com
EOF
```

### 第三步：修改数据库配置
编辑 `backend/src/main/resources/application-prod.properties`：

```properties
# 生产环境数据库配置
spring.datasource.url=jdbc:mysql://mysql:3306/onlinestore?useSSL=false&serverTimezone=UTC&characterEncoding=utf8
spring.datasource.username=root
spring.datasource.password=${MYSQL_ROOT_PASSWORD}

# 生产环境JWT配置
jwt.secret=${JWT_SECRET}
jwt.expiration=${JWT_EXPIRATION}

# 生产环境日志配置
logging.level.root=WARN
logging.level.com.gzmtu.backend=INFO
logging.file.name=/var/log/backend/application.log
```

### 第四步：构建和部署
```bash
# 进入项目目录
cd /opt/bookstore

# 构建镜像
docker-compose build

# 启动服务
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f
```

### 第五步：配置反向代理
在1panel中配置Nginx反向代理：

1. 进入"网站管理" → "反向代理"
2. 添加新的反向代理规则：
   - 域名：`your-domain.com`
   - 目标地址：`http://localhost:80`
   - 启用SSL证书（推荐）

### 第六步：配置防火墙
```bash
# 开放必要端口
ufw allow 80/tcp
ufw allow 443/tcp
ufw allow 22/tcp

# 启用防火墙
ufw enable
```

## 服务管理

### 常用命令
```bash
# 查看服务状态
docker-compose ps

# 启动服务
docker-compose up -d

# 停止服务
docker-compose down

# 重启服务
docker-compose restart

# 查看日志
docker-compose logs -f [service_name]

# 进入容器
docker-compose exec [service_name] bash
```

### 数据备份
```bash
# 备份数据库
docker-compose exec mysql mysqldump -u root -p onlinestore > backup_$(date +%Y%m%d_%H%M%S).sql

# 备份应用数据
tar -czf app_backup_$(date +%Y%m%d_%H%M%S).tar.gz /opt/bookstore
```

## 监控和维护

### 1. 日志监控
- 应用日志：`docker-compose logs -f backend`
- 数据库日志：`docker-compose logs -f mysql`
- 前端日志：`docker-compose logs -f frontend`

### 2. 性能监控
在1panel中启用：
- 系统监控
- 容器监控
- 网络监控

### 3. 自动备份
配置定时任务：
```bash
# 编辑crontab
crontab -e

# 添加每日备份任务
0 2 * * * /opt/bookstore/backup.sh
```

## 故障排除

### 常见问题

1. **容器启动失败**
   ```bash
   # 查看详细错误信息
   docker-compose logs [service_name]
   
   # 检查端口占用
   netstat -tulpn | grep :8081
   ```

2. **数据库连接失败**
   ```bash
   # 检查MySQL容器状态
   docker-compose exec mysql mysql -u root -p
   
   # 检查网络连接
   docker-compose exec backend ping mysql
   ```

3. **前端无法访问后端API**
   - 检查nginx配置中的代理设置
   - 确认后端服务正常运行
   - 检查防火墙设置

### 性能优化

1. **数据库优化**
   ```sql
   -- 添加索引
   CREATE INDEX idx_book_category ON book(category_id);
   CREATE INDEX idx_order_user ON `order`(user_id);
   ```

2. **JVM优化**
   在Dockerfile中添加JVM参数：
   ```dockerfile
   CMD ["java", "-Xms512m", "-Xmx1024m", "-jar", "target/backend-0.0.1-SNAPSHOT.jar"]
   ```

3. **Nginx优化**
   - 启用gzip压缩
   - 配置静态资源缓存
   - 启用HTTP/2

## 安全建议

1. **更改默认密码**
   - 修改MySQL root密码
   - 更改JWT密钥
   - 使用强密码策略

2. **网络安全**
   - 配置防火墙规则
   - 启用HTTPS
   - 定期更新系统

3. **数据安全**
   - 定期备份数据
   - 加密敏感信息
   - 监控异常访问

## 扩展部署

### 负载均衡
使用多个后端实例：
```yaml
backend:
  deploy:
    replicas: 3
```

### 数据库集群
配置MySQL主从复制或使用云数据库服务。

### CDN加速
配置CDN服务加速静态资源访问。

## 联系支持

如遇到部署问题，请检查：
1. 系统日志
2. 容器日志
3. 网络连接
4. 配置文件语法

更多帮助请参考1panel官方文档或联系技术支持。 