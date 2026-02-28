'use client';

import { useState } from 'react';
import { useWorkflowStore, useExecutionStore } from '@/stores';
import { updateWorkflow } from '@/lib/api';
import { Save, Play, Plus, FolderOpen, User } from 'lucide-react';
import { cn } from '@/lib/utils';

export default function Toolbar() {
  const { workflowId, workflowName, isDirty, getWorkflowGraph, setDirty } = useWorkflowStore();
  const { toggleDebugDrawer } = useExecutionStore();
  const [isSaving, setIsSaving] = useState(false);

  const handleSave = async () => {
    if (!workflowId) return;
    
    try {
      setIsSaving(true);
      const graph = getWorkflowGraph();
      await updateWorkflow(workflowId, { workflowGraph: graph });
      setDirty(false);
    } catch (error) {
      console.error('保存失败:', error);
    } finally {
      setIsSaving(false);
    }
  };

  return (
    <div className="h-14 bg-white border-b border-gray-200 flex items-center justify-between px-4">
      {/* Left: Logo & Workflow Name */}
      <div className="flex items-center gap-4">
        <div className="flex items-center gap-2">
          <div className="w-8 h-8 bg-indigo-600 rounded-lg flex items-center justify-center">
            <span className="text-white font-bold text-sm">P</span>
          </div>
          <span className="font-bold text-lg text-gray-900">PaiAgent</span>
        </div>
        <div className="h-6 w-px bg-gray-200" />
        <div className="flex items-center gap-2">
          <span className="text-gray-600">{workflowName}</span>
          {isDirty && <span className="text-xs text-orange-500">未保存</span>}
        </div>
      </div>

      {/* Center: Actions */}
      <div className="flex items-center gap-2">
        <button className="flex items-center gap-1 px-3 py-1.5 text-sm text-gray-600 hover:bg-gray-100 rounded-lg transition-colors">
          <Plus className="w-4 h-4" />
          新建
        </button>
        <button
          onClick={handleSave}
          disabled={isSaving || !isDirty}
          className={cn(
            'flex items-center gap-1 px-3 py-1.5 text-sm rounded-lg transition-colors',
            isDirty
              ? 'bg-indigo-600 text-white hover:bg-indigo-700'
              : 'bg-gray-100 text-gray-400 cursor-not-allowed'
          )}
        >
          <Save className="w-4 h-4" />
          {isSaving ? '保存中...' : '保存'}
        </button>
        <button className="flex items-center gap-1 px-3 py-1.5 text-sm text-gray-600 hover:bg-gray-100 rounded-lg transition-colors">
          <FolderOpen className="w-4 h-4" />
          加载
        </button>
        <button
          onClick={toggleDebugDrawer}
          className="flex items-center gap-1 px-3 py-1.5 text-sm bg-green-600 text-white hover:bg-green-700 rounded-lg transition-colors"
        >
          <Play className="w-4 h-4" />
          调试
        </button>
      </div>

      {/* Right: User */}
      <div className="flex items-center gap-2">
        <button className="flex items-center gap-2 px-3 py-1.5 text-sm text-gray-600 hover:bg-gray-100 rounded-lg transition-colors">
          <User className="w-4 h-4" />
          admin
        </button>
        <button className="px-3 py-1.5 text-sm text-gray-600 hover:bg-gray-100 rounded-lg transition-colors">
          登出
        </button>
      </div>
    </div>
  );
}
