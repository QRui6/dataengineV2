package com.urban.carbon.data.manager.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.urban.carbon.api.data.manager.request.DataCreateRequest;
import com.urban.carbon.api.data.manager.request.MergeRequest;
import com.urban.carbon.api.data.manager.request.UploadChunkRequest;
import com.urban.carbon.api.data.manager.response.data.DataInfo;
import com.urban.carbon.api.data.manager.response.data.UploadChunkInfo;
import com.urban.carbon.api.data.manager.response.data.UploadStatusInfo;
import com.urban.carbon.api.data.manager.service.DataFacadeService;
import com.urban.carbon.base.response.OperateResponse;
import com.urban.carbon.base.response.QueryResponse;
import com.urban.carbon.data.manager.infrastructure.utils.UploadProgressWebSocketHandler;
import com.urban.carbon.data.manager.params.DataCreateParams;
import com.urban.carbon.web.vo.Result;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;

@RestController
@RequestMapping("/api/data/upload")
public class DataUploadController {

    /**
     * 文件上传 Facade 接口
     */
    private final DataFacadeService dataFacadeService;

    /**
     * 上传进度 WebSocket 处理器
     */
    private final UploadProgressWebSocketHandler uploadProgressWebSocketHandler;

    /**
     * 构造函数
     *
     * @param dataFacadeService 文件上传 Facade 接口
     */
    public DataUploadController(DataFacadeService dataFacadeService,
                                UploadProgressWebSocketHandler uploadProgressWebSocketHandler) {
        this.dataFacadeService = dataFacadeService;
        this.uploadProgressWebSocketHandler = uploadProgressWebSocketHandler;
    }

    @GetMapping("/test")
    public String test() {
        return "test";
    }

    @PostMapping("/init")
    public Result<DataInfo> initCreateDataInfo(@RequestBody DataCreateParams params) {
        String loginId = (String) StpUtil.getLoginId();
        DataCreateRequest request = new DataCreateRequest();
        request.createRequest(params.getFilename(), params.getDataDesc(), params.getSize(),
                params.getType(), params.getDataSourceId(), Long.valueOf(loginId));
        OperateResponse<DataInfo> response = dataFacadeService.initCreateData(request);
        return Result.success(response.getData());
    }

    @PostMapping("/chunk")
    public Result<UploadChunkInfo> uploadChunk(
            @RequestParam("file") MultipartFile file, @RequestParam String chunkHash,
            @RequestParam Integer index, @RequestParam String uploadId) throws IOException {
        String loginId = (String) StpUtil.getLoginId();
        UploadChunkRequest request = new UploadChunkRequest();
        request.createRequest(uploadId, index, file.getInputStream(), chunkHash);
        request.setLoginId(Long.valueOf(loginId));
        // 调用更新接口
        OperateResponse<UploadChunkInfo> response = dataFacadeService.uploadChunk(request);
        UploadChunkInfo data = response.getData();
        // 发送上传进度更新
        uploadProgressWebSocketHandler.sendProgressUpdate(data.getFileId(),
                String.format("%02f", data.getProgress()));
        return Result.success(data);
    }

    @PostMapping("/merge")
    public Result<UploadStatusInfo> mergeChunks(@NotBlank String uploadId) {
        String loginId = (String) StpUtil.getLoginId();
        MergeRequest request = new MergeRequest();
        request.setUploadId(uploadId);
        request.setLoginId(Long.valueOf(loginId));
        OperateResponse<UploadStatusInfo> statusInfo = dataFacadeService.mergeChunks(request);
        return Result.success(statusInfo.getData());
    }

    @GetMapping("/status")
    public Result<UploadStatusInfo> getUploadStatus(@NotBlank String uploadId) {
        String loginId = (String) StpUtil.getLoginId();
        MergeRequest request = new MergeRequest();
        request.setUploadId(uploadId);
        request.setLoginId(Long.valueOf(loginId));
        QueryResponse<UploadStatusInfo> response = dataFacadeService.getUploadStatus(request);
        return Result.success(response.getData());
    }

    @PostMapping("/cancel")
    public Result<DataInfo> cancelUpload(@NotBlank String uploadId) {
        // TODO 并发上传时，点击取消，会产生脏数据，考虑如何进行修改。
        String loginId = (String) StpUtil.getLoginId();
        MergeRequest request = new MergeRequest();
        request.setUploadId(uploadId);
        request.setLoginId(Long.valueOf(loginId));
        OperateResponse<DataInfo> response = dataFacadeService.cancelUpload(request);
        return Result.success(response.getData());
    }

    @GetMapping("/download")
    public void downloadFile(@NotNull Long dataId, HttpServletResponse response) throws IOException {
        String loginId = (String) StpUtil.getLoginId();
        // 设置响应头
        response.setContentType("application/octet-stream");
        // 查询文件的名称与后缀名
        QueryResponse<DataInfo> resp = dataFacadeService.findById(dataId, Long.valueOf(loginId));
        DataInfo dataInfo = resp.getData();
        String fileName = dataInfo.getName();
        String dataType = dataInfo.getDataType();
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + "." + dataType);
        // 调用文件策略下载文件
        try (OutputStream outputStream = response.getOutputStream()) {
            dataFacadeService.downloadFile(dataInfo.getFilePath(), dataInfo.getSaveSoft(), outputStream);
        } catch (IOException e) {
            if (!response.isCommitted()) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "File download failed");
            }
            throw new RuntimeException(e);
        }
    }

}
