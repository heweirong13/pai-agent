package com.paiagent.engine;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.paiagent.engine.executor.NodeExecutor;
import com.paiagent.websocket.ExecutionEvent;
import com.paiagent.websocket.ExecutionWebSocketHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 工作流执行引擎
 */
@Slf4j
@Component
public class WorkflowEngine {

    private Map<String, NodeExecutor> executorMap;
    private final ExecutionWebSocketHandler webSocketHandler;

    /**
     * 构造函数，自动注入所有 NodeExecutor 实现
     */
    @Autowired
    public WorkflowEngine(List<NodeExecutor> executors, ExecutionWebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
        this.executorMap = new HashMap<>();
        for (NodeExecutor executor : executors) {
            this.executorMap.put(executor.getNodeType(), executor);
        }
    }

    /**
     * 初始化执行器映射
     */
    @jakarta.annotation.PostConstruct
    public void init() {
        log.info("工作流引擎初始化，已加载 {} 个节点执行器", executorMap.size());
        executorMap.forEach((type, executor) -> 
            log.info("  - {} -> {}", type, executor.getClass().getSimpleName())
        );
    }

    /**
     * 执行工作流
     */
    public ExecutionResult execute(String executionId, Long workflowId, String workflowGraph, 
                                   Map<String, Object> inputData) {
        log.info("开始执行工作流: executionId={}, workflowId={}", executionId, workflowId);
        
        // 创建执行上下文
        ExecutionContext context = ExecutionContext.builder()
                .executionId(executionId)
                .workflowId(workflowId)
                .variables(new HashMap<>(inputData))
                .webSocketHandler(webSocketHandler)
                .build();
        
        try {
            // 发送执行开始事件
            webSocketHandler.sendMessage(executionId, ExecutionEvent.executionStart(executionId));
            
            // 解析工作流图
            JSONObject graph = JSONUtil.parseObj(workflowGraph);
            JSONArray nodes = graph.getJSONArray("nodes");
            JSONArray edges = graph.getJSONArray("edges");
            
            // 构建执行顺序（拓扑排序）
            List<JSONObject> sortedNodes = topologicalSort(nodes, edges);
            
            // 按顺序执行节点
            for (JSONObject node : sortedNodes) {
                String nodeId = node.getStr("id");
                String nodeType = node.getJSONObject("data").getStr("nodeType");
                String nodeName = node.getJSONObject("data").getStr("label");
                JSONObject nodeConfig = node.getJSONObject("data").getJSONObject("config");
                
                log.info("执行节点: id={}, type={}, name={}", nodeId, nodeType, nodeName);
                
                // 发送节点开始事件
                webSocketHandler.sendMessage(executionId, 
                    ExecutionEvent.nodeStart(executionId, nodeId, nodeName, nodeType));
                
                // 获取执行器
                NodeExecutor executor = executorMap.get(nodeType);
                if (executor == null) {
                    throw new RuntimeException("不支持的节点类型: " + nodeType);
                }
                
                // 执行节点
                NodeExecutor.NodeConfig config = NodeExecutor.NodeConfig.builder()
                        .nodeId(nodeId)
                        .nodeName(nodeName)
                        .nodeType(nodeType)
                        .configJson(nodeConfig != null ? nodeConfig.toString() : "{}")
                        .build();
                
                NodeExecutor.ExecutionResult result = executor.execute(context, config);
                
                if (!result.isSuccess()) {
                    // 发送节点错误事件
                    webSocketHandler.sendMessage(executionId,
                        ExecutionEvent.nodeError(executionId, nodeId, result.getError()));
                    
                    // 发送执行失败事件
                    webSocketHandler.sendMessage(executionId,
                        ExecutionEvent.executionFailed(executionId, "节点 " + nodeName + " 执行失败: " + result.getError()));
                    
                    return ExecutionResult.builder()
                            .success(false)
                            .error(result.getError())
                            .build();
                }
                
                // 发送节点完成事件
                webSocketHandler.sendMessage(executionId,
                    ExecutionEvent.nodeComplete(executionId, nodeId, result.getOutput()));
            }
            
            // 获取最终输出
            Object finalOutput = context.getVariable("finalOutput");
            
            // 发送执行完成事件
            webSocketHandler.sendMessage(executionId,
                ExecutionEvent.executionComplete(executionId, finalOutput));
            
            return ExecutionResult.builder()
                    .success(true)
                    .output(finalOutput)
                    .build();
            
        } catch (Exception e) {
            log.error("工作流执行失败", e);
            
            webSocketHandler.sendMessage(executionId,
                ExecutionEvent.executionFailed(executionId, e.getMessage()));
            
            return ExecutionResult.builder()
                    .success(false)
                    .error(e.getMessage())
                    .build();
        }
    }

    /**
     * 拓扑排序（简单实现，按依赖关系排序）
     */
    private List<JSONObject> topologicalSort(JSONArray nodes, JSONArray edges) {
        // 构建邻接表和入度表
        Map<String, List<String>> graph = new HashMap<>();
        Map<String, Integer> inDegree = new HashMap<>();
        Map<String, JSONObject> nodeMap = new HashMap<>();
        
        for (int i = 0; i < nodes.size(); i++) {
            JSONObject node = nodes.getJSONObject(i);
            String nodeId = node.getStr("id");
            nodeMap.put(nodeId, node);
            graph.put(nodeId, new ArrayList<>());
            inDegree.put(nodeId, 0);
        }
        
        for (int i = 0; i < edges.size(); i++) {
            JSONObject edge = edges.getJSONObject(i);
            String source = edge.getStr("source");
            String target = edge.getStr("target");
            
            graph.get(source).add(target);
            inDegree.put(target, inDegree.get(target) + 1);
        }
        
        // Kahn 算法
        Queue<String> queue = new LinkedList<>();
        for (Map.Entry<String, Integer> entry : inDegree.entrySet()) {
            if (entry.getValue() == 0) {
                queue.offer(entry.getKey());
            }
        }
        
        List<JSONObject> result = new ArrayList<>();
        while (!queue.isEmpty()) {
            String nodeId = queue.poll();
            result.add(nodeMap.get(nodeId));
            
            for (String neighbor : graph.get(nodeId)) {
                inDegree.put(neighbor, inDegree.get(neighbor) - 1);
                if (inDegree.get(neighbor) == 0) {
                    queue.offer(neighbor);
                }
            }
        }
        
        if (result.size() != nodes.size()) {
            throw new RuntimeException("工作流存在循环依赖");
        }
        
        return result;
    }

    /**
     * 执行结果
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class ExecutionResult {
        private boolean success;
        private Object output;
        private String error;
    }
}
