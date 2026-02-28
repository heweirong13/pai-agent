package com.paiagent.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.paiagent.common.Result;
import com.paiagent.dto.CreateWorkflowRequest;
import com.paiagent.dto.UpdateWorkflowRequest;
import com.paiagent.dto.WorkflowResponse;
import com.paiagent.service.WorkflowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 工作流管理接口
 */
@Tag(name = "工作流管理")
@RestController
@RequestMapping("/api/v1/workflows")
@RequiredArgsConstructor
public class WorkflowController {

    private final WorkflowService workflowService;

    @Operation(summary = "创建工作流")
    @PostMapping
    public Result<WorkflowResponse> createWorkflow(@Valid @RequestBody CreateWorkflowRequest request) {
        WorkflowResponse response = workflowService.createWorkflow(request);
        return Result.success(response);
    }

    @Operation(summary = "更新工作流")
    @PutMapping("/{id}")
    public Result<WorkflowResponse> updateWorkflow(
            @PathVariable Long id,
            @RequestBody UpdateWorkflowRequest request) {
        WorkflowResponse response = workflowService.updateWorkflow(id, request);
        return Result.success(response);
    }

    @Operation(summary = "获取工作流详情")
    @GetMapping("/{id}")
    public Result<WorkflowResponse> getWorkflow(@PathVariable Long id) {
        WorkflowResponse response = workflowService.getWorkflow(id);
        return Result.success(response);
    }

    @Operation(summary = "获取工作流列表")
    @GetMapping
    public Result<Page<WorkflowResponse>> listWorkflows(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<WorkflowResponse> result = workflowService.listWorkflows(page, size);
        return Result.success(result);
    }

    @Operation(summary = "删除工作流")
    @DeleteMapping("/{id}")
    public Result<Void> deleteWorkflow(@PathVariable Long id) {
        workflowService.deleteWorkflow(id);
        return Result.success();
    }

    @Operation(summary = "复制工作流")
    @PostMapping("/{id}/copy")
    public Result<WorkflowResponse> copyWorkflow(@PathVariable Long id) {
        WorkflowResponse response = workflowService.copyWorkflow(id);
        return Result.success(response);
    }
}
