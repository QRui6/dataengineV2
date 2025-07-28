package com.urban.carbon.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.urban.carbon.gateway")
public class DataEngineGateWayApplication {
    public static void main(String[] args) {
        SpringApplication.run(DataEngineGateWayApplication.class, args);
    }
}
