# 雄安碳通量大数据管理平台 · dataengine v2

基于 Spring Cloud 微服务架构的碳通量大数据管理平台后端，面向雄安新区碳通量监测数据提供接入、存储、管理、空间化发布与可视化支撑。

## 项目背景

雄安新区碳通量监测涉及多个监测站点、多类传感器与长时间序列数据，数据来源分散、体量大（时空大数据）、发布链路复杂。本平台旨在构建统一的数据管理后端，支撑碳通量数据的接入、存储、管理与地图服务发布，为《雄安建筑碳排放可视化平台》等上层应用提供数据底座。

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

微服务共 7 个服务模块 + 15 个公共模块，服务间通过 Dubbo RPC 通信，Nacos 负责注册发现与配置管理，统一经网关对外暴露。

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
    ├── config/               # 公共配置（Nacos / 服务配置）
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

- **分片上传**：客户端按配置大小切片上传，服务端逐片落盘并记录分片状态
- **完整性校验**：每片携带 MD5 哈希，服务端校验通过后写入，防止传输损坏
- **断点续传**：上传中断后可通过状态接口查询已完成分片列表，跳过已传分片继续上传
- **实时进度**：通过 WebSocket 向前端推送上传进度
- **并发控制**：基于分布式锁防止同一文件分片的并发写入冲突
- **服务端合并**：全部分片完成后由服务端合并为完整文件

接口：`POST /api/data/upload/init` → `POST /api/data/upload/chunk` → `POST /api/data/upload/merge`，辅以 `GET /api/data/upload/status` 与取消接口。

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

### 5. 可观测性

- **SkyWalking**：全链路调用追踪，覆盖网关与各业务服务
- **Prometheus + Actuator**：JVM 与服务指标暴露，支持 Grafana 看板
- **统一异常处理**：全局异常拦截与标准化错误响应

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

启动前需确保 Nacos、PostgreSQL、Redis 可用，并按需修改各模块 `application.yml` 中引入的公共配置（`common/config/src/main/resources/` 下的 `*.yml`）。

> ⚠️ 配置文件中不应包含生产环境明文凭据，部署时请将数据库/缓存/对象存储的连接信息迁移至环境变量或配置中心。

## 部署

- 支持 Gradle SSH 插件自动化部署：`gradle buildModulesParallel` 构建完成后自动将各模块 jar 上传至目标服务器
- 生产环境部署于 Kubernetes 集群（worker 节点运行服务实例）
