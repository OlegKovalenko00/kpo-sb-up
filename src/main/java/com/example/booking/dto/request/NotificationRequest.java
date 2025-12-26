package com.example.booking.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationRequest {
    @Email
    @NotBlank
    private String email;
    @NotBlank
    private String message;
}
