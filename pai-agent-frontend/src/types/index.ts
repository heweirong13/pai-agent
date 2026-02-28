import { Node, Edge } from 'reactflow';

// 节点类型
export type NodeType = 'INPUT' | 'OUTPUT' | 'LLM' | 'TTS';

// 大模型供应商
export type LLMProvider = 'DEEPSEEK' | 'QWEN' | 'OPENAI' | 'GLM';

// 执行状态
export type ExecutionStatus = 'PENDING' | 'RUNNING' | 'SUCCESS' | 'FAILED' | 'CANCELLED';

// 工作流状态
export type WorkflowStatus = 'DRAFT' | 'PUBLISHED' | 'ARCHIVED';

// 工作流定义
export interface WorkflowDefinition {
  id: number;
  workflowName: string;
  workflowDesc?: string;
  version: number;
  workflowGraph?: WorkflowGraph;
  status: WorkflowStatus;
  createdBy?: string;
  createdTime?: string;
  updatedTime?: string;
}

// 工作流图
export interface WorkflowGraph {
  nodes: WorkflowNode[];
  edges: WorkflowEdge[];
}

// 工作流节点
export interface WorkflowNode {
  id: string;
  type: NodeType;
  position: { x: number; y: number };
  data: NodeData;
}

// 节点数据
export interface NodeData {
  label: string;
  nodeType: NodeType;
  config?: LLMNodeConfig | TTSNodeConfig | InputNodeConfig | OutputNodeConfig;
}

// LLM 节点配置
export interface LLMNodeConfig {
  provider: LLMProvider;
  model: string;
  systemPrompt?: string;
  temperature?: number;
  maxTokens?: number;
  inputVariable?: string;
}

// TTS 节点配置
export interface TTSNodeConfig {
  voice: string;
  speed?: number;
  volume?: number;
  format?: string;
  inputVariable?: string;
}

// 输入节点配置
export interface InputNodeConfig {
  variableName: string;
  defaultValue?: string;
}

// 输出节点配置
export interface OutputNodeConfig {
  outputType: 'text' | 'audio' | 'both';
  inputVariable?: string;
}

// 工作流边
export interface WorkflowEdge {
  id: string;
  source: string;
  target: string;
  sourceHandle?: string;
  targetHandle?: string;
}

// ReactFlow 节点类型
export type FlowNode = Node<NodeData>;
export type FlowEdge = Edge;

// 执行记录
export interface ExecutionRecord {
  id: number;
  workflowId: number;
  executionId: string;
  inputData?: Record<string, unknown>;
  outputData?: Record<string, unknown>;
  executionStatus: ExecutionStatus;
  startTime?: string;
  endTime?: string;
  duration?: number;
  errorMessage?: string;
  createdTime?: string;
}

// 节点执行日志
export interface NodeExecutionLog {
  id: number;
  executionId: string;
  nodeId: string;
  nodeType: NodeType;
  nodeName?: string;
  inputData?: Record<string, unknown>;
  outputData?: Record<string, unknown>;
  executionStatus: ExecutionStatus;
  startTime?: string;
  endTime?: string;
  duration?: number;
  errorMessage?: string;
}

// LLM 供应商配置
export interface LLMProviderConfig {
  id: number;
  providerName: LLMProvider;
  displayName: string;
  apiEndpoint?: string;
  modelList: string[];
  isEnabled: boolean;
  sortOrder: number;
}

// WebSocket 事件
export interface ExecutionEvent {
  type: EventType;
  executionId: string;
  nodeId?: string;
  nodeName?: string;
  nodeType?: string;
  data?: unknown;
  error?: string;
  audioUrl?: string;
  timestamp: number;
}

export type EventType =
  | 'EXECUTION_START'
  | 'NODE_START'
  | 'NODE_OUTPUT'
  | 'NODE_COMPLETE'
  | 'NODE_ERROR'
  | 'AUDIO_GENERATED'
  | 'EXECUTION_COMPLETE'
  | 'EXECUTION_FAILED';

// API 响应
export interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
  timestamp: number;
}

// 分页响应
export interface PageResponse<T> {
  records: T[];
  total: number;
  size: number;
  current: number;
  pages: number;
}
