package com.example.booking.kafka.events;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookingCanceledEvent {
    private Long bookingId;
    private Long roomId;
    private Long userId;
    private LocalDateTime canceledAt;
}
