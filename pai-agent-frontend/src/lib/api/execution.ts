import apiClient from './client';
import { ApiResponse, ExecutionRecord, NodeExecutionLog } from '@/types';

// 执行工作流
export async function runWorkflow(
  workflowId: number,
  inputData: Record<string, unknown>
): Promise<{ executionId: string }> {
  const response = await apiClient.post<ApiResponse<{ executionId: string }>>('/executions/run', {
    workflowId,
    inputData,
  });
  return response.data.data;
}

// 获取执行记录
export async function getExecution(executionId: string): Promise<ExecutionRecord> {
  const response = await apiClient.get<ApiResponse<ExecutionRecord>>(`/executions/${executionId}`);
  return response.data.data;
}

// 获取执行日志
export async function getExecutionLogs(executionId: string): Promise<NodeExecutionLog[]> {
  const response = await apiClient.get<ApiResponse<NodeExecutionLog[]>>(
    `/executions/${executionId}/logs`
  );
  return response.data.data;
}

// 取消执行
export async function cancelExecution(executionId: string): Promise<void> {
  await apiClient.post(`/executions/${executionId}/cancel`);
}
