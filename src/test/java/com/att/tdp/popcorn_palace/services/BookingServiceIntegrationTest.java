package com.att.tdp.popcorn_palace.services;

import com.att.tdp.popcorn_palace.entities.Booking;
import com.att.tdp.popcorn_palace.entities.Movie;
import com.att.tdp.popcorn_palace.entities.Showtime;
import com.att.tdp.popcorn_palace.repositories.BookingRepository;
import com.att.tdp.popcorn_palace.repositories.MovieRepository;
import com.att.tdp.popcorn_palace.repositories.ShowtimeRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class BookingServiceIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private ShowtimeRepository showtimeRepository;

    private Long showtimeId;

    @BeforeEach
    void setupShowtime() {
        // make sure a showtime exists in the system to connect the booking to it 
        Movie movie = movieRepository.save(new Movie(null, "Harry Potter", "Fantasy", 162, 9.8, 2012, null));
        Showtime showtime = showtimeRepository.save(new Showtime(null, movie.getId(), "Theater 1",
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(3), 15.0));
        showtimeId = showtime.getId();
    }

    @Test
    void testCreateBooking_success() {
        UUID userId = UUID.randomUUID();
        UUID bookingId = bookingService.createBooking(showtimeId, 5, userId);

        assertNotNull(bookingId);
        Booking booking = bookingRepository.findById(bookingId).orElse(null);
        assertNotNull(booking);
        assertEquals(5, booking.getSeatNumber());
    }

    @Test
    void testCreateBooking_seatAlreadyBooked() {
        UUID user1 = UUID.randomUUID();
        UUID user2 = UUID.randomUUID();
        bookingService.createBooking(showtimeId, 10, user1);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            bookingService.createBooking(showtimeId, 10, user2);
        });

        assertEquals(409, ex.getStatusCode().value());
    }

    @Test
    void testCreateBooking_invalidShowtime() {
        UUID userId = UUID.randomUUID();
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            bookingService.createBooking(9999L, 1, userId);
        });

        assertEquals(404, ex.getStatusCode().value());
    }
}
