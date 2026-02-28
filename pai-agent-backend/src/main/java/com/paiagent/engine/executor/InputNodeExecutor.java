package com.paiagent.engine.executor;

import com.paiagent.engine.ExecutionContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 输入节点执行器
 */
@Slf4j
@Component
public class InputNodeExecutor implements NodeExecutor {

    @Override
    public String getNodeType() {
        return "INPUT";
    }

    @Override
    public ExecutionResult execute(ExecutionContext context, NodeConfig config) {
        log.info("执行输入节点: {}", config.getNodeId());
        
        // 输入节点直接从上下文获取用户输入
        Object userInput = context.getVariable("userInput");
        
        if (userInput == null) {
            return ExecutionResult.builder()
                    .success(false)
                    .error("用户输入为空")
                    .build();
        }
        
        // 将输入存储到上下文，供后续节点使用
        context.setVariable("input_" + config.getNodeId(), userInput);
        
        return ExecutionResult.builder()
                .success(true)
                .output(userInput)
                .build();
    }
}
