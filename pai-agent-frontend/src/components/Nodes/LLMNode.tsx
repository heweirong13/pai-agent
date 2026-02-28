'use client';

import { memo } from 'react';
import { Handle, Position, NodeProps } from 'reactflow';
import { NodeData, LLMNodeConfig, LLMProvider } from '@/types';
import { cn } from '@/lib/utils';
import { Bot, Sparkles } from 'lucide-react';

const providerIcons: Record<LLMProvider, { bg: string; text: string; name: string }> = {
  DEEPSEEK: { bg: 'bg-blue-100', text: 'text-blue-600', name: 'DeepSeek' },
  QWEN: { bg: 'bg-orange-100', text: 'text-orange-600', name: '通义千问' },
  OPENAI: { bg: 'bg-emerald-100', text: 'text-emerald-600', name: 'OpenAI' },
  GLM: { bg: 'bg-purple-100', text: 'text-purple-600', name: '智谱AI' },
};

function LLMNode({ data, selected }: NodeProps<NodeData>) {
  const config = data.config as LLMNodeConfig | undefined;
  const provider = config?.provider || 'DEEPSEEK';
  const providerInfo = providerIcons[provider];

  return (
    <div
      className={cn(
        'px-4 py-3 rounded-lg border-2 bg-white shadow-md min-w-[160px]',
        selected ? 'border-indigo-500 shadow-lg' : 'border-gray-200'
      )}
    >
      <Handle
        type="target"
        position={Position.Top}
        className="w-3 h-3 bg-indigo-500 border-2 border-white"
      />
      <div className="flex items-center gap-2">
        <div className={cn('w-8 h-8 rounded-full flex items-center justify-center', providerInfo.bg)}>
          <Bot className={cn('w-4 h-4', providerInfo.text)} />
        </div>
        <div className="flex-1 min-w-0">
          <div className="text-sm font-medium text-gray-900 truncate">{data.label}</div>
          <div className="text-xs text-gray-500 flex items-center gap-1">
            <Sparkles className="w-3 h-3" />
            {providerInfo.name}
          </div>
        </div>
      </div>
      {config?.model && (
        <div className="mt-2 text-xs text-gray-400 bg-gray-50 rounded px-2 py-1 truncate">
          {config.model}
        </div>
      )}
      <Handle
        type="source"
        position={Position.Bottom}
        className="w-3 h-3 bg-indigo-500 border-2 border-white"
      />
    </div>
  );
}

export default memo(LLMNode);
