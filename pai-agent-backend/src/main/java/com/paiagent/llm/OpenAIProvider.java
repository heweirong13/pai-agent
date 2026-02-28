package com.paiagent.llm;

import java.util.List;

/**
 * OpenAI 大模型适配器
 */
public class OpenAIProvider extends OpenAICompatibleProvider {

    public OpenAIProvider(String apiKey) {
        super(apiKey, "https://api.openai.com/v1",
              List.of("gpt-3.5-turbo", "gpt-4", "gpt-4-turbo"));
    }

    public OpenAIProvider(String apiKey, String baseUrl) {
        super(apiKey, baseUrl, List.of("gpt-3.5-turbo", "gpt-4", "gpt-4-turbo"));
    }

    @Override
    public String getProviderName() {
        return "OPENAI";
    }
}
