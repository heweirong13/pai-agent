package com.paiagent.llm;

import java.util.List;

/**
 * DeepSeek 大模型适配器
 */
public class DeepSeekProvider extends OpenAICompatibleProvider {

    public DeepSeekProvider(String apiKey) {
        super(apiKey, "https://api.deepseek.com/v1", 
              List.of("deepseek-chat", "deepseek-coder"));
    }

    public DeepSeekProvider(String apiKey, String baseUrl) {
        super(apiKey, baseUrl, List.of("deepseek-chat", "deepseek-coder"));
    }

    @Override
    public String getProviderName() {
        return "DEEPSEEK";
    }
}
