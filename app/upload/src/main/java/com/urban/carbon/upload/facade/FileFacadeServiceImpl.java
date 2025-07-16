package com.urban.carbon.upload.facade;

import com.urban.carbon.api.upload.request.UploadChunkRequest;
import com.urban.carbon.api.upload.request.UploadInitRequest;
import com.urban.carbon.api.upload.response.data.UploadChunkInfo;
import com.urban.carbon.api.upload.response.data.UploadInitInfo;
import com.urban.carbon.api.upload.response.data.UploadStatusInfo;
import com.urban.carbon.api.upload.service.FileFacadeService;
import com.urban.carbon.rpc.facade.Facade;
import com.urban.carbon.upload.domain.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;

import java.io.IOException;

@Slf4j
@DubboService(version = "1.0.0")
public class FileFacadeServiceImpl implements FileFacadeService {

    private final FileService fileService;

    public FileFacadeServiceImpl(FileService fileService) {
        this.fileService = fileService;
    }

    @Override
    public UploadInitInfo initUpload(UploadInitRequest request) throws IOException {
        return fileService.initUpload(request);
    }

    @Override
    @Facade
    public UploadChunkInfo handleChunkUpload(UploadChunkRequest request) {
        return fileService.handleChunkUpload(request);
    }

    @Override
    @Facade
    public UploadStatusInfo completeUpload(String fileId) {
        return fileService.completeUpload(fileId);
    }

    @Override
    public UploadStatusInfo getUploadStatus(String fileId) {
        return fileService.getUploadStatus(fileId);
    }

    @Override
    @Facade
    public UploadStatusInfo cancelUpload(String fileId) {
        return fileService.cancelUpload(fileId);
    }

    @Override
    @Facade
    public Boolean deleteUploadFile(String fileId, String saveSoftType, String filePath) {
        return fileService.deleteUploadFile(fileId, saveSoftType, filePath);
    }
}

