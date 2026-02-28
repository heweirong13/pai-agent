package com.paiagent.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.paiagent.common.BusinessException;
import com.paiagent.common.ErrorCode;
import com.paiagent.dto.CreateWorkflowRequest;
import com.paiagent.dto.UpdateWorkflowRequest;
import com.paiagent.dto.WorkflowResponse;
import com.paiagent.entity.WorkflowDefinition;
import com.paiagent.mapper.WorkflowDefinitionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 工作流服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WorkflowService {

    private final WorkflowDefinitionMapper workflowDefinitionMapper;

    /**
     * 创建工作流
     */
    @Transactional
    public WorkflowResponse createWorkflow(CreateWorkflowRequest request) {
        WorkflowDefinition workflow = new WorkflowDefinition();
        workflow.setWorkflowName(request.getWorkflowName());
        workflow.setWorkflowDesc(request.getWorkflowDesc());
        workflow.setWorkflowGraph(request.getWorkflowGraph());
        workflow.setVersion(1);
        workflow.setStatus("DRAFT");
        workflow.setDeleted(0);
        
        workflowDefinitionMapper.insert(workflow);
        log.info("创建工作流成功: id={}, name={}", workflow.getId(), workflow.getWorkflowName());
        
        return toResponse(workflow);
    }

    /**
     * 更新工作流
     */
    @Transactional
    public WorkflowResponse updateWorkflow(Long id, UpdateWorkflowRequest request) {
        WorkflowDefinition workflow = getWorkflowById(id);
        
        if (request.getWorkflowName() != null) {
            workflow.setWorkflowName(request.getWorkflowName());
        }
        if (request.getWorkflowDesc() != null) {
            workflow.setWorkflowDesc(request.getWorkflowDesc());
        }
        if (request.getWorkflowGraph() != null) {
            workflow.setWorkflowGraph(request.getWorkflowGraph());
            workflow.setVersion(workflow.getVersion() + 1);
        }
        
        workflowDefinitionMapper.updateById(workflow);
        log.info("更新工作流成功: id={}", id);
        
        return toResponse(workflow);
    }

    /**
     * 获取工作流详情
     */
    public WorkflowResponse getWorkflow(Long id) {
        WorkflowDefinition workflow = getWorkflowById(id);
        return toResponse(workflow);
    }

    /**
     * 获取工作流列表
     */
    public Page<WorkflowResponse> listWorkflows(int page, int size) {
        Page<WorkflowDefinition> pageParam = new Page<>(page, size);
        
        LambdaQueryWrapper<WorkflowDefinition> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(WorkflowDefinition::getCreatedTime);
        
        Page<WorkflowDefinition> result = workflowDefinitionMapper.selectPage(pageParam, wrapper);
        
        Page<WorkflowResponse> responsePage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        responsePage.setRecords(result.getRecords().stream().map(this::toResponse).toList());
        
        return responsePage;
    }

    /**
     * 删除工作流
     */
    @Transactional
    public void deleteWorkflow(Long id) {
        WorkflowDefinition workflow = getWorkflowById(id);
        workflowDefinitionMapper.deleteById(id);
        log.info("删除工作流成功: id={}", id);
    }

    /**
     * 复制工作流
     */
    @Transactional
    public WorkflowResponse copyWorkflow(Long id) {
        WorkflowDefinition original = getWorkflowById(id);
        
        WorkflowDefinition copy = new WorkflowDefinition();
        copy.setWorkflowName(original.getWorkflowName() + " (副本)");
        copy.setWorkflowDesc(original.getWorkflowDesc());
        copy.setWorkflowGraph(original.getWorkflowGraph());
        copy.setVersion(1);
        copy.setStatus("DRAFT");
        copy.setDeleted(0);
        
        workflowDefinitionMapper.insert(copy);
        log.info("复制工作流成功: originalId={}, newId={}", id, copy.getId());
        
        return toResponse(copy);
    }

    /**
     * 根据ID获取工作流
     */
    private WorkflowDefinition getWorkflowById(Long id) {
        WorkflowDefinition workflow = workflowDefinitionMapper.selectById(id);
        if (workflow == null) {
            throw new BusinessException(ErrorCode.WORKFLOW_NOT_FOUND);
        }
        return workflow;
    }

    /**
     * 转换为响应对象
     */
    private WorkflowResponse toResponse(WorkflowDefinition workflow) {
        WorkflowResponse response = new WorkflowResponse();
        BeanUtils.copyProperties(workflow, response);
        return response;
    }
}
