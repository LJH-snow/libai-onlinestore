#!/bin/bash

# 在线图书销售系统 - 数据备份脚本

set -e

# 配置
BACKUP_DIR="/opt/bookstore/backups"
DATE=$(date +%Y%m%d_%H%M%S)
MYSQL_ROOT_PASSWORD=${MYSQL_ROOT_PASSWORD:-BookStore2024!}

# 创建备份目录
mkdir -p $BACKUP_DIR

echo "开始备份数据..."

# 备份数据库
echo "备份MySQL数据库..."
docker-compose exec mysql mysqldump -u root -p${MYSQL_ROOT_PASSWORD} onlinestore > $BACKUP_DIR/database_$DATE.sql

# 备份应用数据
echo "备份应用数据..."
tar -czf $BACKUP_DIR/app_data_$DATE.tar.gz \
    --exclude=node_modules \
    --exclude=target \
    --exclude=.git \
    --exclude=backups \
    .

# 备份Docker镜像
echo "备份Docker镜像..."
docker save bookstore_backend:latest > $BACKUP_DIR/backend_image_$DATE.tar
docker save bookstore_frontend:latest > $BACKUP_DIR/frontend_image_$DATE.tar

# 清理旧备份（保留最近7天）
echo "清理旧备份..."
find $BACKUP_DIR -name "*.sql" -mtime +7 -delete
find $BACKUP_DIR -name "*.tar.gz" -mtime +7 -delete
find $BACKUP_DIR -name "*.tar" -mtime +7 -delete

echo "备份完成！"
echo "备份文件位置: $BACKUP_DIR"
ls -la $BACKUP_DIR/*$DATE* 