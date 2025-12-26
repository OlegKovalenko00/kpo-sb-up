package com.example.booking.observers;

import com.example.booking.builders.ReportBuilder;
import com.example.booking.domains.Booking;
import com.example.booking.domains.Room;
import com.example.booking.domains.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReportBookingObserver implements BookingObserver {
    private final ReportBuilder reportBuilder;

    @Override
    public void onBooking(User user, Room room, Booking booking) {
        String userName = user != null ? user.getName() : "unknown-user";
        String roomName = room != null ? room.getName() : "unknown-room";
        String status = booking.getStatus() != null ? booking.getStatus().name() : "UNKNOWN";
        String value = "Booking " + booking.getId() + " " + status + " by " + userName + " for " + roomName;
        reportBuilder.addOperation(value);
    }
}
