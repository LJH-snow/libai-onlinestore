#!/bin/bash

# 在线图书销售系统 - 自动化部署脚本
# 适用于1panel环境

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

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

# 检查系统要求
check_system_requirements() {
    log_info "检查系统要求..."
    
    # 检查操作系统
    if [[ "$OSTYPE" == "linux-gnu"* ]]; then
        log_success "操作系统: Linux"
    else
        log_error "不支持的操作系统: $OSTYPE"
        exit 1
    fi
    
    # 检查内存
    total_mem=$(free -m | awk 'NR==2{printf "%.0f", $2/1024}')
    if [ $total_mem -lt 4 ]; then
        log_warning "内存不足: ${total_mem}GB (建议至少4GB)"
    else
        log_success "内存: ${total_mem}GB"
    fi
    
    # 检查磁盘空间
    available_space=$(df -BG . | awk 'NR==2{print $4}' | sed 's/G//')
    if [ $available_space -lt 20 ]; then
        log_warning "磁盘空间不足: ${available_space}GB (建议至少20GB)"
    else
        log_success "可用磁盘空间: ${available_space}GB"
    fi
}

# 检查Docker
check_docker() {
    log_info "检查Docker安装..."
    
    if command -v docker &> /dev/null; then
        log_success "Docker已安装"
        docker --version
    else
        log_error "Docker未安装，请先安装Docker"
        exit 1
    fi
    
    if command -v docker-compose &> /dev/null; then
        log_success "Docker Compose已安装"
        docker-compose --version
    else
        log_error "Docker Compose未安装，请先安装Docker Compose"
        exit 1
    fi
}

# 检查1panel
check_1panel() {
    log_info "检查1panel安装..."
    
    if command -v 1panel &> /dev/null; then
        log_success "1panel已安装"
        1panel info
    else
        log_warning "1panel未安装，请先安装1panel"
        log_info "安装命令: curl -sSL https://resource.fit2cloud.com/1panel/package/quick_start.sh | sh"
    fi
}

# 创建环境变量文件
create_env_file() {
    log_info "创建环境变量文件..."
    
    if [ ! -f .env ]; then
        cat > .env << EOF
# 数据库配置
MYSQL_ROOT_PASSWORD=BookStore2024!
MYSQL_DATABASE=onlinestore
MYSQL_USER=bookstore
MYSQL_PASSWORD=BookStore2024!

# 应用配置
SPRING_PROFILES_ACTIVE=prod
JWT_SECRET=BookStoreJWTSecretKey2024!@#$%^&*()
JWT_EXPIRATION=86400000

# 域名配置
DOMAIN=localhost
EOF
        log_success "环境变量文件已创建"
    else
        log_info "环境变量文件已存在"
    fi
}

# 创建生产环境配置文件
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
    sleep 30
    
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
    if docker-compose exec mysql mysqladmin ping -h localhost -u root -p${MYSQL_ROOT_PASSWORD:-BookStore2024!} --silent; then
        log_success "MySQL服务正常"
    else
        log_error "MySQL服务异常"
    fi
    
    # 检查后端
    if curl -f http://localhost:8081/actuator/health 2>/dev/null; then
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

# 显示部署信息
show_deployment_info() {
    log_success "部署完成！"
    echo
    echo "=== 部署信息 ==="
    echo "前端地址: http://localhost:80"
    echo "后端API: http://localhost:8081"
    echo "数据库: localhost:3306"
    echo
    echo "=== 默认账户 ==="
    echo "管理员: admin / 123456"
    echo "普通用户: user / 123456"
    echo
    echo "=== 常用命令 ==="
    echo "查看服务状态: docker-compose ps"
    echo "查看日志: docker-compose logs -f"
    echo "停止服务: docker-compose down"
    echo "重启服务: docker-compose restart"
    echo
    echo "=== 数据备份 ==="
    echo "备份数据库: docker-compose exec mysql mysqldump -u root -p onlinestore > backup.sql"
    echo
}

# 主函数
main() {
    echo "=================================="
    echo "在线图书销售系统 - 自动化部署"
    echo "=================================="
    echo
    
    check_system_requirements
    check_docker
    check_1panel
    create_env_file
    create_prod_config
    build_and_start
    check_health
    show_deployment_info
}

# 执行主函数
main "$@" 