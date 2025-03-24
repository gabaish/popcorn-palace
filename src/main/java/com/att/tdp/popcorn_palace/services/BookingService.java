package com.att.tdp.popcorn_palace.services;

import com.att.tdp.popcorn_palace.entities.Booking;
import com.att.tdp.popcorn_palace.repositories.BookingRepository;
import com.att.tdp.popcorn_palace.repositories.ShowtimeRepository;

import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final ShowtimeRepository showtimeRepository;

    public BookingService(BookingRepository bookingRepository, ShowtimeRepository showtimeRepository) {
        this.bookingRepository = bookingRepository;
        this.showtimeRepository = showtimeRepository;
    }

    public UUID createBooking(Long showtimeId, Integer seatNumber, UUID userId) {
        // check if the showtime exists
        if (!showtimeRepository.existsById(showtimeId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No showtime with the specified id exists in the system.");
        }

        // Check if seat is already booked
        if (bookingRepository.existsByShowtimeIdAndSeatNumber(showtimeId, seatNumber)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Seat is already booked for this showtime.");
        }

        // Generate a unique booking ID
        UUID bookingId = UUID.randomUUID();

        // Save booking
        Booking booking = new Booking(bookingId, showtimeId, seatNumber, userId);
        bookingRepository.save(booking);

        return bookingId;
    }

    // public List<Booking> getAllBookings() {
    //     return bookingRepository.findAll();
    // }
}
