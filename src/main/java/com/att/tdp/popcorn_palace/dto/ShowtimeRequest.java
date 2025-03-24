package com.att.tdp.popcorn_palace.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

import jakarta.validation.constraints.*;

@Getter
@Setter
public class ShowtimeRequest {
    @Min(value = 0, message = "Movie Id must be greater than 0")
    private Long movieId;

    @NotBlank(message = "Theater is required")
    private String theater;

    @NotNull(message = "Start Time is required")
    private LocalDateTime startTime;

    @NotNull(message = "End Time is required")
    private LocalDateTime endTime;

    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private double price;
}
