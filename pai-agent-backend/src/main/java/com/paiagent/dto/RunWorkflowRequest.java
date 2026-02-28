package com.paiagent.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

/**
 * 执行工作流请求
 */
@Data
public class RunWorkflowRequest {
    
    @NotNull(message = "工作流ID不能为空")
    private Long workflowId;
    
    /**
     * 输入数据
     */
    private Map<String, Object> inputData;
}
