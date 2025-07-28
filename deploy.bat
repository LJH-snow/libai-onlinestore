@echo off
chcp 65001 >nul
echo ==================================
echo 在线图书销售系统 - Windows部署脚本
echo ==================================
echo.

echo [信息] 检查Docker安装...
docker --version >nul 2>&1
if errorlevel 1 (
    echo [错误] Docker未安装，请先安装Docker Desktop
    pause
    exit /b 1
)
echo [成功] Docker已安装

echo [信息] 检查Docker Compose安装...
docker-compose --version >nul 2>&1
if errorlevel 1 (
    echo [错误] Docker Compose未安装
    pause
    exit /b 1
)
echo [成功] Docker Compose已安装

echo [信息] 创建环境变量文件...
if not exist .env (
    (
        echo # 数据库配置
        echo MYSQL_ROOT_PASSWORD=BookStore2024!
        echo MYSQL_DATABASE=onlinestore
        echo MYSQL_USER=bookstore
        echo MYSQL_PASSWORD=BookStore2024!
        echo.
        echo # 应用配置
        echo SPRING_PROFILES_ACTIVE=prod
        echo JWT_SECRET=BookStoreJWTSecretKey2024!@#$%%^&*^(
        echo JWT_EXPIRATION=86400000
        echo.
        echo # 域名配置
        echo DOMAIN=localhost
    ) > .env
    echo [成功] 环境变量文件已创建
) else (
    echo [信息] 环境变量文件已存在
)

echo [信息] 创建生产环境配置文件...
if not exist backend\src\main\resources mkdir backend\src\main\resources
(
    echo # 生产环境数据库配置
    echo spring.datasource.url=jdbc:mysql://mysql:3306/onlinestore?useSSL=false^&serverTimezone=UTC^&characterEncoding=utf8
    echo spring.datasource.username=root
    echo spring.datasource.password=%%MYSQL_ROOT_PASSWORD%%
    echo.
    echo # 生产环境JWT配置
    echo jwt.secret=%%JWT_SECRET%%
    echo jwt.expiration=%%JWT_EXPIRATION%%
    echo.
    echo # 生产环境日志配置
    echo logging.level.root=WARN
    echo logging.level.com.gzmtu.backend=INFO
    echo logging.file.name=/var/log/backend/application.log
    echo.
    echo # 生产环境端口
    echo server.port=8081
    echo.
    echo # 生产环境安全配置
    echo spring.jpa.hibernate.ddl-auto=validate
    echo spring.jpa.show-sql=false
) > backend\src\main\resources\application-prod.properties
echo [成功] 生产环境配置文件已创建

echo [信息] 构建Docker镜像...
docker-compose build
if errorlevel 1 (
    echo [错误] 构建失败
    pause
    exit /b 1
)

echo [信息] 启动服务...
docker-compose up -d
if errorlevel 1 (
    echo [错误] 启动失败
    pause
    exit /b 1
)

echo [信息] 等待服务启动...
timeout /t 30 /nobreak >nul

echo [信息] 检查服务状态...
docker-compose ps

echo.
echo [成功] 部署完成！
echo.
echo === 部署信息 ===
echo 前端地址: http://localhost:80
echo 后端API: http://localhost:8081
echo 数据库: localhost:3306
echo.
echo === 默认账户 ===
echo 管理员: admin / 123456
echo 普通用户: user / 123456
echo.
echo === 常用命令 ===
echo 查看服务状态: docker-compose ps
echo 查看日志: docker-compose logs -f
echo 停止服务: docker-compose down
echo 重启服务: docker-compose restart
echo.
echo === 数据备份 ===
echo 备份数据库: docker-compose exec mysql mysqldump -u root -p onlinestore ^> backup.sql
echo.
pause 