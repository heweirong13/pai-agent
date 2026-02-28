package com.paiagent.llm;

import com.paiagent.common.BusinessException;
import com.paiagent.common.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * LLM 供应商工厂
 */
@Slf4j
@Component
public class LLMProviderFactory {

    @Value("${llm.deepseek.api-key:}")
    private String deepseekApiKey;

    @Value("${llm.deepseek.base-url:https://api.deepseek.com/v1}")
    private String deepseekBaseUrl;

    @Value("${llm.qwen.api-key:}")
    private String qwenApiKey;

    @Value("${llm.qwen.base-url:https://dashscope.aliyuncs.com/compatible-mode/v1}")
    private String qwenBaseUrl;

    @Value("${llm.openai.api-key:}")
    private String openaiApiKey;

    @Value("${llm.openai.base-url:https://api.openai.com/v1}")
    private String openaiBaseUrl;

    @Value("${llm.glm.api-key:}")
    private String glmApiKey;

    @Value("${llm.glm.base-url:https://open.bigmodel.cn/api/paas/v4}")
    private String glmBaseUrl;

    private final Map<String, LLMProvider> providers = new HashMap<>();

    @PostConstruct
    public void init() {
        // 初始化 DeepSeek
        if (deepseekApiKey != null && !deepseekApiKey.isEmpty()) {
            providers.put("DEEPSEEK", new DeepSeekProvider(deepseekApiKey, deepseekBaseUrl));
            log.info("已加载 DeepSeek 大模型适配器");
        }

        // 初始化通义千问
        if (qwenApiKey != null && !qwenApiKey.isEmpty()) {
            providers.put("QWEN", new QwenProvider(qwenApiKey, qwenBaseUrl));
            log.info("已加载通义千问大模型适配器");
        }

        // 初始化 OpenAI
        if (openaiApiKey != null && !openaiApiKey.isEmpty()) {
            providers.put("OPENAI", new OpenAIProvider(openaiApiKey, openaiBaseUrl));
            log.info("已加载 OpenAI 大模型适配器");
        }

        // 初始化智谱 GLM
        if (glmApiKey != null && !glmApiKey.isEmpty()) {
            providers.put("GLM", new GLMProvider(glmApiKey, glmBaseUrl));
            log.info("已加载智谱 GLM 大模型适配器");
        }

        log.info("共加载 {} 个大模型适配器", providers.size());
    }

    /**
     * 获取指定供应商的适配器
     */
    public LLMProvider getProvider(String providerName) {
        LLMProvider provider = providers.get(providerName.toUpperCase());
        if (provider == null) {
            throw new BusinessException(ErrorCode.LLM_PROVIDER_NOT_FOUND, "供应商: " + providerName);
        }
        return provider;
    }

    /**
     * 获取所有可用的供应商
     */
    public Map<String, LLMProvider> getAllProviders() {
        return new HashMap<>(providers);
    }

    /**
     * 检查供应商是否可用
     */
    public boolean isProviderAvailable(String providerName) {
        return providers.containsKey(providerName.toUpperCase());
    }
}
