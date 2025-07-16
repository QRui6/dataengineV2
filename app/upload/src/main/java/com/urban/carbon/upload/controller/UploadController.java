package com.urban.carbon.upload.controller;

import com.urban.carbon.api.upload.request.UploadChunkRequest;
import com.urban.carbon.api.upload.request.UploadInitRequest;
import com.urban.carbon.api.upload.response.data.UploadChunkInfo;
import com.urban.carbon.api.upload.response.data.UploadInitInfo;
import com.urban.carbon.api.upload.response.data.UploadStatusInfo;
import com.urban.carbon.base.utils.RandomNameGenerator;
import com.urban.carbon.upload.domain.service.FileService;
import com.urban.carbon.web.vo.Result;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    private static final Integer ID_LENGTH = 32;

    private final FileService fileService;

    public UploadController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/init")
    public Result<UploadInitInfo> initUpload(
            Long fileSize, String dataName, String dataType,
            String dataDesc) throws IOException {
        UploadInitRequest request = new UploadInitRequest();
        String fileId = RandomNameGenerator.generateRandomFileName(
                ID_LENGTH, dataType);
        request.createRequest(fileId, fileSize, dataName, dataType, dataDesc);
        return Result.success(fileService.initUpload(request));
    }

    @PostMapping("/chunk")
    public Result<UploadChunkInfo> chunkUpload(
            @RequestParam String fileId, @RequestParam Long chunkIndex,
            @RequestBody MultipartFile file) throws IOException {
        UploadChunkRequest request = new UploadChunkRequest();
        request.createRequest(fileId, chunkIndex, file.getInputStream());
        return Result.success(fileService.handleChunkUpload(request));
    }

    @PostMapping("/complete")
    public Result<UploadStatusInfo> completeUpload(String fileId) {
        return Result.success(fileService.completeUpload(fileId));
    }

    @PostMapping("/progress")
    public Result<UploadStatusInfo> getUploadStatus(String fileId) {
        return Result.success(fileService.getUploadStatus(fileId));
    }

    @PostMapping("/cancel")
    public Result<UploadStatusInfo> cancelUpload(String fileId) {
        return Result.success(fileService.cancelUpload(fileId));
    }
}
