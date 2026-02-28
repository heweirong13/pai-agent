import apiClient from './client';
import { ApiResponse, WorkflowDefinition, WorkflowGraph, PageResponse } from '@/types';

// 获取工作流列表
export async function getWorkflows(page = 1, size = 10): Promise<PageResponse<WorkflowDefinition>> {
  const response = await apiClient.get<ApiResponse<PageResponse<WorkflowDefinition>>>('/workflows', {
    params: { page, size },
  });
  return response.data.data;
}

// 获取工作流详情
export async function getWorkflow(id: number): Promise<WorkflowDefinition> {
  const response = await apiClient.get<ApiResponse<WorkflowDefinition>>(`/workflows/${id}`);
  return response.data.data;
}

// 创建工作流
export async function createWorkflow(data: {
  workflowName: string;
  workflowDesc?: string;
  workflowGraph?: WorkflowGraph;
}): Promise<WorkflowDefinition> {
  const response = await apiClient.post<ApiResponse<WorkflowDefinition>>('/workflows', data);
  return response.data.data;
}

// 更新工作流
export async function updateWorkflow(
  id: number,
  data: {
    workflowName?: string;
    workflowDesc?: string;
    workflowGraph?: WorkflowGraph;
  }
): Promise<WorkflowDefinition> {
  const response = await apiClient.put<ApiResponse<WorkflowDefinition>>(`/workflows/${id}`, data);
  return response.data.data;
}

// 删除工作流
export async function deleteWorkflow(id: number): Promise<void> {
  await apiClient.delete(`/workflows/${id}`);
}

// 复制工作流
export async function copyWorkflow(id: number): Promise<WorkflowDefinition> {
  const response = await apiClient.post<ApiResponse<WorkflowDefinition>>(`/workflows/${id}/copy`);
  return response.data.data;
}
