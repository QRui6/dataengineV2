package com.urban.carbon.admin;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDubbo
public class DataEngineAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataEngineAdminApplication.class, args);
    }
}
