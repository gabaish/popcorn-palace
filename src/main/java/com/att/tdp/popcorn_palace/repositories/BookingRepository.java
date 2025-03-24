package com.att.tdp.popcorn_palace.repositories;

import com.att.tdp.popcorn_palace.entities.Booking;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {
    boolean existsByShowtimeIdAndSeatNumber(Long showtimeId, Integer seatNumber);
}