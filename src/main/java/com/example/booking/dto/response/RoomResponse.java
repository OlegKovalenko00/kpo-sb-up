package com.example.booking.dto.response;

import com.example.booking.enums.RoomType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoomResponse {
    private Long id;
    private String name;
    private int capacity;
    private RoomType type;
    private boolean available;
}
