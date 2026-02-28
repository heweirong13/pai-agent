'use client';

import { memo } from 'react';
import { Handle, Position, NodeProps } from 'reactflow';
import { NodeData, TTSNodeConfig } from '@/types';
import { cn } from '@/lib/utils';
import { Volume2 } from 'lucide-react';

function TTSNode({ data, selected }: NodeProps<NodeData>) {
  const config = data.config as TTSNodeConfig | undefined;

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
        <div className="w-8 h-8 rounded-full bg-pink-100 flex items-center justify-center">
          <Volume2 className="w-4 h-4 text-pink-600" />
        </div>
        <div className="flex-1 min-w-0">
          <div className="text-sm font-medium text-gray-900 truncate">{data.label}</div>
          <div className="text-xs text-gray-500">超拟人音频合成</div>
        </div>
      </div>
      {config?.voice && (
        <div className="mt-2 text-xs text-gray-400 bg-gray-50 rounded px-2 py-1 truncate">
          发音人: {config.voice}
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

export default memo(TTSNode);
