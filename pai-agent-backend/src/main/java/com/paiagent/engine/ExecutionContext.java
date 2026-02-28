package com.paiagent.engine;

import com.paiagent.websocket.ExecutionWebSocketHandler;
import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 执行上下文
 */
@Data
@Builder
public class ExecutionContext {

    /**
     * 执行 ID
     */
    private String executionId;

    /**
     * 工作流 ID
     */
    private Long workflowId;

    /**
     * 全局变量（存储节点间传递的数据）
     */
    @Builder.Default
    private Map<String, Object> variables = new ConcurrentHashMap<>();

    /**
     * WebSocket 处理器（用于实时推送）
     */
    private ExecutionWebSocketHandler webSocketHandler;

    /**
     * 设置变量
     */
    public void setVariable(String key, Object value) {
        variables.put(key, value);
    }

    /**
     * 获取变量
     */
    public Object getVariable(String key) {
        return variables.get(key);
    }

    /**
     * 获取字符串变量
     */
    public String getStringVariable(String key) {
        Object value = variables.get(key);
        return value != null ? value.toString() : null;
    }
}
