package com.paiagent.websocket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 执行事件
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecutionEvent {

    /**
     * 事件类型
     */
    private EventType type;

    /**
     * 执行ID
     */
    private String executionId;

    /**
     * 节点ID
     */
    private String nodeId;

    /**
     * 节点名称
     */
    private String nodeName;

    /**
     * 节点类型
     */
    private String nodeType;

    /**
     * 输出数据
     */
    private Object data;

    /**
     * 错误信息
     */
    private String error;

    /**
     * 音频URL
     */
    private String audioUrl;

    /**
     * 时间戳
     */
    private Long timestamp;

    /**
     * 事件类型枚举
     */
    public enum EventType {
        EXECUTION_START,      // 执行开始
        NODE_START,           // 节点开始执行
        NODE_OUTPUT,          // 节点输出（流式文本）
        NODE_COMPLETE,        // 节点执行完成
        NODE_ERROR,           // 节点执行失败
        AUDIO_GENERATED,      // 音频生成完成
        EXECUTION_COMPLETE,   // 执行完成
        EXECUTION_FAILED      // 执行失败
    }

    public static ExecutionEvent executionStart(String executionId) {
        return ExecutionEvent.builder()
                .type(EventType.EXECUTION_START)
                .executionId(executionId)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    public static ExecutionEvent nodeStart(String executionId, String nodeId, String nodeName, String nodeType) {
        return ExecutionEvent.builder()
                .type(EventType.NODE_START)
                .executionId(executionId)
                .nodeId(nodeId)
                .nodeName(nodeName)
                .nodeType(nodeType)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    public static ExecutionEvent nodeOutput(String executionId, String nodeId, Object data) {
        return ExecutionEvent.builder()
                .type(EventType.NODE_OUTPUT)
                .executionId(executionId)
                .nodeId(nodeId)
                .data(data)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    public static ExecutionEvent nodeComplete(String executionId, String nodeId, Object data) {
        return ExecutionEvent.builder()
                .type(EventType.NODE_COMPLETE)
                .executionId(executionId)
                .nodeId(nodeId)
                .data(data)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    public static ExecutionEvent nodeError(String executionId, String nodeId, String error) {
        return ExecutionEvent.builder()
                .type(EventType.NODE_ERROR)
                .executionId(executionId)
                .nodeId(nodeId)
                .error(error)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    public static ExecutionEvent audioGenerated(String executionId, String nodeId, String audioUrl) {
        return ExecutionEvent.builder()
                .type(EventType.AUDIO_GENERATED)
                .executionId(executionId)
                .nodeId(nodeId)
                .audioUrl(audioUrl)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    public static ExecutionEvent executionComplete(String executionId, Object data) {
        return ExecutionEvent.builder()
                .type(EventType.EXECUTION_COMPLETE)
                .executionId(executionId)
                .data(data)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    public static ExecutionEvent executionFailed(String executionId, String error) {
        return ExecutionEvent.builder()
                .type(EventType.EXECUTION_FAILED)
                .executionId(executionId)
                .error(error)
                .timestamp(System.currentTimeMillis())
                .build();
    }
}
