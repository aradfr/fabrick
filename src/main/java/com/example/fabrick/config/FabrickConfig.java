package com.example.fabrick.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.ZoneId;

@Configuration
@ConfigurationProperties(prefix = "fabrick.api")
@Data
public class FabrickConfig {
    private String baseUrl;
    private String authSchema;
    private String apiKey;
    private Long accountId;
    private ZoneId timeZone;
}