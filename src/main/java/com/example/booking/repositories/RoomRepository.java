package com.example.booking.repositories;

import com.example.booking.domains.Room;
import com.example.booking.enums.RoomType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByCapacityGreaterThanEqual(int capacity);
    List<Room> findByType(RoomType type);
}
