package com.att.tdp.popcorn_palace.controllers;

import com.att.tdp.popcorn_palace.dto.ShowtimeRequest;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ShowtimeControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private MovieRepository movieRepository;
    @Autowired private ShowtimeRepository showtimeRepository;


    private Long movieId;

    @BeforeEach
    void setup() {
        showtimeRepository.deleteAll();
        movieRepository.deleteAll();
        Movie movie = new Movie(null, "Showtime Movie", "Drama", 100, 7.5, 2023, null);
        movieId = movieRepository.save(movie).getId();
    }

    @Test
    void testAddShowtime_success() throws Exception {
        ShowtimeRequest req = new ShowtimeRequest();
        req.setMovieId(movieId);
        req.setTheater("Theater 1");
        req.setStartTime(LocalDateTime.now().plusDays(1));
        req.setEndTime(LocalDateTime.now().plusDays(1).plusHours(2));
        req.setPrice(20.5);

        mockMvc.perform(post("/showtimes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.theater").value("Theater 1"));
    }

    @Test
    void testAddShowtime_missingField() throws Exception {
        String json = """
            {
              "movieId": %d,
              "startTime": "2025-04-01T18:00:00",
              "endTime": "2025-04-01T20:00:00"
            }
            """.formatted(movieId);

        mockMvc.perform(post("/showtimes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    void testAddShowtime_invalidFieldTime() throws Exception {
        String json = """
            {
              "movieId": %d,
              "theater": "Theater 1",
              "startTime": "2025-04:00",
              "endTime": "2025-04-01T20:00:00",
              "price": 10.2
            }
            """.formatted(movieId);

        mockMvc.perform(post("/showtimes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testGetShowtime_notFound() throws Exception {
        mockMvc.perform(get("/showtimes/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void testEditShowtime_success() throws Exception {
        // Create a showtime to edit
        Showtime showtime = showtimeRepository.save(new Showtime(null, movieId, "Theater 1",
            LocalDateTime.of(2030, 1, 1, 10, 0),
            LocalDateTime.of(2030, 1, 1, 12, 0),
            25.0));

        ShowtimeRequest update = new ShowtimeRequest();
        update.setMovieId(movieId);
        update.setTheater("Theater 1");
        update.setStartTime(LocalDateTime.of(2030, 1, 1, 13, 0));
        update.setEndTime(LocalDateTime.of(2030, 1, 1, 15, 0));
        update.setPrice(30.0);

        mockMvc.perform(post("/showtimes/" + showtime.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(update)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.price").value(30.0));
    }

    @Test
    void testEditShowtime_partialRequest() throws Exception {
        Showtime showtime = showtimeRepository.save(new Showtime(null, movieId, "Theater 1",
            LocalDateTime.of(2030, 1, 1, 10, 0),
            LocalDateTime.of(2030, 1, 1, 12, 0),
            25.0));

        String json = """
            {
            "movieId": %d,
            "startTime": "2030-01-01T14:00:00"
            }
            """.formatted(movieId);

        mockMvc.perform(post("/showtimes/" + showtime.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isBadRequest());
            
    }

    @Test
    void testEditShowtime_nonExistentId() throws Exception {
        ShowtimeRequest update = new ShowtimeRequest();
        update.setMovieId(movieId);
        update.setTheater("Theater 1");
        update.setStartTime(LocalDateTime.of(2030, 1, 1, 10, 0));
        update.setEndTime(LocalDateTime.of(2030, 1, 1, 12, 0));
        update.setPrice(20.0);

        mockMvc.perform(post("/showtimes/9999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(update)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void testDeleteShowtime_success() throws Exception {
        Showtime showtime = showtimeRepository.save(new Showtime(null, movieId, "Theater 1",
            LocalDateTime.of(2030, 1, 1, 10, 0),
            LocalDateTime.of(2030, 1, 1, 12, 0),
            25.0));

        mockMvc.perform(delete("/showtimes/" + showtime.getId()))
            .andExpect(status().isOk());
    }

    @Test
    void testDeleteShowtime_nonExistentId() throws Exception {
        mockMvc.perform(delete("/showtimes/9999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("No showtime with the specified id exists in the system."));
    }

}
