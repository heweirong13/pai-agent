package com.paiagent.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 工作流节点实体
 */
@Data
@TableName("workflow_node")
public class WorkflowNode {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 工作流ID
     */
    private Long workflowId;

    /**
     * 节点唯一标识（前端生成）
     */
    private String nodeId;

    /**
     * 节点类型: INPUT, OUTPUT, LLM, TTS
     */
    private String nodeType;

    /**
     * 节点名称
     */
    private String nodeName;

    /**
     * 节点配置（JSON格式）
     */
    private String nodeConfig;

    /**
     * 画布X坐标
     */
    private Double positionX;

    /**
     * 画布Y坐标
     */
    private Double positionY;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
}
