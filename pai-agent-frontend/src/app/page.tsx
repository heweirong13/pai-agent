'use client';

import { useEffect } from 'react';
import Toolbar from '@/components/Toolbar';
import NodeLibrary from '@/components/NodeLibrary';
import WorkflowCanvas from '@/components/WorkflowCanvas';
import NodeConfigPanel from '@/components/NodeConfigPanel';
import DebugDrawer from '@/components/DebugDrawer';
import { useWorkflowStore } from '@/stores';

export default function Home() {
  const { setWorkflowInfo, addNode } = useWorkflowStore();

  // 初始化一个默认工作流
  useEffect(() => {
    setWorkflowInfo(1, 'qoder5', '');
    
    // 添加默认节点
    addNode('INPUT', { x: 250, y: 50 });
    addNode('LLM', { x: 250, y: 180 });
    addNode('TTS', { x: 250, y: 310 });
    addNode('OUTPUT', { x: 250, y: 440 });
  }, []);

  return (
    <div className="h-screen flex flex-col bg-gray-50">
      <Toolbar />
      <div className="flex-1 flex overflow-hidden">
        <NodeLibrary />
        <WorkflowCanvas />
        <NodeConfigPanel />
      </div>
      <DebugDrawer />
    </div>
  );
}
