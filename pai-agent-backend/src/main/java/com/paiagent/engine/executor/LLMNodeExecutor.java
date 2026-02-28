package com.paiagent.engine.executor;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.paiagent.engine.ExecutionContext;
import com.paiagent.llm.ChatRequest;
import com.paiagent.service.LLMService;
import com.paiagent.websocket.ExecutionEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * LLM 大模型节点执行器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LLMNodeExecutor implements NodeExecutor {

    private final LLMService llmService;

    @Override
    public String getNodeType() {
        return "LLM";
    }

    @Override
    public ExecutionResult execute(ExecutionContext context, NodeConfig config) {
        log.info("执行 LLM 节点: {}", config.getNodeId());
        
        try {
            // 解析节点配置
            JSONObject configJson = JSONUtil.parseObj(config.getConfigJson());
            String provider = configJson.getStr("provider", "DEEPSEEK");
            String model = configJson.getStr("model", "deepseek-chat");
            String systemPrompt = configJson.getStr("systemPrompt", "");
            Double temperature = configJson.getDouble("temperature", 0.7);
            Integer maxTokens = configJson.getInt("maxTokens", 2000);
            String inputVariable = configJson.getStr("inputVariable", "userInput");
            
            // 获取输入
            String userInput = resolveVariable(context, inputVariable);
            if (userInput == null || userInput.isEmpty()) {
                return ExecutionResult.builder()
                        .success(false)
                        .error("LLM 输入为空")
                        .build();
            }
            
            // 构建消息
            List<ChatRequest.ChatMessage> messages = new ArrayList<>();
            if (systemPrompt != null && !systemPrompt.isEmpty()) {
                messages.add(ChatRequest.ChatMessage.builder()
                        .role("system")
                        .content(systemPrompt)
                        .build());
            }
            messages.add(ChatRequest.ChatMessage.builder()
                    .role("user")
                    .content(userInput)
                    .build());
            
            // 使用流式调用
            StringBuilder fullOutput = new StringBuilder();
            CountDownLatch latch = new CountDownLatch(1);
            AtomicReference<Exception> errorRef = new AtomicReference<>();
            
            llmService.streamChat(provider, model, messages, temperature, maxTokens,
                    // onMessage
                    (chunk) -> {
                        fullOutput.append(chunk);
                        // 通过 WebSocket 发送流式输出
                        if (context.getWebSocketHandler() != null) {
                            context.getWebSocketHandler().sendMessage(
                                    context.getExecutionId(),
                                    ExecutionEvent.nodeOutput(context.getExecutionId(), config.getNodeId(), chunk)
                            );
                        }
                    },
                    // onComplete
                    (result) -> {
                        latch.countDown();
                    },
                    // onError
                    (error) -> {
                        errorRef.set(error);
                        latch.countDown();
                    }
            );
            
            // 等待完成
            boolean completed = latch.await(120, TimeUnit.SECONDS);
            
            if (!completed) {
                return ExecutionResult.builder()
                        .success(false)
                        .error("LLM 调用超时")
                        .build();
            }
            
            if (errorRef.get() != null) {
                return ExecutionResult.builder()
                        .success(false)
                        .error(errorRef.get().getMessage())
                        .build();
            }
            
            String output = fullOutput.toString();
            
            // 存储输出到上下文
            context.setVariable("output_" + config.getNodeId(), output);
            context.setVariable("llmOutput", output);
            
            return ExecutionResult.builder()
                    .success(true)
                    .output(output)
                    .build();
                    
        } catch (Exception e) {
            log.error("LLM 节点执行失败", e);
            return ExecutionResult.builder()
                    .success(false)
                    .error(e.getMessage())
                    .build();
        }
    }
    
    /**
     * 解析变量引用
     */
    private String resolveVariable(ExecutionContext context, String variableRef) {
        if (variableRef == null) {
            return null;
        }
        
        // 移除 {{ }} 包裹
        String varName = variableRef.replace("{{", "").replace("}}", "").trim();
        
        Object value = context.getVariable(varName);
        return value != null ? value.toString() : null;
    }
}
