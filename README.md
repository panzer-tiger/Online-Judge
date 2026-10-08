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

| 功能      | 说明                               |
| ------- | -------------------------------- |
| 短信注册/登录 | 通过阿里云短信服务发送验证码，JWT Token 认证      |
| 个人信息管理  | 修改个人资料、上传头像（阿里云 OSS）             |
| 题目浏览    | 分页题目列表、题目详情、上一题/下一题导航            |
| 题目全文搜索  | 基于 Elasticsearch 的全文检索           |
| 在线编程练习  | 浏览器内编写代码并提交                      |
| 代码编译运行  | 同步（OpenFeign）和异步（RabbitMQ）两种判题模式 |
| 实时判题    | Docker 沙箱执行，支持时间/内存/CPU 限制       |
| 竞赛列表    | 浏览未结束和历史竞赛                       |
| 竞赛报名/参赛 | 报名竞赛、竞赛内答题、查看排行榜                 |
| 系统消息    | 接收竞赛结果、排名通知等系统消息                 |
| 文件上传    | 支持文件上传，Redis 限制每日上传次数            |

### 管理端功能

| 功能    | 说明                     |
| ----- | ---------------------- |
| 管理员登录 | 用户名/密码登录（BCrypt 加密）    |
| 题目管理  | 题目 CRUD、测试用例管理、ES 索引同步 |
| 竞赛管理  | 竞赛 CRUD、发布/取消、题目分配     |
| 用户管理  | 用户列表、启用/禁用用户状态         |
| 管理员管理 | 新增管理员账号                |
| 定时任务  | 竞赛列表缓存刷新、赛后成绩计算与排名     |

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

| 模块             | 说明           | 职责                                        |
| -------------- | ------------ | ----------------------------------------- |
| **oj-gateway** | API 网关       | 请求路由、JWT 认证、权限校验                          |
| **oj-api**     | Feign 接口模块   | 服务间调用接口定义、共享 DTO/VO                       |
| **oj-common**  | 公共模块（9 个子模块） | 核心工具、安全、Redis、MyBatis、ES、短信、文件、MQ、Swagger |
| **oj-system**  | 管理端服务        | 题目管理、竞赛管理、用户管理                            |
| **oj-friend**  | 用户端服务        | 注册登录、题目浏览、代码提交、竞赛参与、消息系统                  |
| **oj-judge**   | 判题服务         | Docker 沙箱代码执行、编译检查、结果判定                   |
| **oj-job**     | 定时任务服务       | XXL-Job 调度、竞赛缓存刷新、成绩计算                    |

### oj-common 子模块

| 子模块                     | 说明                       |
| ----------------------- | ------------------------ |
| oj-common-core          | 核心工具类、JWT、枚举、常量、基础实体     |
| oj-common-security      | Token 拦截器、全局异常处理         |
| oj-common-redis         | Redis 配置与服务封装            |
| oj-common-swagger       | SpringDoc OpenAPI 3 接口文档 |
| oj-common-mybatis       | MyBatis-Plus 自动填充配置      |
| oj-common-elasticsearch | Elasticsearch 依赖         |
| oj-common-message       | 阿里云短信服务                  |
| oj-common-file          | 阿里云 OSS 文件上传             |
| oj-common-rabbit        | RabbitMQ 配置              |

---

## 技术栈

### 后端技术

| 技术                            | 版本             | 说明                  |
| ----------------------------- | -------------- | ------------------- |
| **Java**                      | 17             | 开发语言                |
| **Spring Boot**               | 3.0.1          | 应用框架                |
| **Spring Cloud**              | 2022.0.0       | 微服务框架               |
| **Spring Cloud Alibaba**      | 2022.0.0.0-RC2 | 阿里巴巴微服务组件           |
| **Spring Cloud Gateway**      | -              | API 网关（WebFlux 响应式） |
| **Spring Cloud OpenFeign**    | -              | 声明式服务调用             |
| **Spring Cloud LoadBalancer** | -              | 客户端负载均衡             |
| **Nacos**                     | -              | 服务注册发现 + 配置中心       |
| **MyBatis-Plus**              | 3.5.5          | ORM 框架              |
| **MySQL**                     | -              | 关系型数据库              |
| **Redis**                     | -              | 缓存、Token 存储         |
| **RabbitMQ**                  | -              | 消息队列（异步判题）          |
| **Elasticsearch**             | -              | 全文搜索引擎              |
| **XXL-Job**                   | 2.4.0          | 分布式任务调度             |
| **Docker Java**               | 3.3.4          | Docker 容器管理（沙箱判题）   |
| **SpringDoc OpenAPI**         | 2.2.0          | API 接口文档            |
| **Fastjson2**                 | 2.0.43         | JSON 序列化            |
| **JJWT**                      | 0.9.1          | JWT Token 生成与解析     |
| **Hutool**                    | 5.8.22         | Java 工具库            |
| **PageHelper**                | 2.0.0          | MyBatis 分页插件        |
| **阿里云 OSS SDK**               | 3.17.4         | 对象存储                |
| **阿里云短信 SDK**                 | 2.0.0 / 3.0.0  | 短信验证码服务             |
| **TransmittableThreadLocal**  | 2.14.4         | 跨线程池上下文传递           |

### 前端技术

| 技术                 | 说明       |
| ------------------ | -------- |
| **Vue 3**          | 前端框架     |
| **Vite**           | 构建工具     |
| **TypeScript**     | 类型安全     |
| **Axios**          | HTTP 请求库 |
| **Ant Design Vue** | UI 组件库   |
| **Pinia**          | 状态管理     |
| **ESLint**         | 代码规范     |

### 基础设施

| 组件          | 说明                           |
| ----------- | ---------------------------- |
| **Nginx**   | 反向代理、前端静态资源托管、负载均衡           |
| **Docker**  | 判题沙箱环境（openjdk:8-jdk-alpine） |
| **阿里云 ECS** | 云服务器部署                       |
| **阿里云 OSS** | 文件/图片云存储                     |
| **阿里云 SMS** | 短信验证码服务                      |

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

| 措施     | 说明                           |
| ------ | ---------------------------- |
| 网络隔离   | `networkMode: none`，禁止容器网络访问 |
| 只读文件系统 | `readonlyRootfs: true`       |
| 内存限制   | 默认 100MB                     |
| 内存交换限制 | 禁止使用 swap                    |
| CPU 限制 | 限制 CPU 使用率                   |
| 超时控制   | 默认 5 秒执行超时                   |

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

| URL 前缀       | 角色   | 说明        |
| ------------ | ---- | --------- |
| `/system/**` | 管理员  | 管理端接口     |
| `/friend/**` | 普通用户 | 用户端接口     |
| 白名单路径        | 无需认证 | 登录、注册、文档等 |

---

## 数据库设计

所有表统一存放在 `online_judge` 数据库中，共 29 张表（业务 9 张 + Nacos 12 张 + XXL-Job 8 张）。

### 项目业务表

| 表名               | 说明     | 关键字段                                     |
| ---------------- | ------ | ---------------------------------------- |
| tb_sys_user      | 系统管理员  | userAccount, nickName, password          |
| tb_user          | 普通用户   | nickName, phone, status                  |
| tb_question      | 题目     | title, difficulty, content, questionCase |
| tb_exam          | 竞赛     | title, startTime, endTime, status        |
| tb_exam_question | 竞赛题目关联 | questionId, examId, questionOrder        |
| tb_user_exam     | 竞赛报名   | userId, examId, score, examRank          |
| tb_user_submit   | 用户提交记录 | userId, questionId, userCode, pass       |
| tb_message_text  | 消息内容   | messageTitle, messageContent             |
| tb_message       | 消息     | textId, sendId, recId                    |

### Nacos 表（v2.2.3，表名由 Nacos 源码硬编码，不可修改）

config_info、config_info_aggr、config_info_beta、config_info_tag、config_tags_relation、his_config_info、group_capacity、tenant_capacity、tenant_info、users、roles、permissions

### XXL-Job 表（v2.4.0，可选）

xxl_job_info、xxl_job_log、xxl_job_log_report、xxl_job_logglue、xxl_job_registry、xxl_job_group、xxl_job_user、xxl_job_lock

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
├── docker/
│   └── docker-compose.yml         # 中间件一键启动（MySQL/Redis/Nacos/ES/RabbitMQ/Nginx）
├── deploy/
│   ├── init.sql                   # 业务表 9 张
│   ├── nacos.sql                  # Nacos v2.2.3 表 12 张
│   └── xxl-job.sql                # XXL-Job v2.4.0 表 8 张
└── pom.xml                        # 父 POM
```

---

## 注意事项

> **阿里云短信服务**：目前阿里云短信服务已不再向个人开发者提供免费服务，需要企业认证才能申请。如果仅用于本地开发和学习，可以跳过短信功能，使用管理员账号直接登录进行测试，或自行 mock 短信服务。
>
> **阿里云 OSS**：文件上传功能依赖阿里云 OSS，如无需文件上传功能可跳过，不影响核心判题功能。

---

## 快速开始

### 一、运行所需组件清单

下表和命令以本项目代码（`docker/docker-compose.yml`、各模块 `pom.xml`、`bootstrap.yml`）为准。

| 类别   | 组件                                 | 版本                                    | 端口                 | 必需性         |
| ---- | ---------------------------------- | ------------------------------------- | ------------------ | ----------- |
| 基础环境 | **JDK**                            | 17（`maven.compiler.source/target=17`） | -                  | 必需          |
| 基础环境 | **Maven**                          | 3.6+                                  | -                  | 必需          |
| 基础环境 | **Node.js / npm**                  | 18+                                   | -                  | 前端必需        |
| 容器   | **Docker Desktop**                 | 最新版（建议启用 WSL 2）                       | -                  | 必需          |
| 中间件  | **MySQL**                          | 8.0                                   | 3306               | 必需          |
| 中间件  | **Redis**                          | 最新版                                   | 6379               | 必需          |
| 中间件  | **Nacos**                          | v2.2.3                                | 8848 / 9848 / 9849 | 必需          |
| 中间件  | **Elasticsearch**                  | 8.7.1                                 | 9200 / 9300        | 全文搜索必需      |
| 中间件  | **RabbitMQ**                       | 3-management                          | 5672 / 15672       | 异步判题必需      |
| 中间件  | **XXL-Job Admin**                  | 2.4.0                                 | 8080               | 定时任务必需      |
| 中间件  | **Nginx**                          | 1.21                                  | 80 / 443           | 部署时必需       |
| 判题沙箱 | **Docker 镜像 openjdk:8-jdk-alpine** | -                                     | -                  | 判题必需        |
| 云服务  | 阿里云 OSS / 短信 SMS                   | -                                     | -                  | 可选（见「注意事项」） |


> ⚠️ **Elasticsearch 分词器**：代码中 `QuestionES` 使用 `ik_max_word` 分词器，必须为 ES 安装**同版本**的 IK 分词器（本项目 ES 8.7.1 对应 IK 8.7.1）。若版本不一致，全文搜索会报错。

---

### 二、基础环境安装

#### 1. JDK 17

后端使用 Java 17 编译与运行，必须安装 JDK 17（不要用 8 或 11）。

- 下载地址：<https://adoptium.net/> 或 <https://www.oracle.com/java/technologies/downloads/#java17>
- 安装后配置 `JAVA_HOME` 环境变量，并将 `%JAVA_HOME%\bin` 加入 `Path`
- 验证：

```bash
java -version     # 应显示 17.x
javac -version
```

#### 2. Maven

- 下载地址：<https://maven.apache.org/download.cgi>（解压即用）
- 建议在 `conf/settings.xml` 中配置阿里云镜像加速依赖下载，并设置本地仓库路径：

```xml
<mirror>
  <id>aliyunmaven</id>
  <mirrorOf>*</mirrorOf>
  <url>http://maven.aliyun.com/repository/public/</url>
</mirror>
```

- 验证：

```bash
mvn -v
```

#### 3. Node.js

前端（Vue 3 + Vite）依赖 Node.js。

- 下载地址：<https://nodejs.org/en/>（下载 LTS 长期支持版）
- 验证：

```bash
node -v
npm -v
```

### 三、中间件安装

#### 方式 A：Docker Compose 一键安装（推荐）

项目已提供 `docker/docker-compose.yml`，所有组件运行在 `online-judge` 网络下：

```bash
cd docker
docker-compose up -d
```

启动的组件：MySQL、Redis、Nacos、Elasticsearch、RabbitMQ、Nginx。

> 说明：compose 中的 Nacos 未配置 `MYSQL_SERVICE_*` 环境变量，使用**内嵌 Derby** 存储，因此无需执行 `deploy/nacos.sql` 即可启动，数据保存在 `nacos-data` 卷中；XXL-Job 未包含在 compose 内，需要单独启动（见方式 B 第 6 步，且**必须先建表**）。

先用 Docker Desktop 验证环境：

```bash
docker --version
docker run hello-world
```

#### 方式 B：逐个安装

```bash
# 0) 创建自定义网络（容器之间可直接用容器名互访）
docker network create online-judge

# 1) MySQL 8.0
docker run -d --name oj-mysql --network online-judge -p 3306:3306 \
  -e "TZ=Asia/Shanghai" -e "MYSQL_ROOT_PASSWORD=123456" mysql:8.0

# 2) Redis
docker run -d --name oj-redis --network online-judge -p 6379:6379 redis
# 验证：docker exec -it oj-redis redis-cli ping  → 返回 PONG

# 3) Nacos v2.2.3（单机模式，关闭鉴权；默认内嵌 Derby，无需建表）
docker run -d --name oj-nacos --network online-judge \
  -p 8848:8848 -p 9848:9848 -p 9849:9849 \
  -e MODE=standalone -e NACOS_AUTH_ENABLE=false nacos/nacos-server:v2.2.3

# 4) Elasticsearch 8.7.1
docker run -d --name oj-es --network online-judge -p 9200:9200 -p 9300:9300 \
  -e "discovery.type=single-node" -e "xpack.security.enabled=false" \
  -e "ES_JAVA_OPTS=-Xms256m -Xmx256m" elasticsearch:8.7.1
# 验证：curl http://localhost:9200

# 5) RabbitMQ（含管理界面）
docker run -d --name oj-rabbit --network online-judge \
  -p 5672:5672 -p 15672:15672 rabbitmq:3-management
# 管理界面 http://localhost:15672  默认 guest / guest

# 6) XXL-Job 调度中心
#    前置条件：必须先执行 deploy/xxl-job.sql 建表，否则启动即报数据源错误
docker run -d --name oj-xxl-job --network online-judge -p 8080:8080 \
  -e PARAMS="--spring.datasource.url=jdbc:mysql://oj-mysql:3306/online_judge?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&serverTimezone=Asia/Shanghai --spring.datasource.username=root --spring.datasource.password=123456" \
  xuxueli/xxl-job-admin:2.4.0
# 管理界面 http://localhost:8080/xxl-job-admin  默认 admin / 123456
# 注意：xxl_job_* 表在本项目的 online_judge 库中（不是单独的 xxl_job 库）

# 7) Nginx（部署时）
docker run --name oj-nginx -d --network online-judge -p 80:80 nginx:1.21
```

#### XXL-Job 与 Nacos 的数据库初始化

| 组件          | 是否必须建表 | 说明                                                                                                                         |
| ----------- | ------ | -------------------------------------------------------------------------------------------------------------------------- |
| **XXL-Job** | **必须** | xxl-job-admin 没有内嵌数据库，只能连 MySQL，启动前必须先建表。`deploy/xxl-job.sql` 会创建 8 张 `xxl_job_*` 表并写入初始数据（admin/123456 账号、示例执行器、示例任务、调度锁） |
| **Nacos**   | 默认不需要  | 单机模式自带内嵌 Derby，数据随容器/数据卷保存。**只有外接 MySQL 时**才需要先执行 `deploy/nacos.sql`，并在启动时补充数据源配置：                                         |

```bash
# 可选：Nacos 外接 MySQL（需先执行 deploy/nacos.sql 建表）
docker run -d --name oj-nacos --network online-judge \
  -p 8848:8848 -p 9848:9848 -p 9849:9849 \
  -e MODE=standalone -e JVM_XMS=256m -e JVM_XMX=256m \
  -e SPRING_DATASOURCE_PLATFORM=mysql \
  -e MYSQL_SERVICE_HOST=oj-mysql -e MYSQL_SERVICE_PORT=3306 \
  -e MYSQL_SERVICE_DB_NAME=online_judge \
  -e MYSQL_SERVICE_USER=root -e MYSQL_SERVICE_PASSWORD=123456 \
  nacos/nacos-server:v2.2.3
```

**正确顺序**：启动 MySQL → 执行 `deploy/xxl-job.sql`（外接 MySQL 时再加 `deploy/nacos.sql`）→ 启动 XXL-Job / Nacos（外接模式）。

#### 安装 IK 分词器（全文搜索必需）

下载与本项目 ES 版本（8.7.1）一致的 IK 分词器，解压后放入 ES 容器 `/usr/share/elasticsearch/plugins` 目录（或挂载目录），确认安装：

```bash
docker exec -it oj-es bin/elasticsearch-plugin list
```

#### 拉取判题沙箱镜像（必需）

判题服务（`oj-judge`）会在 Docker 容器内编译运行用户代码，镜像常量见 `JudgeConstants.JAVA_ENV_IMAGE`：

```bash
docker pull openjdk:8-jdk-alpine
```

> 若判题服务也运行在容器中并通过 IP 调用 Docker API，需要在宿主机开启 Docker 远程访问（`dockerd -H tcp://0.0.0.0:2375`）。

---

### 四、初始化数据库

业务表、Nacos 表、XXL-Job 表统一存放在 `online_judge` 数据库中，共 **29 张表**（业务 9 + Nacos 12 + XXL-Job 8）。

> **执行时机**：`init.sql`、`xxl-job.sql` 必须在对应服务启动**之前**执行；`nacos.sql` 仅在 Nacos 外接 MySQL 时才需要（默认内嵌 Derby 不需要）。

依次执行 `deploy/` 下的三个脚本：

```bash
mysql -u root -p123456 < deploy/init.sql      # 业务表 9 张
mysql -u root -p123456 < deploy/nacos.sql     # Nacos v2.2.3 表 12 张
mysql -u root -p123456 < deploy/xxl-job.sql   # XXL-Job v2.4.0 表 8 张（可选）
```

| 脚本                   | 表分类                                              | 数量 |
| -------------------- | ------------------------------------------------ | -- |
| `deploy/init.sql`    | 项目业务表（tb_sys_user、tb_user、tb_question、tb_exam 等） | 9  |
| `deploy/nacos.sql`   | Nacos 配置/用户/角色表（config_info、users、roles 等）       | 12 |
| `deploy/xxl-job.sql` | XXL-Job 调度表（xxl_job_info、xxl_job_log 等）          | 8  |

---

### 五、配置 Nacos

1. 访问 `http://localhost:8848/nacos`，默认账号密码 `nacos / nacos`。
2. 各服务的 `bootstrap.yml` 已指定命名空间 ID 为 `6229ec57-34ae-49d1-b268-4c97d9d1f1e9`：
   - 在 Nacos 控制台 **命名空间** 中新建同名（或同 ID）空间；或修改各 `bootstrap.yml` 的 `namespace` 为自己的空间 ID。
3. 在对应命名空间下，为每个服务创建配置文件（`dataId` = 服务名 + `.yaml`，分组默认 `DEFAULT_GROUP`）：

   `oj-gateway.yaml`、`oj-system.yaml`、`oj-friend.yaml`、`oj-judge.yaml`、`oj-job.yaml`
4. 配置内容包含：`server.port`、数据源、Redis、RabbitMQ、Elasticsearch、JWT 密钥、OSS/短信密钥等。

服务端口与启动类对应关系：`oj-gateway`（19090）、`oj-system`（9201）、`oj-friend`（9202）、`oj-job`（9203）、`oj-judge`。

---

### 六、启动后端服务

方式一（本地 IDE 启动）：打开项目，依次启动

- `oj-gateway`（网关）
- `oj-modules/oj-system`（管理端）
- `oj-modules/oj-friend`（用户端）
- `oj-modules/oj-judge`（判题，需 Docker）
- `oj-modules/oj-job`（定时任务，可选）

方式二（命令行打包运行）：

```bash
mvn clean package -DskipTests
```

> 注意：当前 `oj-gateway/pom.xml` 与 `oj-modules/pom.xml` 尚未声明 `spring-boot-maven-plugin`。若要打成可直接 `java -jar` 运行的可执行 JAR，需在网关与 `oj-modules` 的 `pom.xml` 的 `<build><plugins>` 中加入：

```xml
<plugin>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-maven-plugin</artifactId>
</plugin>
```

---

### 七、启动前端

在 B 端 / C 端前端工程目录下（Vue 3 + Vite）：

```bash
npm install
npm run dev
```

默认访问地址 `http://localhost:5173`；开发环境通过 Vite 代理将 `/dev-api` 转发到网关 `http://127.0.0.1:19090`。

---

### 八、验证服务

| 服务            | 地址                                        | 账号             |
| ------------- | ----------------------------------------- | -------------- |
| Nacos 控制台     | `http://localhost:8848/nacos`             | nacos / nacos  |
| RabbitMQ 管理界面 | `http://localhost:15672`                  | guest / guest  |
| XXL-Job 调度中心  | `http://localhost:8080/xxl-job-admin`     | admin / 123456 |
| Elasticsearch | `http://localhost:9200`                   | -              |
| Swagger 接口文档  | `http://localhost:{port}/swagger-ui.html` | -              |
| 前端页面          | `http://localhost:5173`                   | -              |

### 九、配置说明

所有运行时配置（数据库连接、Redis、RabbitMQ、OSS 密钥、短信密钥、JWT 密钥等）均存储在 **Nacos 配置中心**，通过各服务的 `bootstrap.yml` 加载。

首次启动需在 Nacos 控制台创建对应命名空间并导入各服务的配置文件。

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
