package com.urban.carbon.data.manager.controller;

// import cn.dev33.satoken.stp.StpUtil;
import com.urban.carbon.api.data.manager.request.DataCreateRequest;
import com.urban.carbon.api.data.manager.request.MergeRequest;
import com.urban.carbon.api.data.manager.request.UploadChunkRequest;
import com.urban.carbon.api.data.manager.response.data.DataInfo;
import com.urban.carbon.api.data.manager.response.data.UploadChunkInfo;
import com.urban.carbon.api.data.manager.response.data.UploadStatusInfo;
import com.urban.carbon.api.data.manager.service.DataFacadeService;
import com.urban.carbon.base.response.OperateResponse;
import com.urban.carbon.base.response.QueryResponse;
import com.urban.carbon.data.manager.params.DataCreateParams;
import com.urban.carbon.web.vo.Result;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/data")
public class DataManagerController {

    private final DataFacadeService dataFacadeService;

    public DataManagerController(DataFacadeService dataFacadeService) {
        this.dataFacadeService = dataFacadeService;
    }

    @GetMapping("/test")
    public String test() {
        return "test";
    }

    @PostMapping("/upload/init")
    public Result<DataInfo> initCreateDataInfo(@RequestBody DataCreateParams params) {
//        String loginId = (String) StpUtil.getLoginId();
        String loginId = "45";
        DataCreateRequest request = new DataCreateRequest();
        request.createRequest(params.getFilename(), params.getDataDesc(), params.getSize(),
                params.getType(), params.getDataSourceId(), Long.valueOf(loginId));
        OperateResponse<DataInfo> response = dataFacadeService.initCreateData(request);
        return Result.success(response.getData());
    }

    @PostMapping("/upload/chunk")
    public Result<UploadChunkInfo> uploadChunk(
            @RequestParam("file") MultipartFile file, @RequestParam String chunkHash,
            @RequestParam Integer index, @RequestParam String uploadId) throws IOException {
        UploadChunkRequest request = new UploadChunkRequest();
        request.createRequest(uploadId, index, file.getInputStream(), chunkHash);
        request.setLoginId(Long.valueOf("45"));
        OperateResponse<UploadChunkInfo> response = dataFacadeService.uploadChunk(request);
        return Result.success(response.getData());
    }

    @PostMapping("/upload/merge")
    public Result<UploadStatusInfo> mergeChunks(@NotBlank String uploadId) {
        MergeRequest request = new MergeRequest();
        request.setUploadId(uploadId);
        request.setLoginId(Long.valueOf("45"));
        OperateResponse<UploadStatusInfo> statusInfo = dataFacadeService.mergeChunks(request);
        return Result.success(statusInfo.getData());
    }

    @GetMapping("/upload/status")
    public Result<UploadStatusInfo> getUploadStatus(@NotBlank String uploadId) {
        MergeRequest request = new MergeRequest();
        request.setUploadId(uploadId);
        request.setLoginId(Long.valueOf("45"));
        QueryResponse<UploadStatusInfo> response = dataFacadeService.getUploadStatus(request);
        return Result.success(response.getData());
    }

    @PostMapping("/upload/cancel")
    public Result<DataInfo> cancelUpload(@NotBlank String uploadId) {
        MergeRequest request = new MergeRequest();
        request.setUploadId(uploadId);
        request.setLoginId(Long.valueOf("45"));
        OperateResponse<DataInfo> response = dataFacadeService.cancelUpload(request);
        return Result.success(response.getData());
    }

}
