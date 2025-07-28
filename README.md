# 在线书城系统

这是一个全栈在线书城应用程序，采用现代化的前后端分离架构。整个环境使用 Docker 进行了容器化，便于轻松安装和部署。

## 技术栈

- **后端**: Java, Spring Boot, Maven
- **前端**: Vue.js, Vite
- **Web 服务器**: Nginx
- **数据库**: MySQL
- **容器化**: Docker, Docker Compose

## 核心功能

- 用户认证 (注册与登录)
- 按分类浏览图书
- 图书搜索和详情查看
- 购物车管理
- 下单和订单历史查看
- 用于管理图书、用户和订单的后台管理面板

## 项目结构

```
.
├── backend/         # Java Spring Boot 后端应用
├── Sofeware_Demo/   # Vue.js 前端应用
├── mysql/           # MySQL 数据库初始化脚本
├── docker-compose.yml # 主要的 Docker Compose 配置文件
└── ...              # 部署脚本和指南
```

## 快速开始 (本地开发)

要在本地运行此项目，您需要安装 Docker 和 Docker Compose。

1.  **克隆仓库:**
    ```bash
    git clone <your-repository-url>
    cd AAA
    ```

2.  **配置环境变量:**
    复制环境变量示例文件，并根据需要进行编辑。默认值通常足以用于本地启动。
    ```bash
    cp env.example .env
    ```

3.  **使用 Docker Compose 构建并运行:**
    此命令将为前端和后端服务构建镜像，并在后台模式下启动所有容器。
    ```bash
    docker-compose up --build -d
    ```

4.  **访问应用程序:**
    - **前端**: [http://localhost:5137](http://localhost:5137)
    - **后端 API**: [http://localhost:8081](http://localhost:8081)

## 部署

此应用程序设计为使用 Docker 进行部署。

项目当前已部署在您的云服务器上，可以通过以下地址访问：
**http://117.72.197.93**

有关详细的部署说明，请参阅以下文档：
- `DEPLOYMENT.md`: 通用部署指南。
- `1panel_deployment_guide.md`: 使用 1Panel 部署的特定指南。
- `git_deploy.sh`: 使用 Git 的自动化部署脚本。

## 现有文档

有关更具体的信息，请参阅本仓库中的其他指南：
- `QUICK_START.md`
- `DEPLOYMENT.md`
- `SECURITY_GUIDE.md`
- `upload_guide.md`