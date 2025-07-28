# 🚀 1Panel在线图书销售系统快速部署

## 📋 服务器信息
- **服务器IP**: 192.168.122.131
- **1Panel登录地址**: http://192.168.122.131:32435/e98e481bc4
- **用户名**: 18202786f9
- **密码**: 19f317d806

## ⚡ 快速部署步骤

### 第一步：登录1Panel
1. 打开浏览器访问：http://192.168.122.131:32435/e98e481bc4
2. 输入用户名：`18202786f9`
3. 输入密码：`19f317d806`

### 第二步：上传项目文件
**方法A：通过1Panel文件管理器**
1. 点击左侧菜单"文件管理"
2. 创建目录：`/opt/bookstore`
3. 进入该目录，上传所有项目文件

**方法B：通过SSH上传（推荐）**
```bash
# 在本地Windows执行
scp -r D:\AAA\* root@192.168.122.131:/opt/bookstore/
```

### 第三步：执行部署
**通过1Panel终端：**
1. 点击左侧菜单"终端"
2. 执行以下命令：

```bash
# 进入项目目录
cd /opt/bookstore

# 设置脚本权限
chmod +x deploy.sh
chmod +x backup.sh
chmod +x 1panel_deploy.sh

# 执行1Panel专用部署脚本
./1panel_deploy.sh
```

**或者通过SSH连接：**
```bash
# 连接到服务器
ssh root@192.168.122.131

# 进入项目目录并部署
cd /opt/bookstore
./1panel_deploy.sh
```

### 第四步：配置防火墙
在云服务器控制台开放以下端口：
- **80** - HTTP前端访问
- **443** - HTTPS访问（推荐）
- **8081** - 后端API访问

### 第五步：验证部署
部署完成后，访问以下地址测试：

- **前端网站**: http://192.168.122.131:80
- **后端API**: http://192.168.122.131:8081
- **1Panel管理**: http://192.168.122.131:32435/e98e481bc4

## 🔑 默认账户
- **管理员**: `admin` / `123456`
- **普通用户**: `user` / `123456`

## 🛠️ 常用管理命令
```bash
# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f

# 重启服务
docker-compose restart

# 停止服务
docker-compose down

# 备份数据
./backup.sh
```

## ⚠️ 重要提醒
1. **生产环境请修改默认密码**
2. **建议配置域名和SSL证书**
3. **定期备份数据**
4. **监控系统资源使用情况**

## 🆘 遇到问题？
1. 查看详细部署文档：`DEPLOYMENT.md`
2. 查看1Panel专用指南：`1panel_deployment_guide.md`
3. 检查服务日志：`docker-compose logs -f`
4. 访问1Panel官方文档：https://1panel.cn/docs 