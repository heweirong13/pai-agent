package com.paiagent.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 工作流定义实体
 */
@Data
@TableName("workflow_definition")
public class WorkflowDefinition {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 工作流名称
     */
    private String workflowName;

    /**
     * 工作流描述
     */
    private String workflowDesc;

    /**
     * 版本号
     */
    private Integer version;

    /**
     * 工作流图结构（JSON格式，包含nodes和edges）
     */
    private String workflowGraph;

    /**
     * 状态: DRAFT-草稿, PUBLISHED-已发布, ARCHIVED-已归档
     */
    private String status;

    /**
     * 创建人
     */
    private String createdBy;

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

    /**
     * 是否删除
     */
    @TableLogic
    private Integer deleted;
}
