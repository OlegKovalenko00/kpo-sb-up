package com.example.booking.kafka;

import com.example.booking.kafka.events.BookingCanceledEvent;
import com.example.booking.kafka.events.RoomBookedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookingProducerService {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    @Value("${app.kafka.topics.room-booked:room-booked}")
    private String roomBookedTopic;
    @Value("${app.kafka.topics.booking-canceled:booking-canceled}")
    private String bookingCanceledTopic;

    public void publishRoomBooked(RoomBookedEvent event) {
        kafkaTemplate.send(roomBookedTopic, event);
    }

    public void publishBookingCanceled(BookingCanceledEvent event) {
        kafkaTemplate.send(bookingCanceledTopic, event);
    }
}
