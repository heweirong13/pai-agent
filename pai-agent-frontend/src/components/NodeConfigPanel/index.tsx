'use client';

import { useWorkflowStore } from '@/stores';
import { NodeData, LLMNodeConfig, TTSNodeConfig, LLMProvider } from '@/types';
import { Settings, X } from 'lucide-react';

const llmProviders: { value: LLMProvider; label: string }[] = [
  { value: 'DEEPSEEK', label: 'DeepSeek' },
  { value: 'QWEN', label: '通义千问' },
  { value: 'OPENAI', label: 'OpenAI' },
  { value: 'GLM', label: '智谱AI' },
];

const llmModels: Record<LLMProvider, string[]> = {
  DEEPSEEK: ['deepseek-chat', 'deepseek-coder'],
  QWEN: ['qwen-turbo', 'qwen-plus', 'qwen-max'],
  OPENAI: ['gpt-3.5-turbo', 'gpt-4', 'gpt-4-turbo'],
  GLM: ['glm-4', 'glm-4-flash', 'glm-3-turbo'],
};

const ttsVoices = [
  { value: 'zhixiaoxia', label: '知小夏（女声）' },
  { value: 'zhixiaobai', label: '知小白（男声）' },
  { value: 'zhixiaomei', label: '知小美（女声）' },
  { value: 'zhigui', label: '知柜（男声）' },
];

export default function NodeConfigPanel() {
  const { nodes, selectedNodeId, updateNodeData, updateNodeConfig, setSelectedNode } = useWorkflowStore();
  
  const selectedNode = nodes.find((n) => n.id === selectedNodeId);
  
  if (!selectedNode) {
    return (
      <div className="w-80 bg-white border-l border-gray-200 flex flex-col h-full">
        <div className="p-4 border-b border-gray-200">
          <h2 className="text-lg font-semibold text-gray-900">节点配置</h2>
        </div>
        <div className="flex-1 flex items-center justify-center text-gray-400">
          <div className="text-center">
            <Settings className="w-12 h-12 mx-auto mb-2 opacity-50" />
            <p>选择一个节点进行配置</p>
          </div>
        </div>
      </div>
    );
  }

  const nodeData = selectedNode.data as NodeData;
  const nodeType = nodeData.nodeType;

  return (
    <div className="w-80 bg-white border-l border-gray-200 flex flex-col h-full">
      <div className="p-4 border-b border-gray-200 flex items-center justify-between">
        <h2 className="text-lg font-semibold text-gray-900">节点配置</h2>
        <button
          onClick={() => setSelectedNode(null)}
          className="p-1 hover:bg-gray-100 rounded"
        >
          <X className="w-5 h-5 text-gray-500" />
        </button>
      </div>
      
      <div className="flex-1 overflow-y-auto p-4 space-y-4">
        {/* 通用配置 */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">节点 ID</label>
          <input
            type="text"
            value={selectedNode.id}
            disabled
            className="w-full px-3 py-2 bg-gray-50 border border-gray-200 rounded-lg text-sm text-gray-500"
          />
        </div>
        
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">节点名称</label>
          <input
            type="text"
            value={nodeData.label}
            onChange={(e) => updateNodeData(selectedNode.id, { label: e.target.value })}
            className="w-full px-3 py-2 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
          />
        </div>

        {/* LLM 节点配置 */}
        {nodeType === 'LLM' && (
          <LLMConfig
            config={nodeData.config as LLMNodeConfig}
            onChange={(config) => updateNodeConfig(selectedNode.id, config)}
          />
        )}

        {/* TTS 节点配置 */}
        {nodeType === 'TTS' && (
          <TTSConfig
            config={nodeData.config as TTSNodeConfig}
            onChange={(config) => updateNodeConfig(selectedNode.id, config)}
          />
        )}

        {/* OUTPUT 节点配置 */}
        {nodeType === 'OUTPUT' && (
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">输出类型</label>
            <select
              value={(nodeData.config as any)?.outputType || 'both'}
              onChange={(e) => updateNodeConfig(selectedNode.id, { outputType: e.target.value })}
              className="w-full px-3 py-2 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
            >
              <option value="text">仅文本</option>
              <option value="audio">仅音频</option>
              <option value="both">文本+音频</option>
            </select>
          </div>
        )}
      </div>
      
      <div className="p-4 border-t border-gray-200">
        <button className="w-full py-2 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 transition-colors">
          保存配置
        </button>
      </div>
    </div>
  );
}

function LLMConfig({
  config,
  onChange,
}: {
  config?: LLMNodeConfig;
  onChange: (config: Partial<LLMNodeConfig>) => void;
}) {
  const provider = config?.provider || 'DEEPSEEK';
  const models = llmModels[provider] || [];

  return (
    <>
      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">模型供应商</label>
        <select
          value={provider}
          onChange={(e) => {
            const newProvider = e.target.value as LLMProvider;
            onChange({ 
              provider: newProvider, 
              model: llmModels[newProvider][0] 
            });
          }}
          className="w-full px-3 py-2 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
        >
          {llmProviders.map((p) => (
            <option key={p.value} value={p.value}>
              {p.label}
            </option>
          ))}
        </select>
      </div>
      
      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">模型</label>
        <select
          value={config?.model || models[0]}
          onChange={(e) => onChange({ model: e.target.value })}
          className="w-full px-3 py-2 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
        >
          {models.map((m) => (
            <option key={m} value={m}>
              {m}
            </option>
          ))}
        </select>
      </div>
      
      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">系统提示词</label>
        <textarea
          value={config?.systemPrompt || ''}
          onChange={(e) => onChange({ systemPrompt: e.target.value })}
          rows={4}
          placeholder="输入系统提示词..."
          className="w-full px-3 py-2 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 resize-none"
        />
      </div>
      
      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">
          温度: {config?.temperature || 0.7}
        </label>
        <input
          type="range"
          min="0"
          max="2"
          step="0.1"
          value={config?.temperature || 0.7}
          onChange={(e) => onChange({ temperature: parseFloat(e.target.value) })}
          className="w-full"
        />
      </div>
      
      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">最大 Token 数</label>
        <input
          type="number"
          value={config?.maxTokens || 2000}
          onChange={(e) => onChange({ maxTokens: parseInt(e.target.value) })}
          className="w-full px-3 py-2 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
        />
      </div>

      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">回答内容配置</label>
        <input
          type="text"
          value={config?.inputVariable || '{{userInput}}'}
          onChange={(e) => onChange({ inputVariable: e.target.value })}
          placeholder="{{userInput}}"
          className="w-full px-3 py-2 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
        />
      </div>
    </>
  );
}

function TTSConfig({
  config,
  onChange,
}: {
  config?: TTSNodeConfig;
  onChange: (config: Partial<TTSNodeConfig>) => void;
}) {
  return (
    <>
      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">发音人</label>
        <select
          value={config?.voice || 'zhixiaoxia'}
          onChange={(e) => onChange({ voice: e.target.value })}
          className="w-full px-3 py-2 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
        >
          {ttsVoices.map((v) => (
            <option key={v.value} value={v.value}>
              {v.label}
            </option>
          ))}
        </select>
      </div>
      
      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">
          语速: {config?.speed || 1.0}x
        </label>
        <input
          type="range"
          min="0.5"
          max="2"
          step="0.1"
          value={config?.speed || 1.0}
          onChange={(e) => onChange({ speed: parseFloat(e.target.value) })}
          className="w-full"
        />
      </div>
      
      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">
          音量: {config?.volume || 50}
        </label>
        <input
          type="range"
          min="0"
          max="100"
          value={config?.volume || 50}
          onChange={(e) => onChange({ volume: parseInt(e.target.value) })}
          className="w-full"
        />
      </div>
      
      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">音频格式</label>
        <select
          value={config?.format || 'mp3'}
          onChange={(e) => onChange({ format: e.target.value })}
          className="w-full px-3 py-2 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
        >
          <option value="mp3">MP3</option>
          <option value="wav">WAV</option>
          <option value="pcm">PCM</option>
        </select>
      </div>
    </>
  );
}
