# 🔒 在线图书销售系统 - 安全部署指南

## ⚠️ 重要安全提醒

**您的GitHub仓库现在是公开的，请立即采取以下安全措施：**

### 1. 立即更改所有相关密码

由于您的个人信息可能已经暴露，请立即更改以下密码：

- **GitHub账户密码**
- **其他使用相同密码的网站账户**
- **邮箱密码**
- **其他重要账户密码**

### 2. 生成安全的随机密码

使用以下方法生成安全密码：

```bash
# 方法1：使用在线密码生成器
# 访问：https://passwordsgenerator.net/

# 方法2：使用命令行生成
openssl rand -base64 32

# 方法3：使用Python生成
python3 -c "import secrets; print(secrets.token_urlsafe(32))"
```

### 3. 安全部署步骤

#### 第一步：创建安全的环境变量文件

```bash
# 复制环境变量模板
cp env.example .env

# 编辑环境变量文件，设置安全密码
nano .env
```

 在 `.env` 文件中设置：
 ```bash
 # 数据库配置 - 使用安全密码
 MYSQL_ROOT_PASSWORD=123456
 MYSQL_PASSWORD=123456

# JWT配置 - 使用强密钥
JWT_SECRET=YourVerySecureJWTSecretKey2024!@#$%^&*()_+{}|:<>?[]\;',./

# 应用配置
SPRING_PROFILES_ACTIVE=prod
JWT_EXPIRATION=86400000

# 服务器配置
SERVER_IP=192.168.122.131
DOMAIN=192.168.122.131
```

#### 第二步：提交安全更改到GitHub

```bash
# 添加更改
git add .

# 提交更改
git commit -m "🔒 安全更新：移除敏感信息，使用环境变量"

# 推送到GitHub
git push origin main
```

#### 第三步：部署到1panel

```bash
# 通过SSH连接到服务器
ssh root@192.168.122.131

# 创建项目目录
mkdir -p /opt/bookstore
cd /opt/bookstore

# 克隆最新代码
git clone https://github.com/LJH-snow/online_book_system.git .

# 创建安全的环境变量文件
cp env.example .env

# 编辑环境变量文件，设置您的安全密码
nano .env

# 设置脚本权限
chmod +x deploy.sh
chmod +x backup.sh
chmod +x 1panel_deploy.sh

# 执行部署
./1panel_deploy.sh
```

### 4. 密码安全建议

#### 强密码特征：
- **长度**：至少16个字符
- **复杂度**：包含大小写字母、数字、特殊字符
- **唯一性**：每个网站使用不同密码
- **随机性**：避免使用个人信息

#### 示例强密码：
```
K9#mP$vL2@nX8&qR5!jH7*wE3^sA6%tY4
```

### 5. 额外安全措施

#### 启用双因素认证：
- **GitHub**：Settings → Security → Two-factor authentication
- **邮箱**：启用2FA
- **其他重要账户**：启用2FA

#### 使用密码管理器：
- **Bitwarden**（免费）
- **1Password**
- **LastPass**

#### 定期检查：
- 定期更改密码
- 监控账户异常活动
- 使用 https://haveibeenpwned.com/ 检查密码泄露

### 6. 部署后的安全检查

```bash
# 检查服务状态
docker-compose ps

# 检查日志中是否有敏感信息
docker-compose logs | grep -i password

# 测试数据库连接
docker-compose exec mysql mysql -u root -p

# 检查防火墙设置
ufw status
```

### 7. 紧急联系

如果发现安全威胁：
1. 立即更改所有相关密码
2. 联系相关网站客服
3. 监控账户异常活动
4. 考虑冻结信用卡等敏感账户

## 🛡️ 安全部署完成

完成以上步骤后，您的系统将安全部署，个人信息将得到保护。

**记住：安全是持续的过程，请定期更新密码和检查安全设置！** 