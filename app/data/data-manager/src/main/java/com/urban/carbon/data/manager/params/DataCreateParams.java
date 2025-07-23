package com.urban.carbon.data.manager.params;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DataCreateParams {

    /**
     * 文件名
     */
    private String filename;

    /**
     * 文件大小
     */
    private Long size;

    /**
     * 文件类型
     */
    private String type;

    /**
     * 数据源id
     */
    private Long dataSourceId;

    /**
     * 数据描述
     */
    private String dataDesc;

}
