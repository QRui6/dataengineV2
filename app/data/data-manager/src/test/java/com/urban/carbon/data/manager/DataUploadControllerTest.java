package com.urban.carbon.data.manager;

import com.urban.carbon.api.data.manager.request.DataCreateRequest;
import com.urban.carbon.api.data.manager.request.MergeRequest;
import com.urban.carbon.api.data.manager.request.UploadChunkRequest;
import com.urban.carbon.api.data.manager.response.data.DataInfo;
import com.urban.carbon.api.data.manager.response.data.UploadChunkInfo;
import com.urban.carbon.api.data.manager.response.data.UploadStatusInfo;
import com.urban.carbon.api.data.manager.service.DataFacadeService;
import com.urban.carbon.base.response.OperateResponse;
import com.urban.carbon.data.manager.infrastructure.utils.UploadProgressWebSocketHandler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DataEngineDataManagerApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("default")
public class DataUploadControllerTest {

    private static final Logger log = LoggerFactory.getLogger(DataUploadControllerTest.class);

    @Autowired
    private DataFacadeService dataFacadeService;

    /**
     * 上传进度 WebSocket 处理器
     */
    @Autowired
    private UploadProgressWebSocketHandler uploadProgressWebSocketHandler;

    @Test
    public void testInitCreateDataInfo() {
        String loginId = "45";
        DataCreateRequest request = new DataCreateRequest();
        String filename = "【Pr CS6 精简版】Adobe Premiere Pro CS6.zip";
        String dataDesc = "【Pr CS6 精简版】Adobe Premiere Pro CS6";
        String filePath = "X:\\wsz\\fileUploadTest\\【Pr CS6 精简版】Adobe Premiere Pro CS6.zip";
        File file = new File(filePath);
        long fileSize = file.length();
        String type = "zip";
        long datasourceId = 4;
        request.createRequest(filename, dataDesc, fileSize,
                type, datasourceId, Long.valueOf(loginId));
        OperateResponse<DataInfo> response = dataFacadeService.initCreateData(request);
        log.info("response data: {}", response.getData());
    }

    @Test
    public void testUploadChunk() {
        UploadChunkRequest request = new UploadChunkRequest();
        String uploadId = "xJ2U4cvVVq6gI9Ub.zip";
        Integer index = 0;
        String filePath = "X:\\wsz\\fileUploadTest\\【Pr CS6 精简版】Adobe Premiere Pro CS6.zip";
        File file = new File(filePath);
        InputStream is;
        long start = 0L;
        byte[] buffer = new byte[8 * 1024 * 1024];
        while (true) {
            try (RandomAccessFile inputStream = new RandomAccessFile(file, "r")) {
                inputStream.seek(start);
                int bytesRead = inputStream.read(buffer);
                if (bytesRead == -1) {
                    break;
                }
                is = new ByteArrayInputStream(buffer, 0, bytesRead);
                String chunkHash = calHash(buffer, 0, bytesRead);
                log.info("chunk index: {}, chunk hash: {}", index, chunkHash);
                request.createRequest(uploadId, index, is, chunkHash);
                request.setLoginId(Long.valueOf("45"));
                OperateResponse<UploadChunkInfo> response = dataFacadeService.uploadChunk(request);
                log.info("response data for upload chunk: {}", response.getData());
                index++;
                start += bytesRead;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Test
    public void testMergeChunks() {
        String uploadId = "xJ2U4cvVVq6gI9Ub.zip";
        MergeRequest request = new MergeRequest();
        request.setUploadId(uploadId);
        request.setLoginId(Long.valueOf("45"));
        OperateResponse<UploadStatusInfo> statusInfo = dataFacadeService.mergeChunks(request);
        log.info("response data: {}", statusInfo.getData());
    }

    @Test
    public void testCancelUpload() {
        String uploadId = "xJ2U4cvVVq6gI9Ub.zip";
        MergeRequest request = new MergeRequest();
        request.setUploadId(uploadId);
        request.setLoginId(Long.valueOf("45"));
        OperateResponse<DataInfo> response = dataFacadeService.cancelUpload(request);
        log.info("response data: {}", response.getData());
    }

    @Test
    public void testWebSocket() throws Exception {
        CompletableFuture<WebSocketSession> future = getWebSocketSessionCompletableFuture();

        WebSocketSession session = future.get(10, TimeUnit.SECONDS);
        assertNotNull(session);

        // 模拟上传切片触发进度更新
        UploadChunkRequest request = new UploadChunkRequest();
        String uploadId = "xJ2U4cvVVq6gI9Ub.zip";
        Integer index = 0;
        String filePath = "X:\\wsz\\fileUploadTest\\【Pr CS6 精简版】Adobe Premiere Pro CS6.zip";
        File file = new File(filePath);
        InputStream is;
        long start = 0L;
        byte[] buffer = new byte[8 * 1024 * 1024];
        while (true) {
            try (RandomAccessFile inputStream = new RandomAccessFile(file, "r")) {
                inputStream.seek(start);
                int bytesRead = inputStream.read(buffer);
                if (bytesRead == -1) {
                    break;
                }
                is = new ByteArrayInputStream(buffer, 0, bytesRead);
                String chunkHash = calHash(buffer, 0, bytesRead);
                request.createRequest(uploadId, index, is, chunkHash);
                request.setLoginId(Long.valueOf("45"));
                OperateResponse<UploadChunkInfo> response = dataFacadeService.uploadChunk(request);
                // 这里可以调用上传接口，触发 sendProgressUpdate
                uploadProgressWebSocketHandler.sendProgressUpdate(uploadId,
                        String.format("%02f", response.getData().getProgress()));
                index++;
                start += bytesRead;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        // 等待接收消息
        try {
            TimeUnit.SECONDS.sleep(30);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        session.close();
    }

    private CompletableFuture<WebSocketSession> getWebSocketSessionCompletableFuture() throws URISyntaxException {
        WebSocketClient client = new StandardWebSocketClient();
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        CompletableFuture<WebSocketSession> future = new CompletableFuture<>();
        client.execute(new TextWebSocketHandler() {
            @Override
            public void afterConnectionEstablished(WebSocketSession session) {
                future.complete(session);
            }

            @Override
            public void handleTextMessage(WebSocketSession session, TextMessage message) {
                log.info("Get Message: {}", message.getPayload());
            }
        }, headers, new URI("ws://127.0.0.1:8085/ws/upload/progress"));
        return future;
    }

    private static InputStream getInputStream() {
        String filePath = "X:\\wsz\\fileUploadTest\\【Pr CS6 精简版】Adobe Premiere Pro CS6.zip";
        File file = new File(filePath);
        InputStream is;
        try (RandomAccessFile inputStream = new RandomAccessFile(file, "r")) {
            inputStream.seek(0L);
            byte[] buffer = new byte[8 * 1024 * 1024];
            int bytesRead = inputStream.read(buffer);
            is = new ByteArrayInputStream(buffer, 0, bytesRead);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return is;
    }

    private String calHash(byte[] buffer, int start, int offset) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(buffer, start, offset);
            byte[] digest = md5.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void calAllHash() {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            String filePath = "X:\\wsz\\fileUploadTest\\【Pr CS6 精简版】Adobe Premiere Pro CS6.zip";
            File file = new File(filePath);
            long start = 0L;
            int index = 0;
            byte[] buffer = new byte[8 * 1024 * 1024];
            while (start < file.length()) {
                try (RandomAccessFile inputStream = new RandomAccessFile(file, "r")) {
                    inputStream.seek(start);
                    int bytesRead = inputStream.read(buffer);
                    md5.update(buffer, 0, bytesRead);
                    // md5 校验
                    byte[] digest = md5.digest();
                    StringBuilder sb = new StringBuilder();
                    for (byte b : digest) {
                        sb.append(String.format("%02x", b));
                    }
                    log.info("index: {}, hash: {}", index, sb);
                    start += bytesRead;
                    index++;
                    md5.reset();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
