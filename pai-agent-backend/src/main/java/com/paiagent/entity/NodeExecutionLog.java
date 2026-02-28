package com.paiagent.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 节点执行日志实体
 */
@Data
@TableName("node_execution_log")
public class NodeExecutionLog {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 执行记录ID
     */
    private Long executionRecordId;

    /**
     * 执行唯一标识
     */
    private String executionId;

    /**
     * 节点ID
     */
    private String nodeId;

    /**
     * 节点类型
     */
    private String nodeType;

    /**
     * 节点名称
     */
    private String nodeName;

    /**
     * 输入数据（JSON格式）
     */
    private String inputData;

    /**
     * 输出数据（JSON格式）
     */
    private String outputData;

    /**
     * 执行状态: PENDING, RUNNING, SUCCESS, FAILED
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
