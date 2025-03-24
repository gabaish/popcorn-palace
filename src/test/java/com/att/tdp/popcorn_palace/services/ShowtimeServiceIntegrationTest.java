package com.att.tdp.popcorn_palace.services;

import com.att.tdp.popcorn_palace.entities.Movie;
import com.att.tdp.popcorn_palace.entities.Showtime;
import com.att.tdp.popcorn_palace.repositories.MovieRepository;
import com.att.tdp.popcorn_palace.repositories.ShowtimeRepository;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ShowtimeServiceIntegrationTest {

    @Autowired
    private ShowtimeService showtimeService;

    @Autowired
    private ShowtimeRepository showtimeRepository;

    @Autowired
    private MovieRepository movieRepository;

    private Long movieId;

    @BeforeEach
    void setupMovie() {
        // make sure a movie exists in the system to connect the showtime to it 
        Movie movie = new Movie(null, "Harry Potter", "Fantasy", 162, 9.8, 2012, null);
        movieId = movieRepository.save(movie).getId();
    }

    @Test
    void testAddShowtime_success() {
        Showtime showtime = showtimeService.addShowtime(movieId, "Theater 1",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(1).plusHours(2),
                10.0);

        assertNotNull(showtime.getId());
        assertEquals("Theater 1", showtime.getTheater());
    }

    @Test
    void testAddShowtime_conflict_throws() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);

        showtimeService.addShowtime(movieId, "Theater 1", start, start.plusHours(2), 12.0);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            showtimeService.addShowtime(movieId, "Theater 1", start.plusMinutes(30), start.plusHours(2).plusMinutes(30), 12.0);
        });

        assertEquals(409, ex.getStatusCode().value());
    }

    @Test
    void testAddShowtime_invalidMovie() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            showtimeService.addShowtime(5995L, "Theater 1", start.plusMinutes(30), start.plusHours(2).plusMinutes(30), 12.0);
        });

        assertEquals(404, ex.getStatusCode().value());
    }

    @Test
    void testEditShowtime_success() {
        Showtime original = showtimeRepository.save(new Showtime(null, movieId, "Theater 1",
                LocalDateTime.of(2025, 4, 12, 10, 0),
                LocalDateTime.of(2025, 4, 12, 12, 0), 10.0));

        Showtime updated = showtimeService.editShowtime(original.getId(), movieId, "Theater 2",
                LocalDateTime.of(2025, 4, 13, 14, 0),
                LocalDateTime.of(2025, 4, 13, 16, 0), 15.0);

        assertEquals("Theater 2", updated.getTheater());
        assertEquals(15.0, updated.getPrice());
    }

    @Test
    void testEditShowtime_conflictingSchedule() {
        // Existing showtime (to conflict with)
        showtimeRepository.save(new Showtime(null, movieId, "Theater 1",
                LocalDateTime.of(2025, 4, 10, 10, 0),
                LocalDateTime.of(2025, 4, 10, 12, 0), 10.0));

        // Showtime to edit
        Showtime toEdit = showtimeRepository.save(new Showtime(null, movieId, "Theater 1",
                LocalDateTime.of(2025, 4, 11, 10, 0),
                LocalDateTime.of(2025, 4, 11, 12, 0), 10.0));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            showtimeService.editShowtime(toEdit.getId(), movieId, "Theater 1",
                    LocalDateTime.of(2025, 4, 10, 11, 0),
                    LocalDateTime.of(2025, 4, 10, 13, 0),
                    12.0);
        });

        assertEquals(409, ex.getStatusCode().value());
    }

    @Test
    void testEditShowtime_movieNotFound() {
        Showtime original = showtimeRepository.save(new Showtime(null, movieId, "Theater 1",
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(2).plusHours(2), 10.0));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            showtimeService.editShowtime(original.getId(), 9999L, "Theater 1",
                    LocalDateTime.now().plusDays(3),
                    LocalDateTime.now().plusDays(3).plusHours(2),
                    12.0);
        });

        assertEquals(404, ex.getStatusCode().value());
    }

    @Test
    void testEditShowtime_showtimeNotFound() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            showtimeService.editShowtime(9999L, movieId, "Theater 1",
                    LocalDateTime.now().plusDays(2),
                    LocalDateTime.now().plusDays(2).plusHours(2),
                    10.0);
        });

        assertEquals(404, ex.getStatusCode().value());
    }

    @Test
    void testDeleteShowtime_success() {
        Showtime showtime = showtimeRepository.save(new Showtime(null, movieId, "Theater 1",
                LocalDateTime.now().plusDays(5),
                LocalDateTime.now().plusDays(5).plusHours(2), 12.0));

        showtimeService.deleteShowtime(showtime.getId());

        assertTrue(showtimeRepository.findById(showtime.getId()).isEmpty());
    }

    @Test
    void testDeleteShowtime_notFound() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            showtimeService.deleteShowtime(9999L);
        });

        assertEquals(404, ex.getStatusCode().value());
    }

}
