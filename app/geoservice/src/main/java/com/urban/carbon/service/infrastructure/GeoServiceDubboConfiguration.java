package com.urban.carbon.service.infrastructure;

import com.urban.carbon.api.data.manager.service.DataFacadeService;
import com.urban.carbon.api.geoservice.service.GeoServiceFacadeService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GeoServiceDubboConfiguration {

    @DubboReference(version = "1.0.0")
    private GeoServiceFacadeService geoServiceFacadeService;

    @DubboReference(version = "1.0.0")
    private DataFacadeService dataFacadeService;

    @Bean
    @ConditionalOnMissingBean
    GeoServiceFacadeService geoServiceFacadeService() {
        return geoServiceFacadeService;
    }

    @Bean
    @ConditionalOnMissingBean
    DataFacadeService dataFacadeService() {
        return dataFacadeService;
    }
}
