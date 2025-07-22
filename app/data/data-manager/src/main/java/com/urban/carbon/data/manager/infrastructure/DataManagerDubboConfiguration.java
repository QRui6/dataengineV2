package com.urban.carbon.data.manager.infrastructure;

import com.urban.carbon.api.data.source.service.DataSourceFacadeService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataManagerDubboConfiguration {

    @DubboReference(version = "1.0.0")
    private DataSourceFacadeService dataSourceFacadeService;

    @Bean
    @ConditionalOnMissingBean
    public DataSourceFacadeService dataSourceFacadeService() {
        return dataSourceFacadeService;
    }
}
