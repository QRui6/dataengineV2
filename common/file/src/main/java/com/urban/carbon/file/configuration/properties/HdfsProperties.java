package com.urban.carbon.file.configuration.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HdfsProperties {

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 基础路径
     */
    private String basePath;

    /**
     * HDFS 地址
     */
    private String hdfsHost;

    /**
     * HDFS 端口
     */
    private String hdfsPort;

    /**
     * HDFS 用户
     */
    private String hdfsUser;

    @Override
    public String toString() {
        return "{\"hdfsFileSystem\":{" +
                "\"enabled\":" + enabled + "," +
                "\"basePath\":\"" + basePath + "\"," +
                "\"hdfsHost\":\"" + hdfsHost + "\"," +
                "\"hdfsPort\":\"" + hdfsPort + "\"," +
                "\"hdfsUser\":\"" + hdfsUser + "\"";
    }
}
