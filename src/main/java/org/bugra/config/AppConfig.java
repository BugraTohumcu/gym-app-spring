package org.bugra.config;


import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan(basePackages = {"org.bugra", "org.springdoc"})
@Import({ StorageConfig.class, WebConfig.class})
public class AppConfig {
}
