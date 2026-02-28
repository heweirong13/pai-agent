package com.paiagent.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 执行记录实体
 */
@Data
@TableName("execution_record")
public class ExecutionRecord {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 工作流ID
     */
    private Long workflowId;

    /**
     * 执行唯一标识
     */
    private String executionId;

    /**
     * 输入数据（JSON格式）
     */
    private String inputData;

    /**
     * 输出数据（JSON格式）
     */
    private String outputData;

    /**
     * 执行状态: PENDING, RUNNING, SUCCESS, FAILED, CANCELLED
     */
    private String executionStatus;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 执行时长（毫秒）
     */
    private Long duration;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
}
