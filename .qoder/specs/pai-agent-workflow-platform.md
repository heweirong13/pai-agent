# PaiAgent-One AI Agent 工作流平台 - 实现计划

## 项目概述

构建一个完整的 AI Agent 流图执行平台，支持拖拽式工作流编辑、多大模型集成、音频合成，实现 AI 播客生成功能。

**架构模式**：单体应用（完整版）  
**用户系统**：无需认证

## 技术栈

| 层级 | 技术选型 |
|------|----------|
| 前端 | Next.js 14 + ReactFlow + Tailwind CSS + Zustand |
| 后端 | Java 17 + Spring Boot 3.x + MyBatis-Plus（单体应用） |
| 数据库 | MySQL 8.0 |
| 存储 | 本地文件存储（MVP）/ MinIO（后续） |
| 大模型 | DeepSeek / 通义千问 / OpenAI / 智谱 GLM |
| 音频合成 | 阿里云语音合成 |

---

## 系统架构（单体应用）

```
┌─────────────────────────────────────────────────────────────┐
│                    前端 (Next.js)                            │
│  ┌─────────────┬──────────────────┬──────────────────────┐  │
│  │  节点库      │     画布          │    配置面板/调试抽屉  │  │
│  └─────────────┴──────────────────┴──────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                              │ HTTP/WebSocket
                              ▼
┌─────────────────────────────────────────────────────────────┐
│              后端 (Spring Boot 单体应用)                     │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  WorkflowController  │  ExecutionController          │   │
│  │  LLMController       │  TTSController                │   │
│  ├──────────────────────────────────────────────────────┤   │
│  │  WorkflowService  │  ExecutionEngine  │  LLMService  │   │
│  │  TTSService       │  StorageService                  │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
                           MySQL
```

---

## 目录结构

### 后端结构（单体应用）
```
pai-agent-backend/
├── src/main/java/com/paiagent/
│   ├── PaiAgentApplication.java       # 启动类
│   ├── controller/
│   │   ├── WorkflowController.java    # 工作流 API
│   │   ├── ExecutionController.java   # 执行 API
│   │   └── NodeController.java        # 节点 API
│   ├── service/
│   │   ├── WorkflowService.java       # 工作流服务
│   │   ├── ExecutionService.java      # 执行服务
│   │   ├── LLMService.java            # 大模型服务
│   │   └── TTSService.java            # TTS 服务
│   ├── engine/
│   │   ├── WorkflowEngine.java        # 执行引擎
│   │   └── executor/
│   │       ├── NodeExecutor.java      # 节点执行器接口
│   │       ├── LLMNodeExecutor.java   # 大模型执行器
│   │       └── TTSNodeExecutor.java   # TTS 执行器
│   ├── llm/
│   │   ├── LLMProvider.java           # 大模型适配器接口
│   │   ├── DeepSeekProvider.java
│   │   ├── QwenProvider.java
│   │   ├── OpenAIProvider.java
│   │   └── GLMProvider.java
│   ├── entity/
│   │   ├── WorkflowDefinition.java
│   │   ├── WorkflowNode.java
│   │   └── ExecutionRecord.java
│   ├── mapper/
│   ├── config/
│   │   └── WebSocketConfig.java
│   └── websocket/
│       └── ExecutionWebSocketHandler.java
├── src/main/resources/
│   ├── application.yml
│   └── mapper/
└── pom.xml
```

### 前端结构
```
pai-agent-frontend/
├── src/
│   ├── app/                   # Next.js App Router
│   │   ├── page.tsx           # 首页（工作流列表）
│   │   └── workflow/
│   │       └── [id]/
│   │           └── page.tsx   # 工作流编辑器
│   ├── components/
│   │   ├── WorkflowCanvas/    # 画布组件
│   │   ├── NodeLibrary/       # 节点库
│   │   ├── NodeConfigPanel/   # 配置面板
│   │   ├── DebugDrawer/       # 调试抽屉
│   │   └── Nodes/             # 自定义节点
│   ├── stores/                # Zustand 状态
│   ├── hooks/                 # 自定义 Hooks
│   ├── lib/                   # 工具库
│   └── types/                 # TypeScript 类型
└── package.json
```

---

## 核心数据库表

| 表名 | 说明 |
|------|------|
| workflow_definition | 工作流定义（名称、描述、图结构JSON） |
| workflow_node | 节点配置（类型、配置JSON、位置） |
| workflow_edge | 连接边（源节点、目标节点） |
| execution_record | 执行记录（状态、输入输出、时间） |
| node_execution_log | 节点执行日志 |
| llm_provider_config | 大模型配置 |
| audio_file | 音频文件记录 |

---

## 核心 API

| 接口 | 方法 | 说明 |
|------|------|------|
| /api/v1/workflows | POST | 创建工作流 |
| /api/v1/workflows/{id} | PUT | 更新工作流 |
| /api/v1/workflows/{id} | GET | 获取工作流详情 |
| /api/v1/nodes/types | GET | 获取节点类型列表 |
| /api/v1/executions/run | POST | 执行工作流 |
| /api/v1/llm/generate | POST | 大模型生成（支持流式） |
| /api/v1/tts/synthesize | POST | 音频合成 |
| ws://*/ws/executions/{id} | WS | 实时执行状态 |

---

## 完整版实现计划

### 模块一：项目初始化与基础架构

#### 1.1 后端项目搭建
- [ ] 创建 Spring Boot 3.x 项目（Maven）
- [ ] 配置 MyBatis-Plus、MySQL、WebSocket 依赖
- [ ] 配置 application.yml（数据库、跨域、文件上传）
- [ ] 创建统一响应封装（Result、ErrorCode）
- [ ] 配置全局异常处理器

#### 1.2 前端项目搭建
- [ ] 创建 Next.js 14 项目（App Router）
- [ ] 集成 Tailwind CSS + shadcn/ui
- [ ] 集成 ReactFlow 11.x
- [ ] 配置 Zustand 状态管理
- [ ] 配置 Axios HTTP 客户端

#### 1.3 数据库初始化
- [ ] 创建 workflow_definition 表
- [ ] 创建 workflow_node 表
- [ ] 创建 workflow_edge 表
- [ ] 创建 execution_record 表
- [ ] 创建 node_execution_log 表
- [ ] 创建 llm_provider_config 表
- [ ] 创建 audio_file 表

---

### 模块二：工作流管理

#### 2.1 后端 API
- [ ] WorkflowController（CRUD 接口）
- [ ] WorkflowService（业务逻辑）
- [ ] WorkflowMapper（数据访问）
- [ ] 工作流版本管理
- [ ] 工作流复制功能

#### 2.2 前端页面
- [ ] 工作流列表页（首页）
- [ ] 新建工作流弹窗
- [ ] 工作流卡片组件（名称、描述、操作按钮）
- [ ] 删除确认弹窗

---

### 模块三：画布编辑器（核心）

#### 3.1 左侧节点库
- [ ] NodeLibrary 组件容器
- [ ] 节点分类（大模型节点、工具节点）
- [ ] DraggableNode 可拖拽节点
- [ ] 节点图标和样式

#### 3.2 中间画布区域
- [ ] ReactFlowCanvas 画布容器
- [ ] 自定义节点类型注册
- [ ] 节点拖拽添加（onDrop）
- [ ] 节点连接（Handles 配置）
- [ ] 节点选中高亮
- [ ] 画布缩放/平移
- [ ] 小地图（MiniMap）
- [ ] 控制面板（Controls）

#### 3.3 自定义节点组件
- [ ] InputNode（用户输入节点）
- [ ] OutputNode（输出节点）
- [ ] LLMNode（大模型节点）
  - 显示模型类型图标
  - 显示节点名称
  - 输入/输出 Handle
- [ ] TTSNode（音频合成节点）
  - 显示 TTS 图标
  - 显示节点名称

#### 3.4 右侧配置面板
- [ ] NodeConfigPanel 容器
- [ ] 通用配置（节点ID、节点名称）
- [ ] LLMNodeConfig（大模型配置）
  - 模型供应商选择
  - 模型选择
  - 系统提示词
  - 温度参数
  - 最大 Token 数
- [ ] TTSNodeConfig（TTS 配置）
  - 发音人选择
  - 语速调节
  - 音量调节
  - 音频格式
- [ ] OutputNodeConfig（输出配置）
  - 输出类型（文本/音频）
  - 变量引用配置

#### 3.5 工作流保存/加载
- [ ] 保存按钮（调用后端 API）
- [ ] 加载工作流（初始化画布）
- [ ] 自动保存（可选）

---

### 模块四：大模型集成

#### 4.1 适配器架构
- [ ] LLMProvider 接口定义
  - chat(messages, options)
  - streamChat(messages, options)
  - getModels()
- [ ] ProviderFactory 工厂类

#### 4.2 各厂商适配器
- [ ] DeepSeekProvider
  - API 对接
  - 流式响应处理
- [ ] QwenProvider（通义千问）
  - API 对接
  - 流式响应处理
- [ ] OpenAIProvider
  - API 对接（兼容格式）
  - 流式响应处理
- [ ] GLMProvider（智谱）
  - API 对接
  - 流式响应处理

#### 4.3 LLM API
- [ ] LLMController（/api/v1/llm/generate）
- [ ] LLMService
- [ ] SSE 流式响应支持

---

### 模块五：音频合成

#### 5.1 阿里云 TTS 集成
- [ ] 阿里云 SDK 集成
- [ ] TTSService 服务类
- [ ] 支持的发音人列表
- [ ] 音频参数配置（语速、音量、格式）

#### 5.2 TTS API
- [ ] TTSController（/api/v1/tts/synthesize）
- [ ] 音频文件存储（本地/MinIO）
- [ ] 音频 URL 返回

---

### 模块六：执行引擎

#### 6.1 引擎核心
- [ ] WorkflowEngine 执行引擎
- [ ] DAGBuilder（构建有向无环图）
- [ ] TopologySorter（拓扑排序）
- [ ] ExecutionContext（执行上下文）

#### 6.2 节点执行器
- [ ] NodeExecutor 接口
- [ ] InputNodeExecutor
- [ ] LLMNodeExecutor（调用大模型）
- [ ] TTSNodeExecutor（调用 TTS）
- [ ] OutputNodeExecutor

#### 6.3 状态管理
- [ ] 执行状态枚举（PENDING/RUNNING/SUCCESS/FAILED）
- [ ] 节点状态追踪
- [ ] 执行记录持久化

#### 6.4 执行 API
- [ ] ExecutionController
  - POST /api/v1/executions/run
  - GET /api/v1/executions/{id}
  - GET /api/v1/executions/{id}/logs
- [ ] ExecutionService

---

### 模块七：实时通信（WebSocket）

#### 7.1 后端 WebSocket
- [ ] WebSocketConfig 配置类
- [ ] ExecutionWebSocketHandler
- [ ] 事件类型定义
  - EXECUTION_START
  - NODE_START
  - NODE_OUTPUT（流式文本）
  - NODE_COMPLETE
  - NODE_ERROR
  - AUDIO_GENERATED
  - EXECUTION_COMPLETE

#### 7.2 前端 WebSocket
- [ ] useWebSocket Hook
- [ ] 自动重连机制
- [ ] 消息类型处理
- [ ] 执行状态同步

---

### 模块八：调试功能

#### 8.1 调试抽屉 UI
- [ ] DebugDrawer 容器组件
- [ ] 抽屉开启/关闭动画
- [ ] 输入面板（用户输入文本）
- [ ] 运行按钮

#### 8.2 执行日志展示
- [ ] ExecutionLog 组件
- [ ] 节点执行状态指示器
- [ ] 流式文本输出显示
- [ ] 错误信息展示

#### 8.3 音频播放器
- [ ] AudioPlayer 组件
- [ ] 播放/暂停控制
- [ ] 进度条
- [ ] 音量控制
- [ ] 下载按钮

---

### 模块九：UI 完善

#### 9.1 顶部工具栏
- [ ] 工作流名称编辑
- [ ] 新建按钮
- [ ] 保存按钮
- [ ] 加载按钮
- [ ] 调试按钮
- [ ] 用户头像/设置（预留）

#### 9.2 样式美化
- [ ] 整体配色方案（参考截图）
- [ ] 节点样式优化
- [ ] 连接线动画
- [ ] 响应式布局

---

### 模块十：测试与文档

#### 10.1 后端测试
- [ ] 单元测试（JUnit 5）
- [ ] API 接口测试
- [ ] 大模型适配器测试

#### 10.2 前端测试
- [ ] 组件测试（Jest）
- [ ] E2E 测试（Playwright）

#### 10.3 文档
- [ ] API 文档（Swagger）
- [ ] 部署文档
- [ ] 使用说明

---

## 关键文件清单

### 后端核心文件
| 文件路径 | 说明 |
|----------|------|
| `src/.../engine/WorkflowEngine.java` | 工作流执行引擎核心 |
| `src/.../engine/executor/NodeExecutor.java` | 节点执行器接口 |
| `src/.../llm/LLMProvider.java` | 大模型适配器接口 |
| `src/.../service/WorkflowService.java` | 工作流 CRUD 服务 |
| `src/.../websocket/ExecutionWebSocketHandler.java` | WebSocket 实时推送 |
| `src/.../controller/ExecutionController.java` | 执行 API |

### 前端核心文件
| 文件路径 | 说明 |
|----------|------|
| `src/components/WorkflowCanvas/ReactFlowCanvas.tsx` | 画布核心实现 |
| `src/stores/workflowStore.ts` | 工作流状态管理 |
| `src/hooks/useWorkflowExecution.ts` | 执行逻辑 Hook |
| `src/components/Nodes/LLMNode.tsx` | 大模型节点组件 |
| `src/components/DebugDrawer/index.tsx` | 调试抽屉 |

---

## 验证方案

### 单元测试
- 后端：JUnit 5 + Mockito
- 前端：Jest + React Testing Library

### 集成测试
1. 启动后端服务（Docker Compose）
2. 访问前端 http://localhost:3000
3. 创建工作流：输入 → 大模型 → TTS → 输出
4. 在调试抽屉输入文本
5. 验证：
   - 大模型返回文本（流式显示）
   - TTS 生成音频
   - 音频可正常播放

### 端到端测试
```bash
# 后端
cd pai-agent-backend && mvn test

# 前端
cd pai-agent-frontend && npm run test
```

---

## 依赖版本

### 后端
```xml
<java.version>17</java.version>
<spring-boot.version>3.2.x</spring-boot.version>
<mybatis-plus.version>3.5.x</mybatis-plus.version>
```

### 前端
```json
{
  "next": "14.x",
  "react": "18.x",
  "reactflow": "11.x",
  "zustand": "4.x",
  "tailwindcss": "3.x"
}
```
