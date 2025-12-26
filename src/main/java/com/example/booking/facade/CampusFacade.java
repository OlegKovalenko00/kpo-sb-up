package com.example.booking.facade;

import com.example.booking.builders.ReportBuilder;
import com.example.booking.domains.Booking;
import com.example.booking.domains.ExternalRoom;
import com.example.booking.domains.ExternalRoomAdapter;
import com.example.booking.domains.Report;
import com.example.booking.domains.Room;
import com.example.booking.domains.User;
import com.example.booking.dto.request.BookingRequest;
import com.example.booking.dto.request.RoomRequest;
import com.example.booking.dto.request.UserRequest;
import com.example.booking.dto.response.BookingResponse;
import com.example.booking.dto.response.RoomResponse;
import com.example.booking.dto.response.UserResponse;
import com.example.booking.enums.ReportFormat;
import com.example.booking.enums.RoomType;
import com.example.booking.export.reports.ReportExporter;
import com.example.booking.export.rooms.RoomExporter;
import com.example.booking.factories.ReportExporterFactory;
import com.example.booking.factories.RoomExporterFactory;
import com.example.booking.services.bookings.BookingService;
import com.example.booking.services.rooms.RoomService;
import com.example.booking.services.users.UserService;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CampusFacade {
    private final UserService userService;
    private final RoomService roomService;
    private final BookingService bookingService;
    private final ReportBuilder reportBuilder;
    private final ReportExporterFactory reportExporterFactory;
    private final RoomExporterFactory roomExporterFactory;

    public UserResponse addUser(UserRequest request) {
        return toUserResponse(userService.create(request));
    }

    public UserResponse updateUser(Long id, UserRequest request) {
        return toUserResponse(userService.update(id, request));
    }

    public void deleteUser(Long id) {
        userService.delete(id);
    }

    public List<UserResponse> listUsers() {
        return userService.getAll().stream().map(this::toUserResponse).collect(Collectors.toList());
    }

    public UserResponse getUser(Long id) {
        return toUserResponse(userService.getById(id));
    }

    public RoomResponse addRoom(RoomRequest request) {
        return toRoomResponse(roomService.create(request));
    }

    public RoomResponse updateRoom(Long id, RoomRequest request) {
        return toRoomResponse(roomService.update(id, request));
    }

    public void deleteRoom(Long id) {
        roomService.delete(id);
    }

    public List<RoomResponse> listRooms() {
        return roomService.getAll().stream().map(this::toRoomResponse).collect(Collectors.toList());
    }

    public List<RoomResponse> filterRoomsByCapacity(int capacity) {
        return roomService.filterByCapacity(capacity).stream().map(this::toRoomResponse).collect(Collectors.toList());
    }

    public List<RoomResponse> filterRoomsByType(RoomType type) {
        return roomService.filterByType(type).stream().map(this::toRoomResponse).collect(Collectors.toList());
    }

    public RoomResponse getRoom(Long id) {
        return toRoomResponse(roomService.getById(id));
    }

    public BookingResponse createBooking(BookingRequest request) {
        return toBookingResponse(bookingService.create(request));
    }

    public BookingResponse updateBooking(Long id, BookingRequest request) {
        return toBookingResponse(bookingService.update(id, request));
    }

    public BookingResponse cancelBooking(Long id) {
        return toBookingResponse(bookingService.cancel(id));
    }

    public void deleteBooking(Long id) {
        bookingService.delete(id);
    }

    public BookingResponse getBooking(Long id) {
        return toBookingResponse(bookingService.getById(id));
    }

    public List<BookingResponse> listBookings() {
        return bookingService.getAll().stream().map(this::toBookingResponse).collect(Collectors.toList());
    }

    public void exportReport(ReportFormat format, Writer writer) throws IOException {
        Report report = reportBuilder.build();
        ReportExporter exporter = reportExporterFactory.resolve(format);
        exporter.export(report, writer);
    }

    public void exportRooms(ReportFormat format, Writer writer) throws IOException {
        List<Room> rooms = roomService.getAll();
        RoomExporter exporter = roomExporterFactory.resolve(format);
        exporter.export(rooms, writer);
    }

    public RoomResponse bookExternalRoom(ExternalRoom externalRoom) {
        ExternalRoomAdapter adapter = new ExternalRoomAdapter(externalRoom);
        Room adapted = adapter.toRoom();
        RoomRequest request = new RoomRequest();
        request.setName(adapted.getName());
        request.setCapacity(adapted.getCapacity());
        request.setType(adapted.getType());
        request.setAvailable(adapted.isAvailable());
        Room created = roomService.create(request);
        request.setAvailable(false);
        Room booked = roomService.update(created.getId(), request);
        return toRoomResponse(booked);
    }

    private UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .active(user.isActive())
                .build();
    }

    private RoomResponse toRoomResponse(Room room) {
        return RoomResponse.builder()
                .id(room.getId())
                .name(room.getName())
                .capacity(room.getCapacity())
                .type(room.getType())
                .available(room.isAvailable())
                .build();
    }

    private BookingResponse toBookingResponse(Booking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .roomId(booking.getRoom().getId())
                .userId(booking.getUser().getId())
                .startTime(booking.getStartTime())
                .endTime(booking.getEndTime())
                .status(booking.getStatus())
                .build();
    }
}
