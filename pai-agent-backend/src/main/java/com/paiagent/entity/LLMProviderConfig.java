package com.paiagent.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 大模型配置实体
 */
@Data
@TableName("llm_provider_config")
public class LLMProviderConfig {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 供应商名称: DEEPSEEK, QWEN, OPENAI, GLM
     */
    private String providerName;

    /**
     * 显示名称
     */
    private String displayName;

    /**
     * API密钥（加密存储）
     */
    private String apiKey;

    /**
     * API端点
     */
    private String apiEndpoint;

    /**
     * 支持的模型列表（JSON格式）
     */
    private String modelList;

    /**
     * 是否启用
     */
    private Boolean isEnabled;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
}
