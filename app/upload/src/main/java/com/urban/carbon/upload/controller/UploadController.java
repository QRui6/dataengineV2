package com.urban.carbon.upload.controller;

import com.urban.carbon.api.upload.request.UploadChunkRequest;
import com.urban.carbon.api.upload.request.UploadInitRequest;
import com.urban.carbon.api.upload.response.data.UploadChunkInfo;
import com.urban.carbon.api.upload.response.data.UploadInitInfo;
import com.urban.carbon.api.upload.response.data.UploadStatusInfo;
import com.urban.carbon.base.utils.RandomNameGenerator;
import com.urban.carbon.upload.domain.service.FileService;
import com.urban.carbon.web.vo.Result;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;

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
            @RequestParam Long fileSize, @RequestParam String dataName,
            @RequestParam String dataType, @RequestParam String dataDesc) throws IOException {
        UploadInitRequest request = new UploadInitRequest();
        String fileId = RandomNameGenerator.generateRandomFileName(ID_LENGTH, dataType);
        request.createRequest(fileId, fileSize, dataName, dataType, dataDesc);
        return Result.success(fileService.initUpload(request));
    }

    @PostMapping("/chunk")
    public Result<UploadChunkInfo> chunkUpload(
            @RequestParam String fileId, @RequestParam Integer chunkIndex,
            @RequestBody MultipartFile file, @RequestParam String hashMD5)
            throws IOException {
        UploadChunkRequest request = new UploadChunkRequest();
        request.createRequest(fileId, chunkIndex, file.getInputStream(), hashMD5);
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

    /**
     * 流式下载文件接口
     * 支持大文件下载，避免本地磁盘压力。
     * 响应头设置为 application/octet-stream，并包含 Content-Disposition。
     *
     * @param fileId 文件唯一标识
     * @param response HttpServletResponse，用于写入文件流
     */
    @GetMapping("/download/{fileId}")
    public void downloadFile(@PathVariable String fileId, HttpServletResponse response) {
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileId);
        try (OutputStream os = response.getOutputStream()) {
            fileService.streamDownloadFile(fileId, os);
        } catch (IOException e) {
            throw new RuntimeException("文件下载失败", e);
        }
    }
}
