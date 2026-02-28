package com.paiagent.llm;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * OpenAI 兼容格式的基础适配器
 * DeepSeek、通义千问、OpenAI、智谱都使用 OpenAI 兼容格式
 */
@Slf4j
public abstract class OpenAICompatibleProvider implements LLMProvider {

    protected final OkHttpClient httpClient;
    protected final String apiKey;
    protected final String baseUrl;
    protected final List<String> models;

    public OpenAICompatibleProvider(String apiKey, String baseUrl, List<String> models) {
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
        this.models = models;
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    @Override
    public List<String> getModels() {
        return models;
    }

    @Override
    public boolean validateConfig() {
        return StrUtil.isNotBlank(apiKey) && StrUtil.isNotBlank(baseUrl);
    }

    @Override
    public String chat(ChatRequest request) {
        String url = baseUrl + "/chat/completions";
        
        JSONObject body = buildRequestBody(request);
        body.set("stream", false);

        Request httpRequest = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .post(RequestBody.create(body.toString(), MediaType.parse("application/json")))
                .build();

        try (Response response = httpClient.newCall(httpRequest).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("LLM API 调用失败: " + response.code() + " " + response.message());
            }
            
            String responseBody = response.body().string();
            JSONObject json = JSONUtil.parseObj(responseBody);
            
            return json.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getStr("content");
        } catch (IOException e) {
            log.error("LLM API 调用异常", e);
            throw new RuntimeException("LLM API 调用异常: " + e.getMessage(), e);
        }
    }

    @Override
    public void streamChat(ChatRequest request, Consumer<String> onMessage, Consumer<String> onComplete, Consumer<Exception> onError) {
        String url = baseUrl + "/chat/completions";
        
        JSONObject body = buildRequestBody(request);
        body.set("stream", true);

        Request httpRequest = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .post(RequestBody.create(body.toString(), MediaType.parse("application/json")))
                .build();

        StringBuilder fullContent = new StringBuilder();

        EventSourceListener listener = new EventSourceListener() {
            @Override
            public void onEvent(EventSource eventSource, String id, String type, String data) {
                if ("[DONE]".equals(data)) {
                    onComplete.accept(fullContent.toString());
                    return;
                }
                
                try {
                    JSONObject json = JSONUtil.parseObj(data);
                    JSONObject delta = json.getJSONArray("choices")
                            .getJSONObject(0)
                            .getJSONObject("delta");
                    
                    String content = delta.getStr("content");
                    if (StrUtil.isNotEmpty(content)) {
                        fullContent.append(content);
                        onMessage.accept(content);
                    }
                } catch (Exception e) {
                    log.warn("解析流式响应失败: {}", data, e);
                }
            }

            @Override
            public void onFailure(EventSource eventSource, Throwable t, Response response) {
                log.error("LLM 流式调用失败", t);
                onError.accept(new RuntimeException("LLM 流式调用失败", t));
            }
        };

        EventSources.createFactory(httpClient)
                .newEventSource(httpRequest, listener);
    }

    private JSONObject buildRequestBody(ChatRequest request) {
        JSONObject body = new JSONObject();
        body.set("model", request.getModel());
        body.set("messages", request.getMessages());
        body.set("temperature", request.getTemperature());
        body.set("max_tokens", request.getMaxTokens());
        return body;
    }
}
