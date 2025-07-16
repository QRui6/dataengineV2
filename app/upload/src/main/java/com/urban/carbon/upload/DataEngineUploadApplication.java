package com.urban.carbon.upload;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDubbo
public class DataEngineUploadApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataEngineUploadApplication.class, args);
    }

}
