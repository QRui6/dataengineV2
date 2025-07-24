package com.urban.carbon.data.source.infrastructure;

import com.urban.carbon.api.data.manager.service.DataFacadeService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSourceDubboConfiguration {

    @DubboReference(version = "1.0.0")
    private DataFacadeService dataFacadeService;

    @Bean
    @ConditionalOnMissingBean
    public DataFacadeService dataFacadeService() {
        return dataFacadeService;
    }
}
