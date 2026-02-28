package com.paiagent.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 工作流边（连接线）实体
 */
@Data
@TableName("workflow_edge")
public class WorkflowEdge {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 工作流ID
     */
    private Long workflowId;

    /**
     * 边唯一标识
     */
    private String edgeId;

    /**
     * 源节点ID
     */
    private String sourceNodeId;

    /**
     * 目标节点ID
     */
    private String targetNodeId;

    /**
     * 源节点输出锚点
     */
    private String sourceHandle;

    /**
     * 目标节点输入锚点
     */
    private String targetHandle;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
}
