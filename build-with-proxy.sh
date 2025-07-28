#!/bin/bash

GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m'

# 自动检测 docker-compose.yml 是否存在
if [ ! -f "docker-compose.yml" ]; then
  echo -e "${RED}❌ 当前目录找不到 docker-compose.yml，请先 cd 到你的项目目录再运行。${NC}"
  exit 1
fi

echo -e "${GREEN}✅ 设置 Docker 构建代理环境变量...${NC}"
export DOCKER_BUILDKIT=0
export HTTP_PROXY=http://192.168.122.1:10809
export HTTPS_PROXY=http://192.168.122.1:10809

echo -e "${GREEN}🧹 清理 Docker 构建缓存...${NC}"
docker builder prune -f

echo -e "${GREEN}🔧 开始无缓存构建镜像...${NC}"
docker-compose build --no-cache

echo -e "${GREEN}🚀 镜像构建完成，可以 docker-compose up -d 启动服务了${NC}"
