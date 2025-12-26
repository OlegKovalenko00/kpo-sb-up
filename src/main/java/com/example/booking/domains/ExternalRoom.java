package com.example.booking.domains;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExternalRoom {
    private String code;
    private String label;
    private int seats;
    private String category;
    private boolean free;
}
