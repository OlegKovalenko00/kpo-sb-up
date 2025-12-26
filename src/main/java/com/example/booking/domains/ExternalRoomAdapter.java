package com.example.booking.domains;

import com.example.booking.enums.RoomType;

public class ExternalRoomAdapter {
    private final ExternalRoom externalRoom;

    public ExternalRoomAdapter(ExternalRoom externalRoom) {
        this.externalRoom = externalRoom;
    }

    public Room toRoom() {
        return Room.builder()
                .name(externalRoom.getLabel())
                .capacity(externalRoom.getSeats())
                .type(resolveType(externalRoom.getCategory()))
                .available(externalRoom.isFree())
                .build();
    }

    private RoomType resolveType(String category) {
        if (category == null) {
            return RoomType.SMALL;
        }
        String normalized = category.toUpperCase();
        if (normalized.contains("LARGE")) {
            return RoomType.LARGE;
        }
        if (normalized.contains("MEDIUM")) {
            return RoomType.MEDIUM;
        }
        if (normalized.contains("ONLINE")) {
            return RoomType.ONLINE;
        }
        return RoomType.SMALL;
    }
}
