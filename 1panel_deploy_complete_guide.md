# 1Panel云服务器部署完整指南

## 📋 部署概览

将AAA在线书城系统部署到云服务器的1Panel管理面板，实现生产环境运行。

## 🔧 准备工作

### 1. 云服务器要求
- **操作系统**: Ubuntu 20.04+ / CentOS 7+
- **内存**: 最低2GB，推荐4GB+
- **存储**: 最低20GB，推荐50GB+
- **网络**: 公网IP，开放必要端口

### 2. 必要端口配置
在云服务器控制台安全组中开放：
- **80**: HTTP访问
- **443**: HTTPS访问  
- **8081/8082**: 后端API
- **22**: SSH访问
- **1Panel端口**: 根据安装时设置

## 🚀 部署步骤

### 第一步：安装1Panel

```bash
# 连接到云服务器
ssh root@your-server-ip

# 下载并安装1Panel
curl -sSL https://resource.fit2cloud.com/1panel/package/quick_start.sh -o quick_start.sh
sudo bash quick_start.sh

# 记录安装完成后显示的：
# - 1Panel访问地址
# - 用户名和密码
# - 安全入口
```

### 第二步：配置1Panel环境

1. **登录1Panel管理界面**
   ```
   访问: http://your-server-ip:端口/安全入口
   输入安装时生成的用户名和密码
   ```

2. **安装Docker**
   - 进入"容器管理" → "设置"
   - 点击"安装Docker"
   - 等待安装完成

3. **配置防火墙**
   - 进入"安全" → "防火墙"
   - 添加规则：80/tcp、443/tcp、8081/tcp

### 第三步：上传项目文件

#### 方法A：通过1Panel文件管理器
```bash
# 1. 在1Panel中创建项目目录
# 进入"文件管理" → 创建目录 /opt/bookstore

# 2. 在本地打包项目
cd /home/libai/桌面/AAA
tar -czf bookstore.tar.gz .

# 3. 通过1Panel文件管理器上传并解压
```

#### 方法B：通过SCP上传（推荐）
```bash
# 在本地Ubuntu执行
cd /home/libai/桌面/AAA

# 上传项目文件到服务器
scp -r . root@your-server-ip:/opt/bookstore/

# 或者先打包再上传
tar -czf bookstore.tar.gz .
scp bookstore.tar.gz root@your-server-ip:/opt/

# 在服务器上解压
ssh root@your-server-ip
cd /opt
tar -xzf bookstore.tar.gz -C bookstore/
```

#### 方法C：通过Git克隆
```bash
# 在服务器上执行
ssh root@your-server-ip
cd /opt
git clone <your-repository-url> bookstore
cd bookstore
```

### 第四步：配置环境

```bash
# 连接到服务器
ssh root@your-server-ip
cd /opt/bookstore

# 创建生产环境配置
cp env.example .env

# 编辑环境变量
nano .env
```

修改.env文件内容：
```env
# 数据库配置
MYSQL_ROOT_PASSWORD=your-secure-password
MYSQL_PASSWORD=your-secure-password

# JWT配置
JWT_SECRET=your-very-secure-jwt-secret-key-2024

# 服务器配置
SERVER_IP=your-server-ip
DOMAIN=your-domain-or-ip

# 端口配置（避免冲突）
MYSQL_PORT=3307
BACKEND_PORT=8082
FRONTEND_PORT=8080
```

### 第五步：执行部署

#### 使用专用部署脚本
```bash
# 设置脚本权限
chmod +x 1panel_deploy.sh
chmod +x deploy.sh
chmod +x backup.sh

# 执行1Panel专用部署脚本
./1panel_deploy.sh
```

#### 手动部署步骤
```bash
# 1. 构建并启动服务
docker-compose up --build -d

# 2. 查看服务状态
docker-compose ps

# 3. 查看日志
docker-compose logs -f

# 4. 等待服务完全启动
sleep 60
```

### 第六步：验证部署

#### 检查服务状态
```bash
# 查看容器状态
docker-compose ps

# 检查端口监听
netstat -tulpn | grep -E ':(80|8082|3307)'

# 测试数据库连接
docker-compose exec mysql mysql -u root -p
```

#### 访问测试
- **前端**: http://your-server-ip:8080
- **后端API**: http://your-server-ip:8082/api/books
- **1Panel管理**: http://your-server-ip:端口/安全入口

#### API功能测试
```bash
# 测试图书列表
curl http://your-server-ip:8082/api/books

# 测试分类列表
curl http://your-server-ip:8082/api/categories

# 测试用户注册
curl -X POST http://your-server-ip:8082/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"123456","email":"test@example.com"}'
```

## 🔧 1Panel高级配置

### 1. 配置网站管理
```bash
# 在1Panel中：
# 1. 进入"网站管理" → "网站"
# 2. 点击"新建网站"
# 3. 配置：
#    - 域名: your-domain.com
#    - 根目录: /opt/bookstore/Sofeware_Demo/dist
#    - 端口: 80
```

### 2. 配置反向代理
```nginx
# 在1Panel网站设置中添加反向代理规则
location /api/ {
    proxy_pass http://localhost:8082;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto $scheme;
}
```

### 3. 配置SSL证书
```bash
# 在1Panel中：
# 1. 进入"网站管理" → "SSL"
# 2. 选择"Let's Encrypt"自动申请
# 3. 或上传自有证书
```

### 4. 配置监控和备份
```bash
# 在1Panel中：
# 1. 进入"监控" → "系统监控"
# 2. 启用CPU、内存、磁盘监控
# 3. 设置告警阈值

# 配置定时备份
# 进入"计划任务" → 添加任务
# 执行命令: /opt/bookstore/backup.sh
# 执行时间: 每日凌晨2点
```

## 🛠️ 管理和维护

### 常用管理命令
```bash
# 查看服务状态
docker-compose ps

# 查看实时日志
docker-compose logs -f

# 重启服务
docker-compose restart

# 停止服务
docker-compose down

# 更新服务
git pull  # 如果使用Git
docker-compose up --build -d

# 备份数据
./backup.sh

# 查看系统资源
htop
df -h
```

### 故障排查
```bash
# 检查容器日志
docker-compose logs mysql
docker-compose logs backend
docker-compose logs frontend

# 检查端口占用
netstat -tulpn | grep :8082

# 检查磁盘空间
df -h

# 检查内存使用
free -h

# 重启Docker服务
sudo systemctl restart docker
```

## ⚠️ 安全建议

1. **修改默认密码**
   - 数据库root密码
   - JWT密钥
   - 1Panel管理密码

2. **配置防火墙**
   - 只开放必要端口
   - 配置IP白名单

3. **定期更新**
   - 系统安全补丁
   - Docker镜像
   - 应用代码

4. **数据备份**
   - 定期备份数据库
   - 备份应用文件
   - 测试恢复流程

## 📞 技术支持

如遇问题可参考：
- 1Panel官方文档: https://1panel.cn/docs
- Docker官方文档: https://docs.docker.com
- 项目README和其他文档
