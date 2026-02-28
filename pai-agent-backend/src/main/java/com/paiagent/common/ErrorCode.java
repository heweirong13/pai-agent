package com.paiagent.common;

import lombok.Getter;

/**
 * 错误码枚举
 */
@Getter
public enum ErrorCode {
    
    // 通用错误码
    SUCCESS(200, "操作成功"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),
    INTERNAL_ERROR(500, "服务器内部错误"),
    
    // 工作流相关错误码 (1xxx)
    WORKFLOW_NOT_FOUND(1001, "工作流不存在"),
    WORKFLOW_SAVE_ERROR(1002, "工作流保存失败"),
    WORKFLOW_INVALID_GRAPH(1003, "工作流图结构无效"),
    WORKFLOW_HAS_CYCLE(1004, "工作流存在循环依赖"),
    
    // 节点相关错误码 (2xxx)
    NODE_NOT_FOUND(2001, "节点不存在"),
    NODE_CONFIG_INVALID(2002, "节点配置无效"),
    NODE_TYPE_NOT_SUPPORTED(2003, "节点类型不支持"),
    
    // 执行相关错误码 (3xxx)
    EXECUTION_NOT_FOUND(3001, "执行记录不存在"),
    EXECUTION_FAILED(3002, "执行失败"),
    EXECUTION_TIMEOUT(3003, "执行超时"),
    EXECUTION_CANCELLED(3004, "执行已取消"),
    
    // LLM相关错误码 (4xxx)
    LLM_PROVIDER_NOT_FOUND(4001, "大模型供应商不存在"),
    LLM_API_ERROR(4002, "大模型API调用失败"),
    LLM_API_KEY_INVALID(4003, "大模型API密钥无效"),
    LLM_RATE_LIMIT(4004, "大模型API请求频率超限"),
    
    // TTS相关错误码 (5xxx)
    TTS_SYNTHESIS_ERROR(5001, "语音合成失败"),
    TTS_CONFIG_INVALID(5002, "TTS配置无效"),
    
    // 文件相关错误码 (6xxx)
    FILE_UPLOAD_ERROR(6001, "文件上传失败"),
    FILE_NOT_FOUND(6002, "文件不存在"),
    FILE_TOO_LARGE(6003, "文件过大");

    private final Integer code;
    private final String message;

    ErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
