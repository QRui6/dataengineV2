package com.urban.carbon.user;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDubbo
public class DataEnginePersonalApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataEnginePersonalApplication.class, args);
    }
}
