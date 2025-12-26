package com.example.booking.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "notification.integration")
@Data
public class NotificationIntegrationProperty {
    private String baseUrl;
}
