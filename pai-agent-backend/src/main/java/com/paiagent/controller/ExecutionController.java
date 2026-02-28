package com.paiagent.controller;

import com.paiagent.common.Result;
import com.paiagent.dto.RunWorkflowRequest;
import com.paiagent.entity.ExecutionRecord;
import com.paiagent.service.ExecutionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 执行管理接口
 */
@Tag(name = "执行管理")
@RestController
@RequestMapping("/api/v1/executions")
@RequiredArgsConstructor
public class ExecutionController {

    private final ExecutionService executionService;

    @Operation(summary = "执行工作流")
    @PostMapping("/run")
    public Result<Map<String, String>> runWorkflow(@Valid @RequestBody RunWorkflowRequest request) {
        String executionId = executionService.runWorkflow(request);
        return Result.success(Map.of("executionId", executionId));
    }

    @Operation(summary = "获取执行记录")
    @GetMapping("/{executionId}")
    public Result<ExecutionRecord> getExecution(@PathVariable String executionId) {
        ExecutionRecord record = executionService.getExecution(executionId);
        return Result.success(record);
    }
}
