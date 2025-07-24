package com.urban.carbon.data.manager.infrastructure.utils;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
public class UploadProgressWebSocketHandler extends TextWebSocketHandler {

    private final Set<WebSocketSession> sessions = new CopyOnWriteArraySet<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
    }

    /**
     * 发送上传进度更新通知
     * 该方法遍历所有会话，并向每个打开的会话发送上传进度信息
     *
     * @param uploadId 上传的唯一标识符，用于跟踪特定的上传过程
     * @param progress 当前的上传进度，表示完成的百分比
     */
    public void sendProgressUpdate(String uploadId, String progress) {
        // 构造上传进度信息的JSON字符串
        String message = String.format("{\"uploadId\":\"%s\",\"progress\":%s}",
                uploadId, progress);
        // 遍历所有会话，向每个会话发送进度更新消息
        sessions.forEach(session -> {
            try {
                // 检查会话是否处于打开状态
                if (session.isOpen()) {
                    // 发送包含进度信息的文本消息
                    session.sendMessage(new TextMessage(message));
                }
            } catch (IOException e) {
                // 如果在发送消息过程中发生IO异常，则包装并重新抛出异常
                throw new RuntimeException(e);
            }
        });
    }

}
