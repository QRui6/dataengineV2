package com.urban.carbon.admin;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 启动类
 *
 * @author XuGaoran
 * @since 0.0.1
 */
@SpringBootApplication
@EnableDubbo
public class DataEngineAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataEngineAdminApplication.class, args);
    }
}
