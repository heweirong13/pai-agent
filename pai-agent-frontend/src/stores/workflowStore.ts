import { create } from 'zustand';
import { Node, Edge, Connection, addEdge, applyNodeChanges, applyEdgeChanges, NodeChange, EdgeChange } from 'reactflow';
import { NodeData, NodeType, LLMNodeConfig, TTSNodeConfig, WorkflowGraph } from '@/types';
import { generateId, generateEdgeId } from '@/lib/utils';

interface WorkflowState {
  // 工作流基本信息
  workflowId: number | null;
  workflowName: string;
  workflowDesc: string;
  
  // ReactFlow 节点和边
  nodes: Node<NodeData>[];
  edges: Edge[];
  
  // 当前选中的节点
  selectedNodeId: string | null;
  
  // 是否有未保存的更改
  isDirty: boolean;
  
  // Actions
  setWorkflowInfo: (id: number | null, name: string, desc: string) => void;
  setNodes: (nodes: Node<NodeData>[]) => void;
  setEdges: (edges: Edge[]) => void;
  onNodesChange: (changes: NodeChange[]) => void;
  onEdgesChange: (changes: EdgeChange[]) => void;
  onConnect: (connection: Connection) => void;
  addNode: (type: NodeType, position: { x: number; y: number }) => void;
  updateNodeData: (nodeId: string, data: Partial<NodeData>) => void;
  updateNodeConfig: (nodeId: string, config: Partial<LLMNodeConfig | TTSNodeConfig>) => void;
  deleteNode: (nodeId: string) => void;
  setSelectedNode: (nodeId: string | null) => void;
  loadWorkflowGraph: (graph: WorkflowGraph) => void;
  getWorkflowGraph: () => WorkflowGraph;
  clearWorkflow: () => void;
  setDirty: (dirty: boolean) => void;
}

const defaultNodeData: Record<NodeType, () => NodeData> = {
  INPUT: () => ({
    label: '用户输入',
    nodeType: 'INPUT',
    config: { variableName: 'userInput', defaultValue: '' },
  }),
  OUTPUT: () => ({
    label: '输出',
    nodeType: 'OUTPUT',
    config: { outputType: 'both', inputVariable: '' },
  }),
  LLM: () => ({
    label: '大模型',
    nodeType: 'LLM',
    config: {
      provider: 'DEEPSEEK',
      model: 'deepseek-chat',
      systemPrompt: '',
      temperature: 0.7,
      maxTokens: 2000,
      inputVariable: 'userInput',
    },
  }),
  TTS: () => ({
    label: '语音合成',
    nodeType: 'TTS',
    config: {
      voice: 'zhixiaoxia',
      speed: 1.0,
      volume: 50,
      format: 'mp3',
      inputVariable: '',
    },
  }),
};

export const useWorkflowStore = create<WorkflowState>((set, get) => ({
  workflowId: null,
  workflowName: '未命名工作流',
  workflowDesc: '',
  nodes: [],
  edges: [],
  selectedNodeId: null,
  isDirty: false,

  setWorkflowInfo: (id, name, desc) => {
    set({ workflowId: id, workflowName: name, workflowDesc: desc });
  },

  setNodes: (nodes) => {
    set({ nodes, isDirty: true });
  },

  setEdges: (edges) => {
    set({ edges, isDirty: true });
  },

  onNodesChange: (changes) => {
    set((state) => ({
      nodes: applyNodeChanges(changes, state.nodes),
      isDirty: true,
    }));
  },

  onEdgesChange: (changes) => {
    set((state) => ({
      edges: applyEdgeChanges(changes, state.edges),
      isDirty: true,
    }));
  },

  onConnect: (connection) => {
    set((state) => ({
      edges: addEdge(
        {
          ...connection,
          id: generateEdgeId(connection.source!, connection.target!),
          animated: true,
          style: { stroke: '#6366f1', strokeWidth: 2 },
        },
        state.edges
      ),
      isDirty: true,
    }));
  },

  addNode: (type, position) => {
    const id = generateId();
    const newNode: Node<NodeData> = {
      id,
      type: type.toLowerCase(),
      position,
      data: defaultNodeData[type](),
    };
    set((state) => ({
      nodes: [...state.nodes, newNode],
      selectedNodeId: id,
      isDirty: true,
    }));
  },

  updateNodeData: (nodeId, data) => {
    set((state) => ({
      nodes: state.nodes.map((node) =>
        node.id === nodeId ? { ...node, data: { ...node.data, ...data } } : node
      ),
      isDirty: true,
    }));
  },

  updateNodeConfig: (nodeId, config) => {
    set((state) => ({
      nodes: state.nodes.map((node) =>
        node.id === nodeId
          ? {
              ...node,
              data: {
                ...node.data,
                config: { ...node.data.config, ...config },
              },
            }
          : node
      ),
      isDirty: true,
    }));
  },

  deleteNode: (nodeId) => {
    set((state) => ({
      nodes: state.nodes.filter((node) => node.id !== nodeId),
      edges: state.edges.filter(
        (edge) => edge.source !== nodeId && edge.target !== nodeId
      ),
      selectedNodeId: state.selectedNodeId === nodeId ? null : state.selectedNodeId,
      isDirty: true,
    }));
  },

  setSelectedNode: (nodeId) => {
    set({ selectedNodeId: nodeId });
  },

  loadWorkflowGraph: (graph) => {
    const nodes: Node<NodeData>[] = graph.nodes.map((node) => ({
      id: node.id,
      type: node.type.toLowerCase(),
      position: node.position,
      data: node.data,
    }));

    const edges: Edge[] = graph.edges.map((edge) => ({
      id: edge.id,
      source: edge.source,
      target: edge.target,
      sourceHandle: edge.sourceHandle,
      targetHandle: edge.targetHandle,
      animated: true,
      style: { stroke: '#6366f1', strokeWidth: 2 },
    }));

    set({ nodes, edges, isDirty: false });
  },

  getWorkflowGraph: () => {
    const { nodes, edges } = get();
    return {
      nodes: nodes.map((node) => ({
        id: node.id,
        type: node.data.nodeType,
        position: node.position,
        data: node.data,
      })),
      edges: edges.map((edge) => ({
        id: edge.id,
        source: edge.source,
        target: edge.target,
        sourceHandle: edge.sourceHandle,
        targetHandle: edge.targetHandle,
      })),
    };
  },

  clearWorkflow: () => {
    set({
      workflowId: null,
      workflowName: '未命名工作流',
      workflowDesc: '',
      nodes: [],
      edges: [],
      selectedNodeId: null,
      isDirty: false,
    });
  },

  setDirty: (dirty) => {
    set({ isDirty: dirty });
  },
}));
