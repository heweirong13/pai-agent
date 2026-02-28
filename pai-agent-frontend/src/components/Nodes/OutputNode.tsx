'use client';

import { memo } from 'react';
import { Handle, Position, NodeProps } from 'reactflow';
import { NodeData } from '@/types';
import { cn } from '@/lib/utils';
import { CheckCircle } from 'lucide-react';

function OutputNode({ data, selected }: NodeProps<NodeData>) {
  return (
    <div
      className={cn(
        'px-4 py-3 rounded-lg border-2 bg-white shadow-md min-w-[150px]',
        selected ? 'border-indigo-500 shadow-lg' : 'border-gray-200'
      )}
    >
      <Handle
        type="target"
        position={Position.Top}
        className="w-3 h-3 bg-indigo-500 border-2 border-white"
      />
      <div className="flex items-center gap-2">
        <div className="w-8 h-8 rounded-full bg-green-100 flex items-center justify-center">
          <CheckCircle className="w-4 h-4 text-green-600" />
        </div>
        <div>
          <div className="text-sm font-medium text-gray-900">{data.label}</div>
          <div className="text-xs text-gray-500">结束输出</div>
        </div>
      </div>
    </div>
  );
}

export default memo(OutputNode);
