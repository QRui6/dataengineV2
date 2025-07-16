package com.urban.carbon.file.configuration.properties;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ToString
@ConfigurationProperties(prefix = FileProperties.PREFIX)
public class FileProperties {

    /**
     * 前缀
     */
    public static final String PREFIX = "spring.file.strategy";

    /**
     * HDFS 配置信息
     */
    private HdfsProperties hdfsFileSystem;

    /**
     * Minio 配置信息
     */
    private MinioProperties minioFileSystem;

    /**
     * 存储到分布式文件与存储在普通文件系统的分界点
     */
    private String threshold;

    /**
     * 分片大小
     */
    private String sliceSize;

    /**
     * 文件上传总大小
     */
    private String totalFileMaxSize;
}

