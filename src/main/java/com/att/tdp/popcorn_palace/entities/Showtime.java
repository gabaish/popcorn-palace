package com.att.tdp.popcorn_palace.entities;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "showtimes", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"theater", "startTime"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Showtime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Min(value = 0, message = "Movie Id must be greater than 0")
    private Long movieId;

    @Column(nullable = false)
    @NotBlank(message = "Theater is required")
    private String theater;

    @Column(nullable = false)
    @NotNull(message = "Start Time is required")
    private LocalDateTime startTime;

    @Column(nullable = false)
    @NotNull(message = "End Time is required")
    private LocalDateTime endTime;

    @Column(nullable = false)
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private double price;

    // Ensure all bookings related to this showtime are deleted when showtime is deleted
    @OneToMany(mappedBy = "showtimeId", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Booking> bookings = new ArrayList<>();

    public Showtime(Long id, Long movieId, String theater, LocalDateTime startTime, LocalDateTime endTime, double price) {
        this.id = id;
        this.movieId = movieId;
        this.theater = theater;
        this.startTime = startTime;
        this.endTime = endTime;
        this.price = price;
    }
}
