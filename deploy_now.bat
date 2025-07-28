@echo off
chcp 65001 >nul
echo ==================================
echo 在线图书销售系统 - 快速部署
echo 服务器: 192.168.122.131
echo ==================================
echo.

echo [信息] 开始上传项目文件到服务器...
echo.

echo [步骤1] 上传项目文件...
scp -r D:\AAA\* root@192.168.122.131:/opt/bookstore/

if errorlevel 1 (
    echo [错误] 文件上传失败，请检查网络连接或SSH配置
    pause
    exit /b 1
)

echo [成功] 文件上传完成！
echo.

echo [步骤2] 连接到服务器执行部署...
echo.

echo 请在SSH终端中执行以下命令：
echo.
echo cd /opt/bookstore
echo chmod +x 1panel_deploy.sh
echo ./1panel_deploy.sh
echo.

echo [信息] 部署完成后，您可以访问：
echo 前端网站: http://192.168.122.131:80
echo 后端API: http://192.168.122.131:8081
echo 1Panel管理: http://192.168.122.131:32435/e98e481bc4
echo.

echo [提示] 如果SSH连接失败，请确保：
echo 1. 服务器SSH服务已启动
echo 2. 防火墙允许SSH连接（端口22）
echo 3. 您有服务器的root访问权限
echo.

pause 