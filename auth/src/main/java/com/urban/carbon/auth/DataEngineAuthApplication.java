package com.urban.carbon.auth;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDubbo
public class DataEngineAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataEngineAuthApplication.class, args);
    }
}
