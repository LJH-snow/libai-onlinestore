#!/bin/bash

# AAA在线书城系统快速测试脚本
# 适用于Ubuntu本地环境

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

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

# 检查Docker环境
check_docker() {
    log_info "检查Docker环境..."
    
    if ! command -v docker &> /dev/null; then
        log_error "Docker未安装，请先安装Docker"
        echo "安装命令："
        echo "curl -fsSL https://get.docker.com -o get-docker.sh"
        echo "sudo sh get-docker.sh"
        exit 1
    fi
    
    if ! command -v docker-compose &> /dev/null; then
        log_error "Docker Compose未安装"
        exit 1
    fi
    
    # 检查Docker服务状态
    if ! docker info &> /dev/null; then
        log_error "Docker服务未运行，请启动Docker服务"
        echo "启动命令: sudo systemctl start docker"
        exit 1
    fi
    
    log_success "Docker环境检查通过"
}

# 检查端口占用
check_ports() {
    log_info "检查端口占用..."
    
    ports=(8080 8082 3307)
    for port in "${ports[@]}"; do
        if netstat -tulpn 2>/dev/null | grep -q ":$port "; then
            log_warning "端口 $port 已被占用"
            echo "占用进程："
            netstat -tulpn | grep ":$port "
            echo "如需停止占用进程，请执行: sudo lsof -ti:$port | xargs sudo kill -9"
        else
            log_success "端口 $port 可用"
        fi
    done
}

# 准备环境文件
prepare_env() {
    log_info "准备环境配置..."
    
    if [ ! -f .env ]; then
        if [ -f env.example ]; then
            cp env.example .env
            log_success "已创建 .env 文件"
        else
            log_warning "env.example 文件不存在，创建默认配置"
            cat > .env << EOF
# 数据库配置
MYSQL_ROOT_PASSWORD=123456
MYSQL_PASSWORD=123456
MYSQL_USER=bookstore
MYSQL_PORT=3307

# 应用配置
BACKEND_PORT=8082
FRONTEND_PORT=8080

# JWT配置
JWT_SECRET=BookStoreJWTSecretKey2024
JWT_EXPIRATION=86400000
EOF
        fi
    else
        log_success ".env 文件已存在"
    fi
}

# 构建和启动服务
start_services() {
    log_info "构建和启动服务..."
    
    # 停止可能存在的旧容器
    docker-compose down 2>/dev/null || true
    
    # 构建并启动服务
    log_info "构建Docker镜像..."
    docker-compose build
    
    log_info "启动服务..."
    docker-compose up -d
    
    log_info "等待服务启动..."
    sleep 30
}

# 检查服务状态
check_services() {
    log_info "检查服务状态..."
    
    # 检查容器状态
    if docker-compose ps | grep -q "Up"; then
        log_success "容器启动成功"
        docker-compose ps
    else
        log_error "容器启动失败"
        docker-compose logs
        return 1
    fi
    
    # 等待服务完全启动
    log_info "等待服务完全启动..."
    sleep 30
    
    # 检查MySQL
    log_info "检查MySQL连接..."
    if docker-compose exec -T mysql mysqladmin ping -h localhost -u root -p123456 --silent 2>/dev/null; then
        log_success "MySQL服务正常"
    else
        log_warning "MySQL服务可能还在启动中，等待更长时间..."
        sleep 30
        if docker-compose exec -T mysql mysqladmin ping -h localhost -u root -p123456 --silent 2>/dev/null; then
            log_success "MySQL服务正常"
        else
            log_error "MySQL服务异常"
            docker-compose logs mysql
        fi
    fi
    
    # 检查后端API
    log_info "检查后端API..."
    max_attempts=6
    attempt=1
    while [ $attempt -le $max_attempts ]; do
        if curl -f http://localhost:8082/api/books &>/dev/null; then
            log_success "后端API服务正常"
            break
        else
            log_info "后端API尚未就绪，等待中... ($attempt/$max_attempts)"
            sleep 10
            ((attempt++))
        fi
    done
    
    if [ $attempt -gt $max_attempts ]; then
        log_warning "后端API可能还在启动中"
        docker-compose logs backend
    fi
    
    # 检查前端
    log_info "检查前端服务..."
    if curl -f http://localhost:8080 &>/dev/null; then
        log_success "前端服务正常"
    else
        log_warning "前端服务可能还在启动中"
        docker-compose logs frontend
    fi
}

# 运行功能测试
run_tests() {
    log_info "运行功能测试..."
    
    # 测试图书列表API
    log_info "测试图书列表API..."
    if curl -s http://localhost:8082/api/books | grep -q "books\|data\|\[\]"; then
        log_success "图书列表API测试通过"
    else
        log_warning "图书列表API测试失败"
    fi
    
    # 测试分类列表API
    log_info "测试分类列表API..."
    if curl -s http://localhost:8082/api/categories | grep -q "categories\|data\|\[\]"; then
        log_success "分类列表API测试通过"
    else
        log_warning "分类列表API测试失败"
    fi
    
    # 测试用户注册API
    log_info "测试用户注册API..."
    response=$(curl -s -X POST http://localhost:8082/api/auth/register \
        -H "Content-Type: application/json" \
        -d '{"username":"testuser'$(date +%s)'","password":"123456","email":"test'$(date +%s)'@example.com"}')
    
    if echo "$response" | grep -q "success\|token\|用户"; then
        log_success "用户注册API测试通过"
    else
        log_warning "用户注册API测试结果: $response"
    fi
}

# 显示测试结果
show_results() {
    log_success "测试完成！"
    echo
    echo "=================================="
    echo "AAA在线书城系统本地测试结果"
    echo "=================================="
    echo
    echo "=== 访问地址 ==="
    echo "前端网站: http://localhost:8080"
    echo "后端API: http://localhost:8082"
    echo "数据库: localhost:3307 (用户: root, 密码: 123456)"
    echo
    echo "=== 测试账户 ==="
    echo "管理员: admin / 123456"
    echo "普通用户: user / 123456"
    echo
    echo "=== 管理命令 ==="
    echo "查看服务状态: docker-compose ps"
    echo "查看日志: docker-compose logs -f"
    echo "停止服务: docker-compose down"
    echo "重启服务: docker-compose restart"
    echo
    echo "=== 下一步 ==="
    echo "1. 在浏览器中访问 http://localhost:8080 测试前端"
    echo "2. 测试用户注册和登录功能"
    echo "3. 测试图书浏览和购买流程"
    echo "4. 如果测试正常，可以部署到云服务器"
    echo
}

# 主函数
main() {
    echo "=================================="
    echo "AAA在线书城系统快速测试"
    echo "=================================="
    echo
    
    check_docker
    check_ports
    prepare_env
    start_services
    check_services
    run_tests
    show_results
}

# 错误处理
trap 'log_error "测试过程中发生错误，请检查日志"; docker-compose logs' ERR

# 执行主函数
main "$@"
