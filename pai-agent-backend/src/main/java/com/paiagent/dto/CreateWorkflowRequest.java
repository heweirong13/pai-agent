package com.paiagent.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 创建工作流请求
 */
@Data
public class CreateWorkflowRequest {
    
    @NotBlank(message = "工作流名称不能为空")
    private String workflowName;
    
    private String workflowDesc;
    
    /**
     * 工作流图结构（JSON字符串）
     */
    private String workflowGraph;
}
