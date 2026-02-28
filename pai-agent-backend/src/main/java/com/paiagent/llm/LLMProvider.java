package com.paiagent.llm;

import java.util.List;
import java.util.function.Consumer;

/**
 * 大模型供应商接口
 */
public interface LLMProvider {

    /**
     * 获取供应商名称
     */
    String getProviderName();

    /**
     * 获取支持的模型列表
     */
    List<String> getModels();

    /**
     * 同步对话
     */
    String chat(ChatRequest request);

    /**
     * 流式对话
     */
    void streamChat(ChatRequest request, Consumer<String> onMessage, Consumer<String> onComplete, Consumer<Exception> onError);

    /**
     * 验证配置
     */
    boolean validateConfig();
}
