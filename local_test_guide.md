# Ubuntu本地测试指南

## 1. 环境准备

### 安装Docker和Docker Compose
```bash
# 更新系统包
sudo apt update && sudo apt upgrade -y

# 安装Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

# 启动Docker服务
sudo systemctl start docker
sudo systemctl enable docker

# 将当前用户添加到docker组
sudo usermod -aG docker $USER

# 重新登录或执行以下命令使组权限生效
newgrp docker

# 验证Docker安装
docker --version
docker-compose --version
```

### 安装Git（如果未安装）
```bash
sudo apt install git -y
```

## 2. 项目准备

### 克隆或准备项目文件
```bash
# 如果项目在Git仓库中
git clone <your-repository-url>
cd AAA

# 或者直接在当前目录工作
cd /home/libai/桌面/AAA
```

### 配置环境变量
```bash
# 复制环境变量模板
cp env.example .env

# 编辑环境变量（可选，默认值适用于本地测试）
nano .env
```

## 3. 本地测试步骤

### 方法A: 使用Docker Compose（推荐）
```bash
# 构建并启动所有服务
docker-compose up --build -d

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f

# 等待服务完全启动（约1-2分钟）
sleep 60
```

### 方法B: 分步启动服务
```bash
# 1. 启动MySQL数据库
docker-compose up -d mysql

# 等待MySQL启动
sleep 30

# 2. 启动后端服务
docker-compose up -d backend

# 等待后端启动
sleep 30

# 3. 启动前端服务
docker-compose up -d frontend
```

## 4. 验证测试

### 检查服务状态
```bash
# 查看所有容器状态
docker-compose ps

# 检查端口占用
netstat -tulpn | grep -E ':(3307|8082|8080)'

# 测试数据库连接
docker-compose exec mysql mysql -u root -p123456 -e "SHOW DATABASES;"
```

### 访问应用
- **前端网站**: http://localhost:8080
- **后端API**: http://localhost:8082
- **数据库**: localhost:3307 (用户: root, 密码: 123456)

### API测试
```bash
# 测试后端健康状态
curl http://localhost:8082/api/books

# 测试用户注册
curl -X POST http://localhost:8082/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"123456","email":"test@example.com"}'

# 测试用户登录
curl -X POST http://localhost:8082/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"123456"}'
```

## 5. 常见问题排查

### 端口冲突
```bash
# 检查端口占用
sudo lsof -i :8080
sudo lsof -i :8082
sudo lsof -i :3307

# 停止占用端口的进程
sudo kill -9 <PID>
```

### 容器启动失败
```bash
# 查看详细日志
docker-compose logs mysql
docker-compose logs backend
docker-compose logs frontend

# 重新构建镜像
docker-compose build --no-cache
docker-compose up -d
```

### 数据库连接问题
```bash
# 进入MySQL容器
docker-compose exec mysql bash

# 在容器内连接数据库
mysql -u root -p123456

# 检查数据库和表
SHOW DATABASES;
USE onlinestore;
SHOW TABLES;
```

## 6. 开发模式

### 前端开发模式
```bash
cd Sofeware_Demo

# 安装依赖
npm install

# 启动开发服务器
npm run dev

# 访问: http://localhost:5173
```

### 后端开发模式
```bash
cd backend-go

# 安装Go依赖
go mod tidy

# 运行后端服务
go run main.go

# 访问: http://localhost:8081
```

## 7. 停止和清理

### 停止服务
```bash
# 停止所有服务
docker-compose down

# 停止并删除数据卷
docker-compose down -v

# 清理未使用的镜像
docker system prune -f
```

### 完全重置
```bash
# 停止并删除所有容器、网络、数据卷
docker-compose down -v --remove-orphans

# 删除所有相关镜像
docker rmi $(docker images | grep bookstore | awk '{print $3}')

# 重新构建
docker-compose up --build -d
```
