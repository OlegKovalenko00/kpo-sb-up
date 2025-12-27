package com.example.booking.services.rooms;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.booking.domains.Room;
import com.example.booking.dto.request.RoomRequest;
import com.example.booking.enums.RoomType;
import com.example.booking.exception.BookingException;
import com.example.booking.repositories.RoomRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RoomServiceTest {
    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private RoomService roomService;

    @Test
    void shouldCreateRoom() {
        RoomRequest request = new RoomRequest();
        request.setName("Room A");
        request.setCapacity(12);
        request.setType(RoomType.MEDIUM);
        request.setAvailable(true);

        when(roomRepository.save(any())).thenAnswer(invocation -> {
            Room room = invocation.getArgument(0);
            room.setId(1L);
            return room;
        });

        Room result = roomService.create(request);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo(request.getName());
        assertThat(result.getCapacity()).isEqualTo(request.getCapacity());
        assertThat(result.getType()).isEqualTo(request.getType());
        assertThat(result.isAvailable()).isTrue();
        verify(roomRepository).save(any(Room.class));
    }

    @Test
    void shouldUpdateRoom() {
        Room existing = Room.builder()
                .id(2L)
                .name("Old")
                .capacity(5)
                .type(RoomType.SMALL)
                .available(false)
                .build();
        RoomRequest request = new RoomRequest();
        request.setName("New");
        request.setCapacity(20);
        request.setType(RoomType.LARGE);
        request.setAvailable(true);

        when(roomRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
        when(roomRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Room updated = roomService.update(existing.getId(), request);

        assertThat(updated.getName()).isEqualTo(request.getName());
        assertThat(updated.getCapacity()).isEqualTo(request.getCapacity());
        assertThat(updated.getType()).isEqualTo(request.getType());
        assertThat(updated.isAvailable()).isTrue();
        verify(roomRepository).save(any(Room.class));
    }

    @Test
    void shouldThrowWhenRoomNotFound() {
        when(roomRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> roomService.getById(99L))
                .isInstanceOf(BookingException.class)
                .hasMessageContaining("Room not found");
    }
}
