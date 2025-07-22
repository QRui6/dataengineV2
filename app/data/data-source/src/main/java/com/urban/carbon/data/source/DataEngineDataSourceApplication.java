package com.urban.carbon.data.source;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDubbo
public class DataEngineDataSourceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataEngineDataSourceApplication.class, args);
    }
}
