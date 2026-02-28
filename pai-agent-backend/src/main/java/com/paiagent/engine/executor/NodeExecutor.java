package com.paiagent.engine.executor;

import com.paiagent.engine.ExecutionContext;

/**
 * 节点执行器接口
 */
public interface NodeExecutor {

    /**
     * 获取支持的节点类型
     */
    String getNodeType();

    /**
     * 执行节点
     */
    ExecutionResult execute(ExecutionContext context, NodeConfig config);

    /**
     * 执行结果
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    class ExecutionResult {
        /**
         * 是否成功
         */
        private boolean success;
        
        /**
         * 输出数据
         */
        private Object output;
        
        /**
         * 错误信息
         */
        private String error;
    }

    /**
     * 节点配置
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    class NodeConfig {
        private String nodeId;
        private String nodeName;
        private String nodeType;
        private String configJson;
    }
}
