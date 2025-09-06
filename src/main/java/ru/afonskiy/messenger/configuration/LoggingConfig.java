package ru.afonskiy.messenger.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "logging.service")
public class LoggingConfig {
    String url;
}
