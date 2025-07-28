package com.urban.carbon.personal.infrastructure;

import com.urban.carbon.api.admin.service.UserManagerFacadeService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AccountDubboConfiguration {

    @DubboReference(version = "0.0.1")
    public UserManagerFacadeService userManagerFacadeService;

    @Bean
    @ConditionalOnMissingBean
    UserManagerFacadeService userManagerFacadeService() {
        return userManagerFacadeService;
    }
}
