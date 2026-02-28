package com.paiagent.llm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * LLM 对话请求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequest {

    /**
     * 模型名称
     */
    private String model;

    /**
     * 对话消息列表
     */
    private List<ChatMessage> messages;

    /**
     * 温度参数 (0-2)
     */
    @Builder.Default
    private Double temperature = 0.7;

    /**
     * 最大生成 Token 数
     */
    @Builder.Default
    private Integer maxTokens = 2000;

    /**
     * 是否流式输出
     */
    @Builder.Default
    private Boolean stream = false;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatMessage {
        /**
         * 角色: system, user, assistant
         */
        private String role;
        
        /**
         * 内容
         */
        private String content;
    }
}
