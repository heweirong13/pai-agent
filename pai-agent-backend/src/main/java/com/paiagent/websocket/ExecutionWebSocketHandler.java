package com.paiagent.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 执行状态 WebSocket 处理器
 */
@Slf4j
@Component
public class ExecutionWebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;
    
    /**
     * 存储所有连接的会话，key为executionId
     */
    private final Map<String, Map<String, WebSocketSession>> executionSessions = new ConcurrentHashMap<>();

    public ExecutionWebSocketHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String executionId = extractExecutionId(session);
        if (executionId != null) {
            executionSessions.computeIfAbsent(executionId, k -> new ConcurrentHashMap<>())
                    .put(session.getId(), session);
            log.info("WebSocket连接建立: executionId={}, sessionId={}", executionId, session.getId());
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String executionId = extractExecutionId(session);
        if (executionId != null) {
            Map<String, WebSocketSession> sessions = executionSessions.get(executionId);
            if (sessions != null) {
                sessions.remove(session.getId());
                if (sessions.isEmpty()) {
                    executionSessions.remove(executionId);
                }
            }
            log.info("WebSocket连接关闭: executionId={}, sessionId={}", executionId, session.getId());
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 处理客户端发送的消息（如心跳）
        String payload = message.getPayload();
        log.debug("收到WebSocket消息: {}", payload);
        
        if ("ping".equals(payload)) {
            session.sendMessage(new TextMessage("pong"));
        }
    }

    /**
     * 向指定执行的所有连接发送消息
     */
    public void sendMessage(String executionId, ExecutionEvent event) {
        Map<String, WebSocketSession> sessions = executionSessions.get(executionId);
        if (sessions == null || sessions.isEmpty()) {
            log.debug("没有找到执行{}的WebSocket连接", executionId);
            return;
        }

        try {
            String message = objectMapper.writeValueAsString(event);
            TextMessage textMessage = new TextMessage(message);
            
            for (WebSocketSession session : sessions.values()) {
                if (session.isOpen()) {
                    try {
                        session.sendMessage(textMessage);
                    } catch (IOException e) {
                        log.error("发送WebSocket消息失败: sessionId={}", session.getId(), e);
                    }
                }
            }
        } catch (Exception e) {
            log.error("序列化WebSocket消息失败", e);
        }
    }

    /**
     * 从WebSocket URI中提取executionId
     */
    private String extractExecutionId(WebSocketSession session) {
        String path = session.getUri().getPath();
        // /ws/executions/{executionId}
        String[] parts = path.split("/");
        if (parts.length >= 4) {
            return parts[3];
        }
        return null;
    }

    /**
     * 检查是否有指定执行的连接
     */
    public boolean hasConnections(String executionId) {
        Map<String, WebSocketSession> sessions = executionSessions.get(executionId);
        return sessions != null && !sessions.isEmpty();
    }
}
