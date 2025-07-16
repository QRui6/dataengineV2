package com.urban.carbon.file.configuration.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MinioProperties {
    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 基础路径
     */
    private String basePath;

    /**
     * minio 主机地址
     */
    private String minioHost;

    /**
     * minio 端口
     */
    private Integer minioPort;

    /**
     * minio 用户名
     */
    private String minioUser;

    /**
     * minio 密码
     */
    private String minioPassword;

    @Override
    public String toString() {
        return "MinioFileConfiguration{" +
                ", basePath='" + basePath + "', " +
                ", minioHost='" + minioHost + "', " +
                ", minioPort='" + minioPort + "', " +
                ", minioUser='" + minioUser + "'" +
                '}';
    }

}
