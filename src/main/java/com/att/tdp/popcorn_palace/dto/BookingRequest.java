package com.att.tdp.popcorn_palace.dto;

import java.util.UUID;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingRequest {

    @NotNull(message = "Showtime ID is required")
    @Min(value = 1, message = "Showtime ID must be greater than 0")
    private Long showtimeId;

    @NotNull(message = "Seat number is required")
    @Min(value = 1, message = "Seat number must be greater than 0")
    private Integer seatNumber;

    @NotNull(message = "User ID is required")
    private UUID userId;
}
