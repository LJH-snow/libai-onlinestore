#!/bin/bash

# 1Panel在线图书销售系统快速部署脚本
# 适用于服务器: 183.9.70.176

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# 服务器信息
SERVER_IP="117.72.197.93"
PROJECT_DIR="/opt/bookstore"

# 日志函数
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 检查是否为root用户
check_root() {
    if [[ $EUID -ne 0 ]]; then
        log_error "请使用root用户运行此脚本"
        exit 1
    fi
}

# 检查Docker
check_docker() {
    log_info "检查Docker环境..."
    
    if ! command -v docker &> /dev/null; then
        log_error "Docker未安装，请在1panel中安装Docker"
        exit 1
    fi
    
    if ! command -v docker-compose &> /dev/null; then
        log_error "Docker Compose未安装"
        exit 1
    fi
    
    log_success "Docker环境检查通过"
}

# 创建项目目录
create_project_dir() {
    log_info "创建项目目录..."
    
    mkdir -p $PROJECT_DIR
    cd $PROJECT_DIR
    
    log_success "项目目录已创建: $PROJECT_DIR"
}

# 创建环境变量文件
create_env_file() {
    log_info "创建环境变量文件..."
    
    cat > .env << EOF
# 数据库配置
MYSQL_ROOT_PASSWORD=123456
MYSQL_DATABASE=onlinestore
MYSQL_USER=bookstore
MYSQL_PASSWORD=123456

# 应用配置
SPRING_PROFILES_ACTIVE=prod
JWT_SECRET=BookStoreJWTSecretKey2024!@#$%^&*()
JWT_EXPIRATION=86400000

# 服务器配置
SERVER_IP=$SERVER_IP
DOMAIN=$SERVER_IP
EOF
    
    log_success "环境变量文件已创建"
}

# 创建生产环境配置
create_prod_config() {
    log_info "创建生产环境配置文件..."
    
    mkdir -p backend/src/main/resources
    
    cat > backend/src/main/resources/application-prod.properties << EOF
# 生产环境数据库配置
spring.datasource.url=jdbc:mysql://mysql:3306/onlinestore?useSSL=false&serverTimezone=UTC&characterEncoding=utf8
spring.datasource.username=root
spring.datasource.password=\${MYSQL_ROOT_PASSWORD}

# 生产环境JWT配置
jwt.secret=\${JWT_SECRET}
jwt.expiration=\${JWT_EXPIRATION}

# 生产环境日志配置
logging.level.root=WARN
logging.level.com.gzmtu.backend=INFO
logging.file.name=/var/log/backend/application.log

# 生产环境端口
server.port=8081

# 生产环境安全配置
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false

# CORS配置
spring.web.cors.allowed-origins=http://$SERVER_IP,http://$SERVER_IP:80,http://$SERVER_IP:8081
spring.web.cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS
spring.web.cors.allowed-headers=*
spring.web.cors.allow-credentials=true
EOF
    
    log_success "生产环境配置文件已创建"
}

# 构建和启动服务
build_and_start() {
    log_info "构建Docker镜像..."
    docker-compose build
    
    log_info "启动服务..."
    docker-compose up -d
    
    log_info "等待服务启动..."
    sleep 45
    
    # 检查服务状态
    if docker-compose ps | grep -q "Up"; then
        log_success "服务启动成功"
    else
        log_error "服务启动失败"
        docker-compose logs
        exit 1
    fi
}

# 检查服务健康状态
check_health() {
    log_info "检查服务健康状态..."
    
    # 检查MySQL
    if docker-compose exec mysql mysqladmin ping -h localhost -u root -p123456 --silent 2>/dev/null; then
        log_success "MySQL服务正常"
    else
        log_warning "MySQL服务可能还在启动中"
    fi
    
    # 检查后端
    if curl -f http://localhost:8081/api/books 2>/dev/null; then
        log_success "后端服务正常"
    else
        log_warning "后端服务可能还在启动中"
    fi
    
    # 检查前端
    if curl -f http://localhost:80 2>/dev/null; then
        log_success "前端服务正常"
    else
        log_warning "前端服务可能还在启动中"
    fi
}

# 配置防火墙
configure_firewall() {
    log_info "配置防火墙..."
    
    # 检查ufw是否可用
    if command -v ufw &> /dev/null; then
        ufw allow 80/tcp
        ufw allow 443/tcp
        ufw allow 8081/tcp
        log_success "防火墙规则已配置"
    else
        log_warning "ufw未安装，请手动配置防火墙"
    fi
}

# 显示部署信息
show_deployment_info() {
    log_success "部署完成！"
    echo
    echo "=================================="
    echo "在线图书销售系统部署成功"
    echo "=================================="
    echo
    echo "=== 访问地址 ==="
    echo "前端地址: http://$SERVER_IP:80"
    echo "后端API: http://$SERVER_IP:8081"
    echo "1Panel管理: http://192.168.122.131:32435/e98e481bc4"
    echo
    echo "=== 默认账户 ==="
    echo "管理员: admin / 123456"
    echo "普通用户: user / 123456"
    echo
    echo "=== 管理命令 ==="
    echo "查看服务状态: docker-compose ps"
    echo "查看日志: docker-compose logs -f"
    echo "停止服务: docker-compose down"
    echo "重启服务: docker-compose restart"
    echo "备份数据: ./backup.sh"
    echo
    echo "=== 重要提醒 ==="
    echo "1. 请在云服务器控制台开放端口: 80, 443, 8081"
    echo "2. 生产环境请修改默认密码和JWT密钥"
    echo "3. 建议配置域名和SSL证书"
    echo "4. 定期备份数据"
    echo
}

# 主函数
main() {
    echo "=================================="
    echo "1Panel在线图书销售系统部署"
    echo "服务器: $SERVER_IP"
    echo "=================================="
    echo
    
    check_root
    check_docker
    create_project_dir
    create_env_file
    create_prod_config
    configure_firewall
    build_and_start
    check_health
    show_deployment_info
}

# 执行主函数
main "$@" 