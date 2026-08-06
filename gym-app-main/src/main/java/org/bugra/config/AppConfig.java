package org.bugra.config;


import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


@Configuration
@ComponentScan(basePackages = {"org.bugra"})
@Import({ StorageConfig.class, WebConfig.class, SecurityConfig.class})
public class AppConfig {
}
