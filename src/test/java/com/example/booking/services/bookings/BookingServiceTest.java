package com.example.booking.services.bookings;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.booking.clients.NotificationClient;
import com.example.booking.domains.Booking;
import com.example.booking.domains.Room;
import com.example.booking.domains.User;
import com.example.booking.dto.request.BookingRequest;
import com.example.booking.dto.request.NotificationRequest;
import com.example.booking.dto.response.NotificationResponse;
import com.example.booking.enums.BookingStatus;
import com.example.booking.enums.RoomType;
import com.example.booking.exception.BookingException;
import com.example.booking.kafka.BookingProducerService;
import com.example.booking.kafka.events.BookingCanceledEvent;
import com.example.booking.kafka.events.RoomBookedEvent;
import com.example.booking.repositories.BookingRepository;
import com.example.booking.services.rooms.RoomService;
import com.example.booking.services.users.UserService;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {
    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private RoomService roomService;

    @Mock
    private UserService userService;

    @Mock
    private NotificationClient notificationClient;

    @Mock
    private BookingProducerService bookingProducerService;

    @InjectMocks
    private BookingService bookingService;

    @Test
    void shouldCreateBookingAndPublishEvents() {
        Room room = Room.builder().id(1L).name("Room A").capacity(10).type(RoomType.SMALL).available(true).build();
        User user = User.builder().id(2L).name("User A").email("user@example.com").active(true).build();
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = start.plusHours(1);
        BookingRequest request = new BookingRequest();
        request.setRoomId(room.getId());
        request.setUserId(user.getId());
        request.setStartTime(start);
        request.setEndTime(end);

        when(roomService.getById(room.getId())).thenReturn(room);
        when(userService.getById(user.getId())).thenReturn(user);
        when(bookingRepository.save(any())).thenAnswer(invocation -> {
            Booking booking = invocation.getArgument(0);
            booking.setId(10L);
            return booking;
        });
        when(notificationClient.send(any())).thenReturn(NotificationResponse.builder().delivered(true).build());

        Booking result = bookingService.create(request);

        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getStatus()).isEqualTo(BookingStatus.CREATED);
        assertThat(room.isAvailable()).isFalse();

        ArgumentCaptor<RoomBookedEvent> roomBookedCaptor = ArgumentCaptor.forClass(RoomBookedEvent.class);
        verify(bookingProducerService).publishRoomBooked(roomBookedCaptor.capture());
        RoomBookedEvent event = roomBookedCaptor.getValue();
        assertThat(event.getBookingId()).isEqualTo(10L);
        assertThat(event.getRoomId()).isEqualTo(room.getId());
        assertThat(event.getUserId()).isEqualTo(user.getId());

        verify(notificationClient).send(any(NotificationRequest.class));
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void shouldThrowWhenRoomUnavailable() {
        Room room = Room.builder().id(1L).available(false).build();
        User user = User.builder().id(2L).active(true).build();
        BookingRequest request = new BookingRequest();
        request.setRoomId(room.getId());
        request.setUserId(user.getId());
        request.setStartTime(LocalDateTime.now().plusHours(1));
        request.setEndTime(LocalDateTime.now().plusHours(2));

        when(roomService.getById(room.getId())).thenReturn(room);
        when(userService.getById(user.getId())).thenReturn(user);

        assertThatThrownBy(() -> bookingService.create(request))
                .isInstanceOf(BookingException.class)
                .hasMessageContaining("Room is unavailable");
    }

    @Test
    void shouldThrowWhenUserInactive() {
        Room room = Room.builder().id(1L).available(true).build();
        User user = User.builder().id(2L).active(false).build();
        BookingRequest request = new BookingRequest();
        request.setRoomId(room.getId());
        request.setUserId(user.getId());
        request.setStartTime(LocalDateTime.now().plusHours(1));
        request.setEndTime(LocalDateTime.now().plusHours(2));

        when(roomService.getById(room.getId())).thenReturn(room);
        when(userService.getById(user.getId())).thenReturn(user);

        assertThatThrownBy(() -> bookingService.create(request))
                .isInstanceOf(BookingException.class)
                .hasMessageContaining("User is inactive");
    }

    @Test
    void shouldThrowWhenEndBeforeStart() {
        Room room = Room.builder().id(1L).available(true).build();
        User user = User.builder().id(2L).active(true).build();
        LocalDateTime start = LocalDateTime.now().plusHours(2);
        LocalDateTime end = start.minusHours(1);
        BookingRequest request = new BookingRequest();
        request.setRoomId(room.getId());
        request.setUserId(user.getId());
        request.setStartTime(start);
        request.setEndTime(end);

        when(roomService.getById(room.getId())).thenReturn(room);
        when(userService.getById(user.getId())).thenReturn(user);

        assertThatThrownBy(() -> bookingService.create(request))
                .isInstanceOf(BookingException.class)
                .hasMessageContaining("End time must be after start time");
    }

    @Test
    void shouldCancelBooking() {
        Room room = Room.builder().id(1L).available(false).build();
        User user = User.builder().id(2L).active(true).build();
        Booking booking = Booking.builder()
                .id(5L)
                .room(room)
                .user(user)
                .startTime(LocalDateTime.now().plusHours(1))
                .endTime(LocalDateTime.now().plusHours(2))
                .status(BookingStatus.CREATED)
                .build();

        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Booking result = bookingService.cancel(booking.getId());

        assertThat(result.getStatus()).isEqualTo(BookingStatus.CANCELED);
        assertThat(room.isAvailable()).isTrue();

        ArgumentCaptor<BookingCanceledEvent> captor = ArgumentCaptor.forClass(BookingCanceledEvent.class);
        verify(bookingProducerService).publishBookingCanceled(captor.capture());
        BookingCanceledEvent event = captor.getValue();
        assertThat(event.getBookingId()).isEqualTo(booking.getId());
        assertThat(event.getRoomId()).isEqualTo(room.getId());
        assertThat(event.getUserId()).isEqualTo(user.getId());

        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void shouldThrowWhenCancelingAlreadyCanceled() {
        Room room = Room.builder().id(1L).available(true).build();
        Booking booking = Booking.builder().id(5L).room(room).status(BookingStatus.CANCELED).build();
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.cancel(booking.getId()))
                .isInstanceOf(BookingException.class)
                .hasMessageContaining("Booking already canceled");
    }
}
