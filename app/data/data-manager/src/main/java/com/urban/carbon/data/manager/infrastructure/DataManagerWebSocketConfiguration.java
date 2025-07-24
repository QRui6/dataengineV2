package com.urban.carbon.data.manager.infrastructure;

import com.urban.carbon.data.manager.infrastructure.utils.UploadProgressWebSocketHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@Slf4j
@EnableWebSocket
public class DataManagerWebSocketConfiguration implements WebSocketConfigurer {

    /**
     * 上传进度WebSocket处理器
     */
    private final UploadProgressWebSocketHandler uploadProgressWebSocketHandler;

    /**
     * 构造函数
     *
     * @param uploadProgressWebSocketHandler 上传进度WebSocket处理器
     */
    public DataManagerWebSocketConfiguration(UploadProgressWebSocketHandler uploadProgressWebSocketHandler) {
        this.uploadProgressWebSocketHandler = uploadProgressWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        log.info("Registering WebSocket Handler...");
        registry.addHandler(uploadProgressWebSocketHandler, "/ws/upload/progress")
                .setAllowedOrigins("*");
        log.info("WebSocket Handler Registered.");
    }
}
