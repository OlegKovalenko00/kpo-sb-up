package com.example.booking.services.bookings;

import com.example.booking.clients.NotificationClient;
import com.example.booking.domains.Booking;
import com.example.booking.domains.Room;
import com.example.booking.domains.User;
import com.example.booking.dto.request.BookingRequest;
import com.example.booking.dto.request.NotificationRequest;
import com.example.booking.enums.BookingStatus;
import com.example.booking.exception.BookingException;
import com.example.booking.kafka.BookingProducerService;
import com.example.booking.kafka.events.BookingCanceledEvent;
import com.example.booking.kafka.events.RoomBookedEvent;
import com.example.booking.observers.BookingOperation;
import com.example.booking.repositories.BookingRepository;
import com.example.booking.services.rooms.RoomService;
import com.example.booking.services.users.UserService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final RoomService roomService;
    private final UserService userService;
    private final NotificationClient notificationClient;
    private final BookingProducerService bookingProducerService;

    @Transactional
    @BookingOperation
    public Booking create(BookingRequest request) {
        Room room = roomService.getById(request.getRoomId());
        User user = userService.getById(request.getUserId());
        if (!room.isAvailable()) {
            throw new BookingException("Room is unavailable");
        }
        if (!user.isActive()) {
            throw new BookingException("User is inactive");
        }
        validateSchedule(request.getStartTime(), request.getEndTime());
        Booking booking = Booking.builder()
                .room(room)
                .user(user)
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .status(BookingStatus.CREATED)
                .build();
        Booking saved = bookingRepository.save(booking);
        room.setAvailable(false);
        bookingProducerService.publishRoomBooked(RoomBookedEvent.builder()
                .bookingId(saved.getId())
                .roomId(room.getId())
                .userId(user.getId())
                .startTime(saved.getStartTime())
                .endTime(saved.getEndTime())
                .build());
        notificationClient.send(NotificationRequest.builder()
                .email(user.getEmail())
                .message("Room booked: " + room.getName())
                .build());
        return saved;
    }

    @Transactional
    @BookingOperation
    public Booking cancel(Long id) {
        Booking booking = getById(id);
        if (booking.getStatus() == BookingStatus.CANCELED) {
            throw new BookingException("Booking already canceled");
        }
        booking.setStatus(BookingStatus.CANCELED);
        Room room = booking.getRoom();
        room.setAvailable(true);
        bookingProducerService.publishBookingCanceled(BookingCanceledEvent.builder()
                .bookingId(booking.getId())
                .roomId(room.getId())
                .userId(booking.getUser().getId())
                .canceledAt(LocalDateTime.now())
                .build());
        return bookingRepository.save(booking);
    }

    @Transactional
    public Booking update(Long id, BookingRequest request) {
        Booking booking = getById(id);
        Room room = roomService.getById(request.getRoomId());
        User user = userService.getById(request.getUserId());
        if (!room.isAvailable() && !room.getId().equals(booking.getRoom().getId())) {
            throw new BookingException("Room is unavailable");
        }
        if (!user.isActive()) {
            throw new BookingException("User is inactive");
        }
        validateSchedule(request.getStartTime(), request.getEndTime());
        Room previousRoom = booking.getRoom();
        if (!previousRoom.getId().equals(room.getId())) {
            previousRoom.setAvailable(true);
            room.setAvailable(false);
        }
        booking.setRoom(room);
        booking.setUser(user);
        booking.setStartTime(request.getStartTime());
        booking.setEndTime(request.getEndTime());
        return bookingRepository.save(booking);
    }

    @Transactional
    public void delete(Long id) {
        Booking booking = getById(id);
        booking.getRoom().setAvailable(true);
        bookingRepository.delete(booking);
    }

    public Booking getById(Long id) {
        return bookingRepository.findById(id).orElseThrow(() -> new BookingException("Booking not found"));
    }

    public List<Booking> getAll() {
        return bookingRepository.findAll();
    }

    private void validateSchedule(LocalDateTime start, LocalDateTime end) {
        if (end.isBefore(start) || end.isEqual(start)) {
            throw new BookingException("End time must be after start time");
        }
    }
}
