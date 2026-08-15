# 雄安碳通量大数据管理平台 · dataengine v2

基于 Spring Cloud 微服务架构的碳通量大数据管理平台后端，面向雄安新区碳通量监测数据提供接入、存储、管理、空间化发布与可视化支撑。

![JDK](https://img.shields.io/badge/JDK-21-orange) ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.3-brightgreen) ![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2025.0.0-blue) ![License](https://img.shields.io/badge/License-MIT--0-lightgrey)

## 功能特性

- **大文件分片上传**：分片 + MD5 校验 + 断点续传 + WebSocket 实时进度 + 服务端合并
- **双存储引擎**：策略模式抽象，HDFS 与 MinIO 可配置切换，支撑时空大数据分级存储
- **并发控制组件**：注解式分布式锁（AOP + Redisson）与滑动窗口限流器
- **空间数据发布**：GeoServer REST 集成，WMS / WFS 地图服务自动化发布
- **统一认证鉴权**：sa-token + Redisson 分布式会话，网关统一鉴权
- **全链路可观测**：SkyWalking 链路追踪 + Prometheus 指标监控
- **微服务治理**：Nacos 注册发现 + Dubbo RPC + Sentinel 网关限流

## 项目背景

雄安新区碳通量监测涉及多个监测站点、多类传感器与长时间序列数据，存在数据来源分散、体量大（时空大数据）、发布链路复杂等问题。本平台构建统一的数据管理后端，打通「数据源接入 → 数据上传存储 → 数据管理 → 空间化发布 → 上层可视化」全链路，为《雄安建筑碳排放可视化平台》等应用提供数据底座。

## 系统架构

```
                        ┌─────────────────────────────────────────┐
                        │              Spring Cloud Gateway        │
                        │        鉴权（sa-token）· 限流（Sentinel）  │
                        └──────────────────┬──────────────────────┘
                                           │
        ┌──────────────┬──────────────┬────┴─────┬──────────────┬─────────────┐
        │              │              │          │              │             │
   ┌────▼─────┐  ┌─────▼────┐  ┌──────▼───┐  ┌───▼──────┐  ┌────▼────┐  ┌─────▼─────┐
   │   auth   │  │  admin   │  │ data-    │  │ data-    │  │ geo-    │  │ personal  │
   │ 认证服务  │  │ 管理后台  │  │ manager  │  │ source   │  │ service │  │ 个人中心   │
   └────┬─────┘  └─────┬────┘  │ 数据管理  │  │ 数据源接入│  │ 地理服务 │  └─────┬─────┘
        │              │      └──────┬───┘  └───┬──────┘  └────┬────┘        │
        └──────────────┴─────────────┴──────────┴──────────────┴─────────────┘
                                  Dubbo RPC（Nacos 注册发现）

        ┌──────────┐  ┌──────────┐  ┌──────────────┐  ┌───────────┐  ┌───────────┐
        │PostgreSQL│  │  Redis   │  │ MinIO / HDFS │  │ GeoServer │  │   Nacos   │
        │ 业务数据  │  │ 缓存/锁  │  │ 文件分级存储  │  │ 空间数据发布│  │ 注册/配置  │
        └──────────┘  └──────────┘  └──────────────┘  └───────────┘  └───────────┘
```

### 服务职责

| 服务 | 职责 |
|------|------|
| gateway | 统一入口：路由转发、登录校验、权限校验、Sentinel 限流 |
| auth | 认证服务：注册、登录、登出、账号激活、Token 签发 |
| admin | 管理后台：用户管理（冻结/解冻/启用/禁用）、角色管理 |
| data-manager | 数据管理：数据 CRUD、大文件分片上传/合并/下载 |
| data-source | 数据源接入：监测数据源注册、管理与状态跟踪 |
| geoservice | 地理服务：空间数据管理与 GeoServer 服务发布 |
| personal | 个人中心：昵称、密码、头像修改 |

## 技术栈

| 分类 | 技术 |
|------|------|
| 基础框架 | Spring Boot 3.5.3 · JDK 21 |
| 微服务 | Spring Cloud 2025.0.0 · Spring Cloud Alibaba 2023.0.3.3 |
| 服务通信 | Dubbo RPC + Facade 模式 |
| 认证鉴权 | sa-token（Redisson 分布式会话） |
| 网关限流 | Sentinel + Nacos 数据源 |
| 缓存 | Redis + Redisson + Caffeine 多级缓存 |
| 数据库 | PostgreSQL · MyBatis-Plus · Druid 连接池 |
| 文件存储 | MinIO（对象存储）· HDFS（Hadoop Client） |
| 空间数据 | GeoServer（WMS / WFS 发布） |
| 可观测性 | SkyWalking 链路追踪 · Prometheus + Actuator 监控 |
| 工程化 | Gradle 多模块并行构建 · MapStruct · Lombok · SSH 自动部署 |

## 模块结构

```text
dataenginev2/
├── gateway/                  # 网关：路由、鉴权、限流
├── auth/                     # 认证服务：登录注册、Token 管理
├── app/
│   ├── admin/                # 管理后台：用户与角色管理
│   ├── data/
│   │   ├── data-manager/     # 数据管理：数据 CRUD、文件上传与下载
│   │   └── data-source/      # 数据源接入：监测数据源注册与管理
│   ├── geoservice/           # 地理服务：空间数据发布与管理
│   └── personal/             # 个人中心
└── common/                   # 公共组件（15 个模块）
    ├── api/                  # 服务间接口定义（Facade API）
    ├── base/                 # 基础响应、异常、工具类
    ├── cache/                # Redis + Redisson + Caffeine 缓存封装
    ├── config/               # 公共配置
    ├── data-source-config/   # MyBatis-Plus + PostgreSQL + Druid
    ├── file/                 # 文件存储双策略（HDFS / MinIO）
    ├── geoserver/            # GeoServer REST 客户端
    ├── limiter/              # 限流组件
    ├── lock/                 # 注解式分布式锁
    ├── prometheus/           # 监控指标暴露
    ├── rpc/                  # Dubbo RPC 配置与 Facade 切面
    ├── sa-token/             # 认证组件封装
    ├── skywalking/           # 链路追踪接入
    └── web/                  # Web 通用组件（Token 过滤器、全局异常处理）
```

## 核心功能

### 1. 大文件分片上传

针对碳通量时空数据文件体积大的特点，实现可靠的大文件上传链路：

```
客户端                          data-manager 服务                     存储后端
  │  POST /init（文件名、大小、数据源）  │                               │
  │ ────────────────────────────────▶ │ 创建上传任务                    │
  │  POST /chunk（分片 + MD5）         │                               │
  │ ────────────────────────────────▶ │ 校验 MD5 → 落盘 → 记录分片状态  │
  │   WebSocket 推送进度               │ ──▶ MinIO / HDFS / 本地        │
  │ ◀──────────────────────────────── │                               │
  │  POST /merge（全部完成）           │                               │
  │ ────────────────────────────────▶ │ 服务端合并 → 元数据入库         │
```

- **分片上传**：客户端按配置切片上传，服务端逐片落盘并记录分片状态
- **完整性校验**：每片携带 MD5 哈希，服务端校验通过后写入，防止传输损坏
- **断点续传**：中断后通过 `GET /status` 查询已完成分片列表，跳过已传分片继续上传
- **实时进度**：通过 WebSocket 向前端推送上传进度
- **并发控制**：基于分布式锁防止同一文件分片的并发写入冲突
- **服务端合并**：全部分片完成后由服务端合并为完整文件

### 2. 文件存储双策略

基于策略模式设计文件存储抽象，支持多后端可配置切换：

- `FileStrategy` 接口统一抽象上传、下载、删除、合并等操作
- `HDFSFileStrategy`：面向碳通量时空大数据的分布式文件存储
- `MinioFileStrategy`：面向小文件与通用对象存储场景
- `FileStrategyFactory`：按配置动态选择存储后端，业务层无感知

### 3. 分布式锁与限流

- **注解式分布式锁**：自定义 `@DistributeLock` 注解 + AOP 切面，基于 Redisson 实现，支持 SpEL 表达式动态指定锁 key（如 `#request.fileId + ':' + #request.chunkIndex`），用于分片上传、数据操作等并发场景
- **限流组件**：`RateLimiter` 接口抽象，基于 Redisson `RRateLimiter` 实现；`tryAcquire` 单次获取时为滑动窗口语义，批量获取时为漏桶语义，应用于高频查询接口防护

### 4. 空间数据服务集成

封装 GeoServer REST API 客户端，支持 WorkSpace、DataStore、FeatureType、CoverageStore 的全生命周期管理，实现碳通量监测数据的 WMS / WFS 地图服务自动化发布。

### 5. 认证与权限

- 基于 sa-token 实现登录认证与 Token 管理，会话存储于 Redis（Redisson）
- 网关统一校验 Token 与角色权限，业务服务通过 `StpUtil` 获取登录态
- 支持账号注册、激活、冻结/解冻、启用/禁用等完整账号生命周期

### 6. 可观测性

- **SkyWalking**：全链路调用追踪，覆盖网关与各业务服务
- **Prometheus + Actuator**：JVM 与服务指标暴露，支持 Grafana 看板
- **统一异常处理**：全局异常拦截与标准化错误响应

## API 接口总览

| 服务 | 接口前缀 | 主要端点 |
|------|---------|---------|
| auth | `/api/auth` | `POST /register`、`POST /login`、`POST /logout`、`GET /active` |
| auth | `/api/token` | Token 校验与管理 |
| admin | `/api/userManage` | `GET /query`、`POST /create`、`PUT /modify`、`POST /freeze`、`POST /unfreeze`、`POST /enable`、`POST /disable`、`DELETE /delete`、`POST /batchDelete` |
| admin | `/api/role` | `GET /queryAll`、`GET /allRole` |
| personal | `/api/user` | `GET /getUserInfo`、`PUT /modifyNickName`、`PUT /modifyPassword`、`PUT /modifyProfilePhoto` |
| data-source | `/api/data/source` | `GET /query`、`POST /add`、`PUT /modify`、`DELETE /delete` |
| data-manager | `/api/data/upload` | `POST /init`、`POST /chunk`、`POST /merge`、`GET /status`、`POST /cancel`、`GET /download` |
| data-manager | `/api/data/interaction` | `GET /query`、`DELETE /delete` |
| geoservice | `/api/service` | `POST /publish`、`GET /query`、`GET /query/{serviceMd5}`、`POST /add`、`PUT /modify`、`DELETE /delete` |

## 数据库设计

核心表结构（PostgreSQL）：

| 表 | 关键字段 | 说明 |
|----|---------|------|
| 用户表 | ID、NICK_NAME、PASSWORD_HASH、TELEPHONE、STATE、ROLE_ID、LAST_LOGIN_TIME | 账号与角色关联，含乐观锁 LOCK_VERSION |
| 角色表 | ID、ROLE_NAME | 权限角色定义 |
| 上传任务表 | FILE_ID、USER_ID、DATA_SOURCE_ID、CHUNK_SIZE、TOTAL_CHUNKS、SAVE_SOFT、STATUS | 一次大文件上传的元数据 |
| 分片表 | FILE_ID、CHUNK_INDEX、CHUNK_SIZE、MD5、STATUS、RETRY_COUNT | 每片的状态与校验值，支撑断点续传 |
| 数据表 | FILE_ID、DATA_SOURCE_ID、NAME、TYPE、DESCRIPTION | 数据资产元数据 |
| 数据源表 | 名称、类型、状态 | 监测数据源注册信息 |
| 地理服务表 | 服务名、MD5、发布状态 | 已发布的 GeoServer 服务 |

所有业务表均含 `GMT_CREATE` / `GMT_MODIFIED` / `DELETED`（逻辑删除）/ `LOCK_VERSION`（乐观锁）审计字段。

## 测试

- `SlidingWindowRateLimiterTest`：限流器单元测试（漏桶 / 滑动窗口两种语义）
- `GeoServerHttpUtilsTest`：GeoServer REST 客户端 HTTP 工具测试

## 快速开始

### 环境要求

| 依赖 | 版本要求 |
|------|---------|
| JDK | 21+ |
| Gradle | 8.x |
| PostgreSQL | 14+（含 PostGIS 扩展） |
| Redis | 6+ |
| Nacos | 2.x（注册中心 + 配置中心） |
| MinIO / HDFS | 按需部署 |
| GeoServer | 2.24+（可选） |

### 环境变量

数据库、缓存、对象存储等连接信息均通过环境变量注入（避免明文写入配置文件），主要变量包括：`DATAENGINE_DB_PASSWORD`、`DATAENGINE_REDIS_PASSWORD`、`DATAENGINE_MINIO_PASSWORD`、`DATAENGINE_GEOSERVER_PASSWORD`、`DATAENGINE_NACOS_URL` 等，完整清单见 `common/base/src/main/resources/base.yml`。

### 构建

```bash
# 构建全部模块（并行）
gradle buildModulesParallel

# 或构建单个模块，如数据管理服务
gradle :app:data:data-manager:build
```

### 运行

各模块打包产物位于对应模块的 `build/libs/` 目录：

```bash
java -jar gateway/build/libs/*.jar
java -jar auth/build/libs/*.jar
java -jar app/admin/build/libs/*.jar
java -jar app/data/data-manager/build/libs/*.jar
java -jar app/data/data-source/build/libs/*.jar
java -jar app/geoservice/build/libs/*.jar
java -jar app/personal/build/libs/*.jar
```

启动顺序建议：先启动 Nacos、PostgreSQL、Redis 等基础依赖，再启动认证与网关，最后启动各业务服务。

## 部署

- 支持 Gradle SSH 插件自动化部署：`gradle buildModulesParallel` 构建完成后自动将各模块 jar 上传至目标服务器（部署目标通过 `DATAENGINE_DEPLOY_HOST`、`DATAENGINE_DEPLOY_KEY_PATH` 环境变量配置）
- 生产环境部署于 Kubernetes 集群（worker 节点运行服务实例）

## 项目状态

- 当前版本：`0.0.2`
- 处于持续迭代中，详见 `CHANGELOG`
