package com.example.booking.dto.request;

import com.example.booking.enums.RoomType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RoomRequest {
    @NotBlank
    private String name;
    @Min(1)
    private int capacity;
    @NotNull
    private RoomType type;
    private boolean available = true;
}
