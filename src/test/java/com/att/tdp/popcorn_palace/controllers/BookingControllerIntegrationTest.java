package com.att.tdp.popcorn_palace.controllers;

import com.att.tdp.popcorn_palace.dto.BookingRequest;
import com.att.tdp.popcorn_palace.entities.Movie;
import com.att.tdp.popcorn_palace.entities.Showtime;
import com.att.tdp.popcorn_palace.repositories.MovieRepository;
import com.att.tdp.popcorn_palace.repositories.ShowtimeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.containsString;

@SpringBootTest
@AutoConfigureMockMvc
class BookingControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private MovieRepository movieRepository;
    @Autowired private ShowtimeRepository showtimeRepository;
    @Autowired private ObjectMapper objectMapper;

    private Long showtimeId;

    @BeforeEach
    void setup() {
        movieRepository.deleteAll();
        showtimeRepository.deleteAll();

        Movie movie = movieRepository.save(new Movie(null, "Booking Movie", "Thriller", 90, 7.0, 2022, null));
        Showtime showtime = showtimeRepository.save(new Showtime(null, movie.getId(), "Theater Z",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(1).plusHours(2), 15.0));

        showtimeId = showtime.getId();
    }

    @Test
    void testCreateBooking_success() throws Exception {
        BookingRequest req = new BookingRequest();
        req.setShowtimeId(showtimeId);
        req.setSeatNumber(5);
        req.setUserId(UUID.randomUUID());

        mockMvc.perform(post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("bookingId")));
    }

    @Test
    void testCreateBooking_duplicateSeat() throws Exception {
        UUID user = UUID.randomUUID();
        BookingRequest req = new BookingRequest();
        req.setShowtimeId(showtimeId);
        req.setSeatNumber(5);
        req.setUserId(user);

        // First booking
        mockMvc.perform(post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isOk());

        // Second booking same seat
        mockMvc.perform(post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isConflict());
    }

    @Test
    void testCreateBooking_invalidShowtime() throws Exception {
        BookingRequest req = new BookingRequest();
        req.setShowtimeId(9999L);
        req.setSeatNumber(10);
        req.setUserId(UUID.randomUUID());

        mockMvc.perform(post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isNotFound());
    }

    @Test
    void testAddBooking_partialData() throws Exception {
        String invalidJson = """
            {
                "showtimeId": 1,
                "userId": "84438967-f68f-4fa0-b620-0f08217e76af"
            }
            """;

        mockMvc.perform(post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    void testAddBooking_invalidUUIDFormat() throws Exception {
        String invalidJson = """
            {
                "showtimeId": 1,
                "seatNumber": 10,
                "userId": "not-a-valid-uuid"
            }
            """;

        mockMvc.perform(post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Invalid UUID format. Please provide a valid UUID."));
    }


}
