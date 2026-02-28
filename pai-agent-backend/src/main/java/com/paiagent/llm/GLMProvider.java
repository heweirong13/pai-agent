package com.paiagent.llm;

import java.util.List;

/**
 * 智谱 GLM 大模型适配器
 */
public class GLMProvider extends OpenAICompatibleProvider {

    public GLMProvider(String apiKey) {
        super(apiKey, "https://open.bigmodel.cn/api/paas/v4",
              List.of("glm-4", "glm-4-flash", "glm-3-turbo"));
    }

    public GLMProvider(String apiKey, String baseUrl) {
        super(apiKey, baseUrl, List.of("glm-4", "glm-4-flash", "glm-3-turbo"));
    }

    @Override
    public String getProviderName() {
        return "GLM";
    }
}
