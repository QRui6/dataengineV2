package com.urban.carbon.api.data.manager.request;

import com.urban.carbon.base.request.BaseRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MergeRequest extends BaseRequest {

    /**
     * 上传ID
     */
    @NotBlank
    private String uploadId;
}
