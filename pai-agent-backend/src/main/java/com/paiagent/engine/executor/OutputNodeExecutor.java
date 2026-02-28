package com.paiagent.engine.executor;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.paiagent.engine.ExecutionContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 输出节点执行器
 */
@Slf4j
@Component
public class OutputNodeExecutor implements NodeExecutor {

    @Override
    public String getNodeType() {
        return "OUTPUT";
    }

    @Override
    public ExecutionResult execute(ExecutionContext context, NodeConfig config) {
        log.info("执行输出节点: {}", config.getNodeId());
        
        try {
            // 解析节点配置
            JSONObject configJson = JSONUtil.parseObj(config.getConfigJson());
            String outputType = configJson.getStr("outputType", "both");
            
            // 收集输出数据
            Map<String, Object> output = new HashMap<>();
            
            // 文本输出
            String textOutput = context.getStringVariable("llmOutput");
            if (textOutput != null && ("text".equals(outputType) || "both".equals(outputType))) {
                output.put("text", textOutput);
            }
            
            // 音频输出
            String audioUrl = context.getStringVariable("audioUrl");
            if (audioUrl != null && ("audio".equals(outputType) || "both".equals(outputType))) {
                output.put("audioUrl", audioUrl);
            }
            
            // 存储最终输出
            context.setVariable("finalOutput", output);
            
            return ExecutionResult.builder()
                    .success(true)
                    .output(output)
                    .build();
                    
        } catch (Exception e) {
            log.error("输出节点执行失败", e);
            return ExecutionResult.builder()
                    .success(false)
                    .error(e.getMessage())
                    .build();
        }
    }
}
