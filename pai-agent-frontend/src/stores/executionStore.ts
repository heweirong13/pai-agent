import { create } from 'zustand';
import { ExecutionStatus, ExecutionEvent, NodeExecutionLog } from '@/types';

interface NodeStatus {
  nodeId: string;
  status: ExecutionStatus;
  output?: string;
  error?: string;
}

interface ExecutionState {
  // 执行信息
  executionId: string | null;
  executionStatus: ExecutionStatus;
  
  // 节点状态
  nodeStatuses: Map<string, NodeStatus>;
  
  // 执行日志
  logs: NodeExecutionLog[];
  
  // 流式输出
  streamingOutput: string;
  currentStreamingNodeId: string | null;
  
  // 音频
  audioUrl: string | null;
  
  // 调试抽屉
  isDebugDrawerOpen: boolean;
  
  // 输入数据
  inputText: string;
  
  // Actions
  setExecutionId: (id: string | null) => void;
  setExecutionStatus: (status: ExecutionStatus) => void;
  updateNodeStatus: (nodeId: string, status: NodeStatus) => void;
  appendStreamingOutput: (text: string) => void;
  setCurrentStreamingNodeId: (nodeId: string | null) => void;
  clearStreamingOutput: () => void;
  setAudioUrl: (url: string | null) => void;
  setLogs: (logs: NodeExecutionLog[]) => void;
  addLog: (log: NodeExecutionLog) => void;
  handleEvent: (event: ExecutionEvent) => void;
  toggleDebugDrawer: () => void;
  setDebugDrawerOpen: (open: boolean) => void;
  setInputText: (text: string) => void;
  reset: () => void;
}

export const useExecutionStore = create<ExecutionState>((set, get) => ({
  executionId: null,
  executionStatus: 'PENDING',
  nodeStatuses: new Map(),
  logs: [],
  streamingOutput: '',
  currentStreamingNodeId: null,
  audioUrl: null,
  isDebugDrawerOpen: false,
  inputText: '',

  setExecutionId: (id) => set({ executionId: id }),

  setExecutionStatus: (status) => set({ executionStatus: status }),

  updateNodeStatus: (nodeId, status) => {
    set((state) => {
      const newStatuses = new Map(state.nodeStatuses);
      newStatuses.set(nodeId, status);
      return { nodeStatuses: newStatuses };
    });
  },

  appendStreamingOutput: (text) => {
    set((state) => ({
      streamingOutput: state.streamingOutput + text,
    }));
  },

  setCurrentStreamingNodeId: (nodeId) => {
    set({ currentStreamingNodeId: nodeId });
  },

  clearStreamingOutput: () => {
    set({ streamingOutput: '', currentStreamingNodeId: null });
  },

  setAudioUrl: (url) => set({ audioUrl: url }),

  setLogs: (logs) => set({ logs }),

  addLog: (log) => {
    set((state) => ({
      logs: [...state.logs, log],
    }));
  },

  handleEvent: (event) => {
    const {
      updateNodeStatus,
      appendStreamingOutput,
      setCurrentStreamingNodeId,
      clearStreamingOutput,
      setAudioUrl,
      setExecutionStatus,
    } = get();

    switch (event.type) {
      case 'EXECUTION_START':
        setExecutionStatus('RUNNING');
        clearStreamingOutput();
        break;

      case 'NODE_START':
        if (event.nodeId) {
          updateNodeStatus(event.nodeId, {
            nodeId: event.nodeId,
            status: 'RUNNING',
          });
          setCurrentStreamingNodeId(event.nodeId);
          clearStreamingOutput();
        }
        break;

      case 'NODE_OUTPUT':
        if (event.data && typeof event.data === 'string') {
          appendStreamingOutput(event.data);
        }
        break;

      case 'NODE_COMPLETE':
        if (event.nodeId) {
          updateNodeStatus(event.nodeId, {
            nodeId: event.nodeId,
            status: 'SUCCESS',
            output: typeof event.data === 'string' ? event.data : JSON.stringify(event.data),
          });
        }
        break;

      case 'NODE_ERROR':
        if (event.nodeId) {
          updateNodeStatus(event.nodeId, {
            nodeId: event.nodeId,
            status: 'FAILED',
            error: event.error,
          });
        }
        break;

      case 'AUDIO_GENERATED':
        if (event.audioUrl) {
          setAudioUrl(event.audioUrl);
        }
        break;

      case 'EXECUTION_COMPLETE':
        setExecutionStatus('SUCCESS');
        break;

      case 'EXECUTION_FAILED':
        setExecutionStatus('FAILED');
        break;
    }
  },

  toggleDebugDrawer: () => {
    set((state) => ({ isDebugDrawerOpen: !state.isDebugDrawerOpen }));
  },

  setDebugDrawerOpen: (open) => set({ isDebugDrawerOpen: open }),

  setInputText: (text) => set({ inputText: text }),

  reset: () => {
    set({
      executionId: null,
      executionStatus: 'PENDING',
      nodeStatuses: new Map(),
      logs: [],
      streamingOutput: '',
      currentStreamingNodeId: null,
      audioUrl: null,
    });
  },
}));
