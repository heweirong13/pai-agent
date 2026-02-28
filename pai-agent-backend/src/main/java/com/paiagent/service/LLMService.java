package com.paiagent.service;

import com.paiagent.llm.ChatRequest;
import com.paiagent.llm.LLMProvider;
import com.paiagent.llm.LLMProviderFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * LLM 服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LLMService {

    private final LLMProviderFactory providerFactory;

    /**
     * 同步对话
     */
    public String chat(String provider, String model, List<ChatRequest.ChatMessage> messages,
                       Double temperature, Integer maxTokens) {
        LLMProvider llmProvider = providerFactory.getProvider(provider);
        
        ChatRequest request = ChatRequest.builder()
                .model(model)
                .messages(messages)
                .temperature(temperature != null ? temperature : 0.7)
                .maxTokens(maxTokens != null ? maxTokens : 2000)
                .build();
        
        log.info("调用 {} 模型 {}", provider, model);
        return llmProvider.chat(request);
    }

    /**
     * 流式对话
     */
    public void streamChat(String provider, String model, List<ChatRequest.ChatMessage> messages,
                           Double temperature, Integer maxTokens,
                           Consumer<String> onMessage, Consumer<String> onComplete, Consumer<Exception> onError) {
        LLMProvider llmProvider = providerFactory.getProvider(provider);
        
        ChatRequest request = ChatRequest.builder()
                .model(model)
                .messages(messages)
                .temperature(temperature != null ? temperature : 0.7)
                .maxTokens(maxTokens != null ? maxTokens : 2000)
                .stream(true)
                .build();
        
        log.info("流式调用 {} 模型 {}", provider, model);
        llmProvider.streamChat(request, onMessage, onComplete, onError);
    }

    /**
     * 获取所有可用的供应商和模型
     */
    public Map<String, List<String>> getAvailableProviders() {
        return providerFactory.getAllProviders().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().getModels()
                ));
    }
}
