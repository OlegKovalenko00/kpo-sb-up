package com.example.booking.clients;

import com.example.booking.config.NotificationIntegrationProperty;
import com.example.booking.dto.request.NotificationRequest;
import com.example.booking.dto.response.NotificationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class NotificationClientImpl implements NotificationClient {
    private final RestTemplate restTemplate;
    private final NotificationIntegrationProperty properties;

    @Override
    public NotificationResponse send(NotificationRequest request) {
        try {
            ResponseEntity<NotificationResponse> response = restTemplate.postForEntity(properties.getBaseUrl() + "/notifications", request, NotificationResponse.class);
            NotificationResponse body = response.getBody();
            if (body != null) {
                return body;
            }
            return NotificationResponse.builder().delivered(true).details("No content").build();
        } catch (Exception ex) {
            return NotificationResponse.builder().delivered(false).details(ex.getMessage()).build();
        }
    }
}
