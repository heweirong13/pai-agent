'use client';

import { useState } from 'react';
import { useExecutionStore, useWorkflowStore } from '@/stores';
import { runWorkflow } from '@/lib/api';
import { X, Play, Loader2, CheckCircle, XCircle, Clock } from 'lucide-react';
import { cn } from '@/lib/utils';
import AudioPlayer from './AudioPlayer';

export default function DebugDrawer() {
  const { workflowId } = useWorkflowStore();
  const {
    isDebugDrawerOpen,
    setDebugDrawerOpen,
    inputText,
    setInputText,
    executionStatus,
    streamingOutput,
    audioUrl,
    nodeStatuses,
    reset,
    setExecutionId,
    setExecutionStatus,
  } = useExecutionStore();

  const [isRunning, setIsRunning] = useState(false);

  const handleRun = async () => {
    if (!workflowId || !inputText.trim()) return;

    try {
      setIsRunning(true);
      reset();
      setExecutionStatus('RUNNING');

      const { executionId } = await runWorkflow(workflowId, { userInput: inputText });
      setExecutionId(executionId);

      // TODO: 连接 WebSocket 接收实时状态
    } catch (error) {
      setExecutionStatus('FAILED');
      console.error('执行失败:', error);
    } finally {
      setIsRunning(false);
    }
  };

  if (!isDebugDrawerOpen) return null;

  return (
    <div className="fixed inset-y-0 right-0 w-96 bg-white shadow-xl z-50 flex flex-col">
      {/* Header */}
      <div className="flex items-center justify-between p-4 border-b border-gray-200">
        <h2 className="text-lg font-semibold text-gray-900">调试面板</h2>
        <button
          onClick={() => setDebugDrawerOpen(false)}
          className="p-1 hover:bg-gray-100 rounded"
        >
          <X className="w-5 h-5 text-gray-500" />
        </button>
      </div>

      {/* Input Section */}
      <div className="p-4 border-b border-gray-200">
        <label className="block text-sm font-medium text-gray-700 mb-2">
          输入文本
        </label>
        <textarea
          value={inputText}
          onChange={(e) => setInputText(e.target.value)}
          placeholder="请输入测试文本..."
          rows={4}
          className="w-full px-3 py-2 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 resize-none"
        />
        <button
          onClick={handleRun}
          disabled={isRunning || !inputText.trim()}
          className={cn(
            'mt-3 w-full py-2 rounded-lg flex items-center justify-center gap-2 transition-colors',
            isRunning || !inputText.trim()
              ? 'bg-gray-300 cursor-not-allowed'
              : 'bg-indigo-600 hover:bg-indigo-700 text-white'
          )}
        >
          {isRunning ? (
            <>
              <Loader2 className="w-4 h-4 animate-spin" />
              执行中...
            </>
          ) : (
            <>
              <Play className="w-4 h-4" />
              运行
            </>
          )}
        </button>
      </div>

      {/* Execution Status */}
      <div className="flex-1 overflow-y-auto p-4 space-y-4">
        {/* Status Badge */}
        <div className="flex items-center gap-2">
          <span className="text-sm font-medium text-gray-700">执行状态:</span>
          <StatusBadge status={executionStatus} />
        </div>

        {/* Node Statuses */}
        {nodeStatuses.size > 0 && (
          <div>
            <h3 className="text-sm font-medium text-gray-700 mb-2">节点状态</h3>
            <div className="space-y-2">
              {Array.from(nodeStatuses.values()).map((node) => (
                <div
                  key={node.nodeId}
                  className="flex items-center gap-2 p-2 bg-gray-50 rounded-lg"
                >
                  <NodeStatusIcon status={node.status} />
                  <span className="text-sm text-gray-600">{node.nodeId}</span>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* Streaming Output */}
        {streamingOutput && (
          <div>
            <h3 className="text-sm font-medium text-gray-700 mb-2">输出内容</h3>
            <div className="p-3 bg-gray-50 rounded-lg text-sm text-gray-800 whitespace-pre-wrap max-h-60 overflow-y-auto">
              {streamingOutput}
            </div>
          </div>
        )}

        {/* Audio Player */}
        {audioUrl && (
          <div>
            <h3 className="text-sm font-medium text-gray-700 mb-2">音频输出</h3>
            <AudioPlayer src={audioUrl} />
          </div>
        )}
      </div>
    </div>
  );
}

function StatusBadge({ status }: { status: string }) {
  const statusConfig = {
    PENDING: { bg: 'bg-gray-100', text: 'text-gray-600', label: '等待中' },
    RUNNING: { bg: 'bg-blue-100', text: 'text-blue-600', label: '执行中' },
    SUCCESS: { bg: 'bg-green-100', text: 'text-green-600', label: '成功' },
    FAILED: { bg: 'bg-red-100', text: 'text-red-600', label: '失败' },
    CANCELLED: { bg: 'bg-yellow-100', text: 'text-yellow-600', label: '已取消' },
  };

  const config = statusConfig[status as keyof typeof statusConfig] || statusConfig.PENDING;

  return (
    <span className={cn('px-2 py-1 rounded-full text-xs font-medium', config.bg, config.text)}>
      {config.label}
    </span>
  );
}

function NodeStatusIcon({ status }: { status: string }) {
  switch (status) {
    case 'RUNNING':
      return <Loader2 className="w-4 h-4 text-blue-500 animate-spin" />;
    case 'SUCCESS':
      return <CheckCircle className="w-4 h-4 text-green-500" />;
    case 'FAILED':
      return <XCircle className="w-4 h-4 text-red-500" />;
    default:
      return <Clock className="w-4 h-4 text-gray-400" />;
  }
}
