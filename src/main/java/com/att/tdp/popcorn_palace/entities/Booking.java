package com.att.tdp.popcorn_palace.entities;

import java.util.UUID;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "bookings", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"showtime_id", "seatNumber"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    @Id
    private UUID bookingId;

    @Column(nullable = false)
    private Long showtimeId;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private Integer seatNumber;

    public Booking(UUID bookingId, Long showtimeId, Integer seatNumber, UUID userId) {
        this.bookingId = bookingId;
        this.showtimeId = showtimeId;
        this.seatNumber = seatNumber;
        this.userId = userId;
    }
}
