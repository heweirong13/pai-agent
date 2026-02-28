package com.paiagent.dto;

import lombok.Data;

/**
 * 更新工作流请求
 */
@Data
public class UpdateWorkflowRequest {
    
    private String workflowName;
    
    private String workflowDesc;
    
    /**
     * 工作流图结构（JSON字符串）
     */
    private String workflowGraph;
}
