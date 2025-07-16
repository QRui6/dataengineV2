package com.urban.carbon.file.configuration;

import com.urban.carbon.file.configuration.properties.FileProperties;
import com.urban.carbon.file.strategy.FileStrategy;
import com.urban.carbon.file.strategy.FileStrategyFactory;
import com.urban.carbon.file.strategy.HDFSFileStrategy;
import com.urban.carbon.file.strategy.MinioFileStrategy;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(FileProperties.class)
public class FileConfiguration {

    @Bean
    @ConditionalOnMissingBean
    HDFSFileStrategy hdfsFileStrategy(FileProperties fileProperties) {
        return new HDFSFileStrategy(fileProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    MinioFileStrategy minioFileStrategy(FileProperties fileProperties) {
        return new MinioFileStrategy(fileProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    FileStrategyFactory fileStrategyFactory(FileStrategy ...fileStrategies) {
        return new FileStrategyFactory(fileStrategies);
    }
}
