package org.bugra.config;


import org.bugra.filter.AuthFilter;
import org.bugra.service.AuthService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan(basePackages = {"org.bugra"})
@Import({ StorageConfig.class, WebConfig.class})
public class AppConfig {

    @Bean
    public AuthFilter authFilter(AuthService authService){
        AuthFilter authFilter = new AuthFilter();
        authFilter.setAuthService(authService);
        return authFilter;
    }
}
