# Fun OJ - 在线编程测评系统

> Fun OJ -- let's have fun

基于 Spring Cloud 微服务架构的在线编程测评系统（Online Judge），采用 BS 架构，支持在线编程练习、竞赛管理、实时判题等功能。

## 项目概述

- **总代码量**：约 1.5 万行
- **接口数量**：40+ 个 RESTful API
- **数据库表**：9 张
- **微服务数量**：5 个（含网关）
- **架构模式**：BS（Browser-Server），前后端分离

---

## 项目功能

### 用户端功能

| 功能 | 说明 |
|------|------|
| 短信注册/登录 | 通过阿里云短信服务发送验证码，JWT Token 认证 |
| 个人信息管理 | 修改个人资料、上传头像（阿里云 OSS） |
| 题目浏览 | 分页题目列表、题目详情、上一题/下一题导航 |
| 题目全文搜索 | 基于 Elasticsearch 的全文检索 |
| 在线编程练习 | 浏览器内编写代码并提交 |
| 代码编译运行 | 同步（OpenFeign）和异步（RabbitMQ）两种判题模式 |
| 实时判题 | Docker 沙箱执行，支持时间/内存/CPU 限制 |
| 竞赛列表 | 浏览未结束和历史竞赛 |
| 竞赛报名/参赛 | 报名竞赛、竞赛内答题、查看排行榜 |
| 系统消息 | 接收竞赛结果、排名通知等系统消息 |
| 文件上传 | 支持文件上传，Redis 限制每日上传次数 |

### 管理端功能

| 功能 | 说明 |
|------|------|
| 管理员登录 | 用户名/密码登录（BCrypt 加密） |
| 题目管理 | 题目 CRUD、测试用例管理、ES 索引同步 |
| 竞赛管理 | 竞赛 CRUD、发布/取消、题目分配 |
| 用户管理 | 用户列表、启用/禁用用户状态 |
| 管理员管理 | 新增管理员账号 |
| 定时任务 | 竞赛列表缓存刷新、赛后成绩计算与排名 |

---

## 系统架构

```
┌─────────────┐     ┌─────────────────┐     ┌──────────────────┐
│   Browser   │────▶│   Nginx (80)    │────▶│  Vue 3 Frontend  │
│  (用户访问)  │     │  反向代理/静态资源 │     │   (Vite 构建)     │
└─────────────┘     └────────┬────────┘     └──────────────────┘
                             │ /api
                             ▼
                  ┌─────────────────────┐
                  │  Spring Cloud       │
                  │  Gateway (网关)      │
                  │  JWT 认证 + 路由     │
                  └─────────┬───────────┘
                            │
              ┌─────────────┼─────────────┐
              ▼             ▼             ▼
     ┌──────────────┐ ┌──────────┐ ┌──────────┐
     │  oj-system   │ │ oj-friend│ │ oj-judge │
     │  管理端服务   │ │ 用户端服务│ │ 判题服务  │
     └──────┬───────┘ └────┬─────┘ └────┬─────┘
            │              │            │
            ▼              ▼            ▼
     ┌─────────────────────────────────────────┐
     │           基础设施层                      │
     │  MySQL │ Redis │ Nacos │ RabbitMQ       │
     │  Elasticsearch │ XXL-Job │ Ali OSS      │
     └─────────────────────────────────────────┘
```

### 微服务划分

| 模块 | 说明 | 职责 |
|------|------|------|
| **oj-gateway** | API 网关 | 请求路由、JWT 认证、权限校验 |
| **oj-api** | Feign 接口模块 | 服务间调用接口定义、共享 DTO/VO |
| **oj-common** | 公共模块（9 个子模块） | 核心工具、安全、Redis、MyBatis、ES、短信、文件、MQ、Swagger |
| **oj-system** | 管理端服务 | 题目管理、竞赛管理、用户管理 |
| **oj-friend** | 用户端服务 | 注册登录、题目浏览、代码提交、竞赛参与、消息系统 |
| **oj-judge** | 判题服务 | Docker 沙箱代码执行、编译检查、结果判定 |
| **oj-job** | 定时任务服务 | XXL-Job 调度、竞赛缓存刷新、成绩计算 |

### oj-common 子模块

| 子模块 | 说明 |
|--------|------|
| oj-common-core | 核心工具类、JWT、枚举、常量、基础实体 |
| oj-common-security | Token 拦截器、全局异常处理 |
| oj-common-redis | Redis 配置与服务封装 |
| oj-common-swagger | SpringDoc OpenAPI 3 接口文档 |
| oj-common-mybatis | MyBatis-Plus 自动填充配置 |
| oj-common-elasticsearch | Elasticsearch 依赖 |
| oj-common-message | 阿里云短信服务 |
| oj-common-file | 阿里云 OSS 文件上传 |
| oj-common-rabbit | RabbitMQ 配置 |

---

## 技术栈

### 后端技术

| 技术 | 版本 | 说明 |
|------|------|------|
| **Java** | 17 | 开发语言 |
| **Spring Boot** | 3.0.1 | 应用框架 |
| **Spring Cloud** | 2022.0.0 | 微服务框架 |
| **Spring Cloud Alibaba** | 2022.0.0.0-RC2 | 阿里巴巴微服务组件 |
| **Spring Cloud Gateway** | - | API 网关（WebFlux 响应式） |
| **Spring Cloud OpenFeign** | - | 声明式服务调用 |
| **Spring Cloud LoadBalancer** | - | 客户端负载均衡 |
| **Nacos** | - | 服务注册发现 + 配置中心 |
| **MyBatis-Plus** | 3.5.5 | ORM 框架 |
| **MySQL** | - | 关系型数据库 |
| **Redis** | - | 缓存、Token 存储 |
| **RabbitMQ** | - | 消息队列（异步判题） |
| **Elasticsearch** | - | 全文搜索引擎 |
| **XXL-Job** | 2.4.0 | 分布式任务调度 |
| **Docker Java** | 3.3.4 | Docker 容器管理（沙箱判题） |
| **SpringDoc OpenAPI** | 2.2.0 | API 接口文档 |
| **Fastjson2** | 2.0.43 | JSON 序列化 |
| **JJWT** | 0.9.1 | JWT Token 生成与解析 |
| **Hutool** | 5.8.22 | Java 工具库 |
| **PageHelper** | 2.0.0 | MyBatis 分页插件 |
| **阿里云 OSS SDK** | 3.17.4 | 对象存储 |
| **阿里云短信 SDK** | 2.0.0 / 3.0.0 | 短信验证码服务 |
| **TransmittableThreadLocal** | 2.14.4 | 跨线程池上下文传递 |

### 前端技术

| 技术 | 说明 |
|------|------|
| **Vue 3** | 前端框架 |
| **Vite** | 构建工具 |
| **TypeScript** | 类型安全 |
| **Axios** | HTTP 请求库 |
| **Ant Design Vue** | UI 组件库 |
| **Pinia** | 状态管理 |
| **ESLint** | 代码规范 |

### 基础设施

| 组件 | 说明 |
|------|------|
| **Nginx** | 反向代理、前端静态资源托管、负载均衡 |
| **Docker** | 判题沙箱环境（openjdk:8-jdk-alpine） |
| **阿里云 ECS** | 云服务器部署 |
| **阿里云 OSS** | 文件/图片云存储 |
| **阿里云 SMS** | 短信验证码服务 |

---

## 判题引擎

判题服务（oj-judge）是系统核心，采用 Docker 容器作为代码执行沙箱：

### 执行流程

1. 接收代码提交（同步 OpenFeign 或异步 RabbitMQ）
2. 创建 Docker 容器（`openjdk:8-jdk-alpine`）
3. 在容器内执行 `javac` 编译
4. 编译通过后，逐个执行测试用例
5. 比对输出结果，记录执行时间和内存
6. 返回判题结果（通过/编译错误/超时/内存超限/运行错误）

### 安全措施

| 措施 | 说明 |
|------|------|
| 网络隔离 | `networkMode: none`，禁止容器网络访问 |
| 只读文件系统 | `readonlyRootfs: true` |
| 内存限制 | 默认 100MB |
| 内存交换限制 | 禁止使用 swap |
| CPU 限制 | 限制 CPU 使用率 |
| 超时控制 | 默认 5 秒执行超时 |

### 两种实现

- **SandboxServiceImpl**：每次提交创建/销毁容器（简单可靠）
- **SandboxPoolServiceImpl**：预热容器池（`BlockingQueue`），复用容器提升性能

---

## 认证与安全

### 认证流程

```
用户注册 → MD5加盐加密密码 → 存入数据库
    ↓
用户登录 → 验证密码 → 生成 UUID Token → 存入 Redis（TTL 30分钟）
    ↓
返回 Token → 前端存入 localStorage → 后续请求携带 Token
    ↓
网关拦截 → 校验 Token → 注入用户信息到请求头 → 转发到后端服务
```

### 权限控制

| URL 前缀 | 角色 | 说明 |
|----------|------|------|
| `/system/**` | 管理员 | 管理端接口 |
| `/friend/**` | 普通用户 | 用户端接口 |
| 白名单路径 | 无需认证 | 登录、注册、文档等 |

---

## 数据库设计

系统共使用 9 张数据库表，主要表结构：

| 表名 | 说明 | 关键字段 |
|------|------|----------|
| tb_sys_user | 系统用户/管理员 | userAccount, userPassword, userRole |
| tb_user | 普通用户 | userAccount, userPassword, userName, userAvatar, userRole |
| tb_question | 题目 | title, content, tags, judgeCase, judgeConfig |
| tb_exam | 竞赛 | title, description, startTime, endTime |
| tb_exam_question | 竞赛题目关联 | examId, questionId |
| tb_user_question_submit | 用户提交记录 | userId, questionId, code, status |
| tb_exam_user | 竞赛报名 | examId, userId |
| tb_message | 系统消息 | userId, content, isRead |
| tb_exam_result | 竞赛成绩 | examId, userId, score, rank |

---

## 项目结构

```
Online-Judge/
├── oj-gateway/                    # API 网关服务
│   └── src/main/java/com/oj/gateway/
│       └── filter/                # JWT 认证过滤器
├── oj-api/                        # Feign 接口模块
│   └── src/main/java/com/oj/api/
│       ├── RemoteJudgeService.java
│       └── domain/                # 共享 DTO/VO
├── oj-common/                     # 公共模块
│   ├── oj-common-core/            # 核心工具、枚举、常量
│   ├── oj-common-security/        # 安全、Token、异常处理
│   ├── oj-common-redis/           # Redis 配置与服务
│   ├── oj-common-swagger/         # API 文档配置
│   ├── oj-common-mybatis/         # MyBatis-Plus 配置
│   ├── oj-common-elasticsearch/   # ES 依赖
│   ├── oj-common-message/         # 短信服务
│   ├── oj-common-file/            # 文件上传（OSS）
│   └── oj-common-rabbit/          # RabbitMQ 配置
├── oj-modules/                    # 业务微服务
│   ├── oj-system/                 # 管理端服务
│   ├── oj-friend/                 # 用户端服务
│   ├── oj-judge/                  # 判题服务
│   └── oj-job/                    # 定时任务服务
├── deploy/
│   └── int.sql                    # 数据库初始化脚本
└── pom.xml                        # 父 POM
```

---

## 注意事项

> **阿里云短信服务**：目前阿里云短信服务已不再向个人开发者提供免费服务，需要企业认证才能申请。如果仅用于本地开发和学习，可以跳过短信功能，使用管理员账号直接登录进行测试，或自行 mock 短信服务。
>
> **阿里云 OSS**：文件上传功能依赖阿里云 OSS，如无需文件上传功能可跳过，不影响核心判题功能。

---

## 快速开始

### 开发工具安装

> 以下工具按课件推荐安装，可根据个人习惯替换同类工具。

| 工具 | 用途 | 下载地址 |
|------|------|----------|
| **IDEA** | 后端 Java 开发 IDE | [jetbrains.com/idea](https://www.jetbrains.com/idea/) |
| **WebStorm** | 前端 Vue 开发 IDE | [jetbrains.com/webstorm](https://www.jetbrains.com/webstorm/) |
| **Apifox** | API 调试工具（替代 Postman + Swagger） | [apifox.com](https://apifox.com/) |
| **AnotherRedisDesktopManager** | Redis 可视化客户端 | [GitHub Releases](https://github.com/qishibo/AnotherRedisDesktopManager/releases) |
| **Navicat** | MySQL 可视化客户端 | [navicat.com](https://www.navicat.com/) |
| **Docker Desktop** | 容器运行环境 | [docker.com/products/docker-desktop](https://www.docker.com/products/docker-desktop/) |

#### 安装 IDEA

IDEA 作为主要的后端开发工具，能用正版就用正版。安装后需要配置 Maven：

1. 打开 IDEA → `File` → `Settings` → `Build, Execution, Deployment` → `Build Tools` → `Maven`
2. 设置 `Maven home path` 为本地 Maven 安装路径
3. 设置 `User settings file` 和 `Local repository` 路径

#### 安装 WebStorm

WebStorm 作为主要的前端开发工具，安装方式与 IDEA 类似，从 JetBrains 官网下载安装包，双击运行即可。

#### 安装 Apifox

Apifox 集成了 Postman + Swagger + Mock 功能，用于接口调试。安装后可以：
- 导入 Swagger/OpenAPI 接口文档
- 直接发送 HTTP 请求测试接口
- Mock 接口数据用于前端开发

#### 安装 AnotherRedisDesktopManager

下载安装包，双击运行即可。连接 Redis 后可以可视化查看和管理 Redis 中的键值对。

#### 安装 Navicat

下载安装包，双击运行。安装完成后新建连接，填入 MySQL 的主机地址、端口（默认 3306）、用户名和密码即可连接。

---

### 基础设施安装

项目依赖以下基础设施组件，课件推荐使用 **Docker** 方式安装（方便快捷，避免环境兼容问题）。

#### 1. Docker Desktop

Docker 是后续安装其他基础设施组件的前提。

1. 下载 [Docker Desktop for Windows](https://www.docker.com/products/docker-desktop/)
2. 双击安装包，按提示完成安装
3. 安装完成后重启电脑
4. 打开终端验证安装：
   ```bash
   docker --version
   docker run hello-world
   ```
5. 在 Docker Desktop 设置中确保已启用 WSL 2 后端（推荐）

#### 2. MySQL 8.0

```bash
# Docker 方式安装（课件推荐）
docker run -d \
  --name mysql \
  -p 3306:3306 \
  -e MYSQL_ROOT_PASSWORD=123456 \
  mysql:8.0

# 验证连接
docker exec -it mysql mysql -u root -p123456
```

安装完成后使用 Navicat 连接 MySQL，执行 `deploy/int.sql` 初始化数据库表。

#### 3. Redis

```bash
# Docker 方式安装（课件推荐）
docker run -d \
  --name redis \
  -p 6379:6379 \
  redis

# 验证
docker exec -it redis redis-cli ping
# 应返回 PONG
```

安装完成后使用 AnotherRedisDesktopManager 连接 Redis，主机 `127.0.0.1`，端口 `6379`。

#### 4. Nacos 2.x

Nacos 作为服务注册发现中心和配置中心。

```bash
# Docker 方式安装（课件推荐）
docker run -d \
  --name nacos \
  -p 8848:8848 \
  -p 9848:9848 \
  -p 9849:9849 \
  -e MODE=standalone \
  -e NACOS_AUTH_ENABLE=false \
  nacos/nacos-server:v2.2.3
```

启动后访问 Nacos 控制台：`http://localhost:8848/nacos`，默认账号密码 `nacos / nacos`。

在 Nacos 控制台中需要：
1. 创建项目使用的命名空间
2. 导入各服务的配置文件（数据库连接、Redis、JWT 密钥等）

#### 5. Elasticsearch 8.x

Elasticsearch 用于题目全文搜索。

```bash
# Docker 方式安装（课件推荐）
docker run -d \
  --name elasticsearch \
  -p 9200:9200 \
  -p 9300:9300 \
  -e "discovery.type=single-node" \
  -e "xpack.security.enabled=false" \
  elasticsearch:8.7.1

# 验证
curl http://localhost:9200
```

#### 6. RabbitMQ

RabbitMQ 用于异步判题消息队列。

```bash
# Docker 方式安装（课件推荐，含管理界面）
docker run -d \
  --name rabbitmq \
  -p 5672:5672 \
  -p 15672:15672 \
  rabbitmq:3-management

# 访问管理界面
# http://localhost:15672
# 默认账号密码: guest / guest
```

#### 7. Docker 拉取判题镜像

判题服务需要在 Docker 容器中执行用户代码：

```bash
docker pull openjdk:8-jdk-alpine
```

#### 8. Nginx

Nginx 用于前端静态资源托管和反向代理。

```bash
# Docker 方式安装
docker run -d \
  --name nginx \
  -p 80:80 \
  -p 443:443 \
  nginx

# 或直接下载安装
# https://nginx.org/en/download.html
```

#### 9. XXL-Job（可选）

定时任务服务依赖 XXL-Job 调度中心，如不需要定时任务功能可跳过。

```bash
# Docker 方式安装
docker run -d \
  --name xxl-job-admin \
  -p 8080:8080 \
  xuxueli/xxl-job-admin:2.4.0

# 访问调度中心
# http://localhost:8080/xxl-job-admin
# 默认账号密码: admin / 123456
```

---

### 一键启动基础设施（Docker Compose）

项目提供了 `docker-compose.yml` 文件，位于 `src/docker/` 目录，所有组件运行在 `online-judge` 网络下：

```bash
cd src/docker
docker-compose up -d
```

启动的服务包括：MySQL、Redis、Nacos、Elasticsearch、RabbitMQ、Nginx。

---

### 启动步骤

1. **启动基础设施**
   ```bash
   docker-compose up -d
   ```

2. **初始化数据库**

   使用 Navicat 连接 MySQL（`127.0.0.1:3306`，`root/123456`），执行 `deploy/int.sql` 创建数据库和表。

3. **配置 Nacos**

   访问 `http://localhost:8848/nacos`，创建命名空间并导入各服务的配置文件（数据库连接、Redis、RabbitMQ、JWT 密钥等）。

4. **启动后端服务**

   使用 IDEA 打开项目，依次启动以下服务：
   - `oj-gateway`（网关服务）
   - `oj-modules/oj-system`（管理端服务）
   - `oj-modules/oj-friend`（用户端服务）
   - `oj-modules/oj-judge`（判题服务）
   - `oj-modules/oj-job`（定时任务服务，可选）

5. **启动前端**

   使用 WebStorm 打开前端项目：
   ```bash
   npm install
   npm run dev
   ```

6. **验证服务**

   - Nacos 控制台：`http://localhost:8848/nacos`
   - RabbitMQ 管理界面：`http://localhost:15672`
   - Swagger 接口文档：`http://localhost:{port}/swagger-ui.html`
   - 前端页面：`http://localhost:5173`（Vite 默认端口）

### 配置说明

所有运行时配置（数据库连接、Redis、RabbitMQ、OSS 密钥、短信密钥、JWT 密钥等）均存储在 **Nacos 配置中心**，通过 `bootstrap.yml` 加载。

首次启动需在 Nacos 控制台创建对应命名空间，并导入各服务的配置文件。

---

## 接口文档

启动服务后访问 Swagger UI：

```
http://localhost:{port}/swagger-ui.html
```

---

## 部署架构

```
┌─────────────────────────────────────────────────┐
│                  Nginx (80)                      │
│         前端静态资源 + API 反向代理               │
└──────────┬──────────────────────────────────────┘
           │
           ▼
┌─────────────────────┐
│  Spring Cloud       │
│  Gateway            │
└─────────┬───────────┘
          │
    ┌─────┼─────┬─────────┐
    ▼     ▼     ▼         ▼
  system friend judge    job
    │     │     │         │
    └─────┴─────┴─────────┘
              │
    ┌─────────┼─────────┬──────────┬──────────┐
    ▼         ▼         ▼          ▼          ▼
  MySQL    Redis    RabbitMQ   ES        Nacos
```

支持单机部署（所有服务部署在同一台 ECS）或多机扩展。
