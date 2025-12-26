package com.example.booking.clients;

import com.example.booking.dto.request.NotificationRequest;
import com.example.booking.dto.response.NotificationResponse;

public interface NotificationClient {
    NotificationResponse send(NotificationRequest request);
}
