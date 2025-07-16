package com.urban.carbon.api.upload.request;

import com.urban.carbon.api.upload.constants.SaveSoftType;
import com.urban.carbon.base.request.BaseRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UploadInitRequest extends BaseRequest {

    /**
     * 需要上传的文件名称（不需要完整路径）
     */
    @NotBlank
    private String fileId;

    /**
     * 需要上传的文件大小
     */
    @NotNull
    private Long fileSize;

    /**
     * 数据名称
     */
    @NotBlank
    private String dataName;

    /**
     * 数据的类型（文件后缀名）
     */
    private String dataType;

    /**
     * 希望保存到哪一个数据存储系统中
     */
    private SaveSoftType saveSoft;

    /**
     * 数据描述
     */
    private String dataDesc;

    /**
     * 创建上传初始化请求
     *
     * @param fileSize 文件大小
     * @param dataName 数据名称
     * @param dataType 数据类型
     * @param dataDesc 数据描述
     */
    public void createRequest(String fileId, Long fileSize, String dataName,
                              String dataType, String dataDesc) {
        this.fileId = fileId;
        this.fileSize = fileSize;
        this.dataName = dataName;
        this.dataType = dataType;
        this.dataDesc = dataDesc;
    }
}
