# 1Panel文件上传指南

## 方法一：通过1panel文件管理器上传

1. **登录1panel管理界面**
   - 访问: http://192.168.122.131:32435/e98e481bc4
   - 用户名: 18202786f9
   - 密码: 19f317d806

2. **进入文件管理**
   - 点击左侧菜单"文件管理"
   - 点击"新建文件夹"，创建目录：`/opt/bookstore`

3. **上传项目文件**
   - 进入 `/opt/bookstore` 目录
   - 点击"上传文件"按钮
   - 选择您本地的项目文件进行上传
   - 或者直接拖拽文件到上传区域

## 方法二：通过SSH上传（推荐）

如果您有服务器的SSH访问权限，可以使用以下命令：

```bash
# 连接到服务器
ssh root@192.168.122.131

# 创建项目目录
mkdir -p /opt/bookstore
cd /opt/bookstore

# 使用scp上传文件（在本地执行）
scp -r /path/to/your/project/* root@192.168.122.131:/opt/bookstore/
```

## 方法三：使用Git克隆（如果项目在Git仓库中）

```bash
# 在服务器上执行
cd /opt
git clone https://github.com/your-username/your-repo.git bookstore
```

## 需要上传的文件清单

确保以下文件都已上传到 `/opt/bookstore` 目录：

- ✅ `backend/` - Spring Boot后端代码
- ✅ `Sofeware_Demo/` - Vue前端代码  
- ✅ `docker-compose.yml` - Docker编排文件
- ✅ `deploy.sh` - 部署脚本
- ✅ `backup.sh` - 备份脚本
- ✅ `mysql/` - 数据库初始化文件
- ✅ `.dockerignore` - Docker忽略文件
- ✅ `DEPLOYMENT.md` - 部署文档

## 文件权限设置

上传完成后，设置正确的文件权限：

```bash
# 设置脚本执行权限
chmod +x /opt/bookstore/deploy.sh
chmod +x /opt/bookstore/backup.sh

# 设置目录权限
chmod -R 755 /opt/bookstore
``` 