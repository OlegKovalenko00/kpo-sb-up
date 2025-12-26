package com.example.booking.observers;

import com.example.booking.domains.Booking;
import com.example.booking.domains.Room;
import com.example.booking.domains.User;

public interface BookingObserver {
    void onBooking(User user, Room room, Booking booking);
}
