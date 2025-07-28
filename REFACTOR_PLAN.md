# Spring Boot to Golang Refactor Plan

本文件详细阐述了将项目后端从 Java Spring Boot 重构为 Golang 的技术路线、选型和实施步骤。

## 1. 重构目标

主要目标是解决原 Java Spring Boot 应用在资源有限的服务器上（低核心数、低内存）运行时负担过重的问题。通过使用 Golang，我们期望达成以下效果：

- **显著降低资源占用**：减少 CPU 和内存的使用。
- **提升应用性能**：利用 Go 的高并发特性，提高 API 响应速度。
- **简化部署**：编译成单个静态二进制文件，创建更小、更安全的 Docker 镜像。

## 2. 核心决策：保持前后端分离

**我们将保持现有的前后端分离架构，仅重构后端。**

- **前端**: 继续使用并保留现有的 `Sofeware_Demo` (Vue 3) 项目。这是现代 Web 开发的最佳实践，可以确保开发效率和优秀的用户体验。
- **后端**: 新建一个 `backend-go` 项目，使用 Go 语言重新实现所有 API 接口。
- **对接**: 当 Go 后端开发完成后，只需在 Vue 前端的配置文件中修改 API 服务器的地址即可，前端代码无需大规模改动。

## 3. 技术选型 (Go Stack)

为了高效地进行重构，我们选择一套在 Go 社区中经过验证的、成熟的库，以对标 Spring Boot 的核心功能。

| 功能 | Spring Boot | Golang 推荐 | 理由 |
| :--- | :--- | :--- | :--- |
| **Web 框架** | Spring Web (MVC) | **Gin** (`github.com/gin-gonic/gin`) | 性能极高，路由功能强大，API 设计简洁，社区庞大，上手快。 |
| **数据库 ORM** | Spring Data JPA | **GORM** (`gorm.io/gorm`) | 功能完善的 ORM，支持关联、事务、迁移，最接近 JPA 的开发体验。 |
| **配置管理** | `application.properties` | **Viper** (`github.com/spf13/viper`) | 支持多种格式（YAML, JSON, .env），可无缝集成环境变量，功能强大。 |
| **校验** | Hibernate Validator | **Validator** (`github.com/go-playground/validator`) | Gin 框架通常会集成此库，用于请求结构体的字段校验。 |

## 4. 实施步骤

建议采用增量、模块化的方式进行重构，而不是一次性全部重写。

1.  **环境搭建 (已完成)**
    - 创建 `backend-go` 目录。
    - 初始化 Go 模块 (`go mod init`)。
    - 创建基础的 Gin 服务器和 Dockerfile。

2.  **配置与数据库连接**
    - 使用 Viper 加载配置文件（如 `config.yaml`），配置数据库连接信息、服务器端口等。
    - 初始化 GORM，并确保与 MySQL 数据库的连接正常。

3.  **模型 (Entity) 迁移**
    - 将 Java `entity` 目录下的所有实体类 (如 `User`, `Book`, `Order`)，逐一转换为 Go 的结构体 (Struct)，并使用 GORM 的标签 (`gorm:"..."`) 来定义表名、字段名和关联关系。

4.  **数据访问层 (Repository) 实现**
    - 创建 `repository` 目录，为每个模型编写数据访问函数（增删改查）。

5.  **业务逻辑层 (Service) 实现**
    - 创建 `service` 目录，处理业务逻辑。此处的代码应与原 Java Service 层逻辑对等。

6.  **接口层 (Controller/Handler) 实现**
    - 创建 `handler` 目录，使用 Gin 框架，将原有的 Controller 中的每一个 API 接口，用 Go 重新实现。确保路由、请求参数（JSON、查询参数）、响应格式与原来保持一致。

7.  **容器化与测试**
    - 编写 API 测试，确保新旧后端行为一致。
    - 使用项目中的 `Dockerfile` 构建生产镜像，并更新 `docker-compose.yml` 文件，将 `backend` 服务指向新的 Go 应用镜像。

## 5. 下一步

当前项目骨架已搭建完毕。下一步是实现 **步骤 2：配置与数据库连接**。
