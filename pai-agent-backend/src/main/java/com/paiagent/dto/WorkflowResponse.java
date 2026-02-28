package com.paiagent.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 工作流响应
 */
@Data
public class WorkflowResponse {
    
    private Long id;
    private String workflowName;
    private String workflowDesc;
    private Integer version;
    private String workflowGraph;
    private String status;
    private String createdBy;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}
