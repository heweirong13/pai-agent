package com.paiagent.engine.executor;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.paiagent.engine.ExecutionContext;
import com.paiagent.service.TTSService;
import com.paiagent.websocket.ExecutionEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * TTS 语音合成节点执行器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TTSNodeExecutor implements NodeExecutor {

    private final TTSService ttsService;

    @Override
    public String getNodeType() {
        return "TTS";
    }

    @Override
    public ExecutionResult execute(ExecutionContext context, NodeConfig config) {
        log.info("执行 TTS 节点: {}", config.getNodeId());
        
        try {
            // 解析节点配置
            JSONObject configJson = JSONUtil.parseObj(config.getConfigJson());
            String voice = configJson.getStr("voice", "zhixiaoxia");
            Double speed = configJson.getDouble("speed", 1.0);
            Integer volume = configJson.getInt("volume", 50);
            String format = configJson.getStr("format", "mp3");
            String inputVariable = configJson.getStr("inputVariable", "llmOutput");
            
            // 获取输入文本
            String text = resolveVariable(context, inputVariable);
            if (text == null || text.isEmpty()) {
                // 尝试从 llmOutput 获取
                text = context.getStringVariable("llmOutput");
            }
            
            if (text == null || text.isEmpty()) {
                return ExecutionResult.builder()
                        .success(false)
                        .error("TTS 输入文本为空")
                        .build();
            }
            
            // 调用 TTS 服务
            TTSService.TTSResult result = ttsService.synthesize(text, voice, speed, volume, format);
            
            // 存储输出到上下文
            context.setVariable("output_" + config.getNodeId(), result.getFileUrl());
            context.setVariable("audioUrl", result.getFileUrl());
            
            // 通过 WebSocket 发送音频生成完成事件
            if (context.getWebSocketHandler() != null) {
                context.getWebSocketHandler().sendMessage(
                        context.getExecutionId(),
                        ExecutionEvent.audioGenerated(context.getExecutionId(), config.getNodeId(), result.getFileUrl())
                );
            }
            
            return ExecutionResult.builder()
                    .success(true)
                    .output(result)
                    .build();
                    
        } catch (Exception e) {
            log.error("TTS 节点执行失败", e);
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
        
        String varName = variableRef.replace("{{", "").replace("}}", "").trim();
        Object value = context.getVariable(varName);
        return value != null ? value.toString() : null;
    }
}
