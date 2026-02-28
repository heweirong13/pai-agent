'use client';

import { DragEvent } from 'react';
import { NodeType } from '@/types';
import { cn } from '@/lib/utils';
import { MessageSquare, Bot, Volume2, CheckCircle, ChevronDown } from 'lucide-react';
import { useState } from 'react';

interface NodeCategory {
  name: string;
  icon: React.ReactNode;
  nodes: {
    type: NodeType;
    label: string;
    description: string;
    icon: React.ReactNode;
    iconBg: string;
  }[];
}

const nodeCategories: NodeCategory[] = [
  {
    name: '大模型节点',
    icon: <Bot className="w-4 h-4" />,
    nodes: [
      {
        type: 'LLM',
        label: 'DeepSeek',
        description: '深度求索大模型',
        icon: <Bot className="w-4 h-4 text-blue-600" />,
        iconBg: 'bg-blue-100',
      },
      {
        type: 'LLM',
        label: '通义千问',
        description: '阿里云大模型',
        icon: <Bot className="w-4 h-4 text-orange-600" />,
        iconBg: 'bg-orange-100',
      },
      {
        type: 'LLM',
        label: 'AI Ping',
        description: 'OpenAI 大模型',
        icon: <Bot className="w-4 h-4 text-emerald-600" />,
        iconBg: 'bg-emerald-100',
      },
      {
        type: 'LLM',
        label: '智谱',
        description: '智谱 GLM 大模型',
        icon: <Bot className="w-4 h-4 text-purple-600" />,
        iconBg: 'bg-purple-100',
      },
    ],
  },
  {
    name: '工具节点',
    icon: <Volume2 className="w-4 h-4" />,
    nodes: [
      {
        type: 'TTS',
        label: '超拟人音频合成',
        description: '阿里云 TTS',
        icon: <Volume2 className="w-4 h-4 text-pink-600" />,
        iconBg: 'bg-pink-100',
      },
    ],
  },
];

interface DraggableNodeProps {
  type: NodeType;
  label: string;
  description: string;
  icon: React.ReactNode;
  iconBg: string;
}

function DraggableNode({ type, label, description, icon, iconBg }: DraggableNodeProps) {
  const onDragStart = (event: DragEvent<HTMLDivElement>) => {
    event.dataTransfer.setData('application/reactflow/type', type);
    event.dataTransfer.setData('application/reactflow/label', label);
    event.dataTransfer.effectAllowed = 'move';
  };

  return (
    <div
      className="flex items-center gap-3 p-2 rounded-lg cursor-grab hover:bg-gray-100 transition-colors"
      draggable
      onDragStart={onDragStart}
    >
      <div className={cn('w-8 h-8 rounded-lg flex items-center justify-center', iconBg)}>
        {icon}
      </div>
      <div className="flex-1 min-w-0">
        <div className="text-sm font-medium text-gray-900 truncate">{label}</div>
        <div className="text-xs text-gray-500 truncate">{description}</div>
      </div>
    </div>
  );
}

interface CategorySectionProps {
  category: NodeCategory;
  defaultExpanded?: boolean;
}

function CategorySection({ category, defaultExpanded = true }: CategorySectionProps) {
  const [expanded, setExpanded] = useState(defaultExpanded);

  return (
    <div className="mb-4">
      <button
        className="flex items-center gap-2 w-full px-2 py-1 text-sm font-medium text-gray-700 hover:bg-gray-100 rounded"
        onClick={() => setExpanded(!expanded)}
      >
        <ChevronDown
          className={cn('w-4 h-4 transition-transform', !expanded && '-rotate-90')}
        />
        {category.icon}
        <span>{category.name}</span>
      </button>
      {expanded && (
        <div className="mt-2 space-y-1">
          {category.nodes.map((node, index) => (
            <DraggableNode key={`${node.type}-${index}`} {...node} />
          ))}
        </div>
      )}
    </div>
  );
}

export default function NodeLibrary() {
  return (
    <div className="w-64 bg-white border-r border-gray-200 flex flex-col h-full">
      <div className="p-4 border-b border-gray-200">
        <h2 className="text-lg font-semibold text-gray-900">节点库</h2>
      </div>
      <div className="flex-1 overflow-y-auto p-4">
        {nodeCategories.map((category) => (
          <CategorySection key={category.name} category={category} />
        ))}
      </div>
      <div className="p-4 border-t border-gray-200 text-xs text-gray-500">
        拖拽节点到画布中使用
      </div>
    </div>
  );
}
