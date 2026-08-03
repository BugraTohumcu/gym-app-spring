package org.bugra.config;


import org.bugra.filter.AuthFilter;
import org.bugra.security.JwtTokenProvider;
import org.bugra.service.AuthService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
@ComponentScan(basePackages = {"org.bugra"})
@Import({ StorageConfig.class, WebConfig.class, SecurityConfig.class})
public class AppConfig {
}
