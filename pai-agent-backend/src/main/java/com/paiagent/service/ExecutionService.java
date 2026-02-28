package com.paiagent.service;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.paiagent.common.BusinessException;
import com.paiagent.common.ErrorCode;
import com.paiagent.dto.RunWorkflowRequest;
import com.paiagent.engine.WorkflowEngine;
import com.paiagent.entity.ExecutionRecord;
import com.paiagent.entity.WorkflowDefinition;
import com.paiagent.mapper.ExecutionRecordMapper;
import com.paiagent.mapper.WorkflowDefinitionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 执行服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExecutionService {

    private final WorkflowDefinitionMapper workflowDefinitionMapper;
    private final ExecutionRecordMapper executionRecordMapper;
    private final WorkflowEngine workflowEngine;

    /**
     * 执行工作流
     */
    @Transactional
    public String runWorkflow(RunWorkflowRequest request) {
        // 获取工作流
        WorkflowDefinition workflow = workflowDefinitionMapper.selectById(request.getWorkflowId());
        if (workflow == null) {
            throw new BusinessException(ErrorCode.WORKFLOW_NOT_FOUND);
        }
        
        // 生成执行 ID
        String executionId = IdUtil.fastSimpleUUID();
        
        // 创建执行记录
        ExecutionRecord record = new ExecutionRecord();
        record.setWorkflowId(request.getWorkflowId());
        record.setExecutionId(executionId);
        record.setInputData(JSONUtil.toJsonStr(request.getInputData()));
        record.setExecutionStatus("RUNNING");
        record.setStartTime(LocalDateTime.now());
        
        executionRecordMapper.insert(record);
        
        // 异步执行工作流
        executeAsync(executionId, record.getId(), workflow, request.getInputData());
        
        return executionId;
    }

    /**
     * 异步执行工作流
     */
    @Async
    public void executeAsync(String executionId, Long recordId, WorkflowDefinition workflow, 
                            Map<String, Object> inputData) {
        log.info("异步执行工作流: executionId={}", executionId);
        
        LocalDateTime startTime = LocalDateTime.now();
        
        try {
            // 执行工作流
            WorkflowEngine.ExecutionResult result = workflowEngine.execute(
                    executionId,
                    workflow.getId(),
                    workflow.getWorkflowGraph(),
                    inputData != null ? inputData : new HashMap<>()
            );
            
            // 更新执行记录
            ExecutionRecord record = executionRecordMapper.selectById(recordId);
            if (result.isSuccess()) {
                record.setExecutionStatus("SUCCESS");
                record.setOutputData(JSONUtil.toJsonStr(result.getOutput()));
            } else {
                record.setExecutionStatus("FAILED");
                record.setErrorMessage(result.getError());
            }
            record.setEndTime(LocalDateTime.now());
            record.setDuration(java.time.Duration.between(startTime, record.getEndTime()).toMillis());
            
            executionRecordMapper.updateById(record);
            
        } catch (Exception e) {
            log.error("工作流执行异常", e);
            
            // 更新执行记录为失败
            ExecutionRecord record = executionRecordMapper.selectById(recordId);
            record.setExecutionStatus("FAILED");
            record.setErrorMessage(e.getMessage());
            record.setEndTime(LocalDateTime.now());
            record.setDuration(java.time.Duration.between(startTime, record.getEndTime()).toMillis());
            
            executionRecordMapper.updateById(record);
        }
    }

    /**
     * 获取执行记录
     */
    public ExecutionRecord getExecution(String executionId) {
        ExecutionRecord record = executionRecordMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ExecutionRecord>()
                        .eq(ExecutionRecord::getExecutionId, executionId)
        );
        if (record == null) {
            throw new BusinessException(ErrorCode.EXECUTION_NOT_FOUND);
        }
        return record;
    }
}
