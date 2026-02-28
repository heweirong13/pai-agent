package com.paiagent.llm;

import java.util.List;

/**
 * 通义千问大模型适配器
 */
public class QwenProvider extends OpenAICompatibleProvider {

    public QwenProvider(String apiKey) {
        super(apiKey, "https://dashscope.aliyuncs.com/compatible-mode/v1",
              List.of("qwen-turbo", "qwen-plus", "qwen-max"));
    }

    public QwenProvider(String apiKey, String baseUrl) {
        super(apiKey, baseUrl, List.of("qwen-turbo", "qwen-plus", "qwen-max"));
    }

    @Override
    public String getProviderName() {
        return "QWEN";
    }
}
