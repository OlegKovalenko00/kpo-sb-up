package com.example.booking.services.rooms;

import com.example.booking.domains.Room;
import com.example.booking.dto.request.RoomRequest;
import com.example.booking.enums.RoomType;
import com.example.booking.exception.BookingException;
import com.example.booking.repositories.RoomRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;

    @Transactional
    public Room create(RoomRequest request) {
        Room room = Room.builder()
                .name(request.getName())
                .capacity(request.getCapacity())
                .type(request.getType())
                .available(request.isAvailable())
                .build();
        return roomRepository.save(room);
    }

    @Transactional
    public Room update(Long id, RoomRequest request) {
        Room room = getById(id);
        room.setName(request.getName());
        room.setCapacity(request.getCapacity());
        room.setType(request.getType());
        room.setAvailable(request.isAvailable());
        return roomRepository.save(room);
    }

    @Transactional
    public void delete(Long id) {
        Room room = getById(id);
        roomRepository.delete(room);
    }

    public Room getById(Long id) {
        return roomRepository.findById(id).orElseThrow(() -> new BookingException("Room not found"));
    }

    public List<Room> getAll() {
        return roomRepository.findAll();
    }

    public List<Room> filterByCapacity(int capacity) {
        return roomRepository.findByCapacityGreaterThanEqual(capacity);
    }

    public List<Room> filterByType(RoomType type) {
        return roomRepository.findByType(type);
    }
}
