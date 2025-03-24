package com.att.tdp.popcorn_palace.controllers;

import com.att.tdp.popcorn_palace.entities.Movie;
import com.att.tdp.popcorn_palace.repositories.MovieRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class MovieControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private MovieRepository movieRepository;
    @Autowired private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        movieRepository.deleteAll();
    }

    @Test
    void testAddMovie_success() throws Exception {
        Movie movie = new Movie(null, "Harry Potter", "Fantasy", 162, 9.8, 2012, null);

        mockMvc.perform(post("/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(movie)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Harry Potter"));
    }

    @Test
    void testAddMovie_duplicateTitle() throws Exception {
        Movie movie = new Movie(null, "Harry Potter", "Fantasy", 162, 9.8, 2012, null);
        movieRepository.save(movie);

        mockMvc.perform(post("/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(movie)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void testAddMovie_missingFieldTitle() throws Exception {
        String invalid = """
            {
                "genre": "Action",
                "duration": 155,
                "rating": 8.0,
                "releaseYear": 2022
            }
            """;

        mockMvc.perform(post("/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalid))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors").isArray())
            .andExpect(jsonPath("$.errors[0]").value(org.hamcrest.Matchers.containsString("title")));;
    }

    @Test
    void testAddMovie_missingFieldGenre() throws Exception {
        String invalid = """
            {
                "title": "Harry Potter",
                "duration": 155,
                "rating": 8.0,
                "releaseYear": 2022
            }
            """;

        mockMvc.perform(post("/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalid))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors").isArray())
            .andExpect(jsonPath("$.errors[0]").value(org.hamcrest.Matchers.containsString("genre")));;
    }

    @Test
    void testAddMovie_invalidFieldDuration() throws Exception {
        String invalid = """
            {
                "title": "Harry Potter",
                "genre": "Fantasy",
                "duration": -14,
                "rating": 9.8,
                "releaseYear": 2012
            }
            """;

        mockMvc.perform(post("/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalid))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors").isArray())
            .andExpect(jsonPath("$.errors[0]").value(org.hamcrest.Matchers.containsString("duration")));;
    }

    @Test
    void testAddMovie_invalidFieldRating() throws Exception {
        String invalid = """
            {
                "title": "Harry Potter",
                "genre": "Fantasy",
                "duration": 162,
                "rating": -9.8,
                "releaseYear": 2012
            }
            """;

        mockMvc.perform(post("/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalid))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors").isArray())
            .andExpect(jsonPath("$.errors[0]").value(org.hamcrest.Matchers.containsString("rating")));;
    }

    @Test
    void testAddMovie_invalidFieldReleaseYear() throws Exception {
        String invalid = """
            {
                "title": "Harry Potter",
                "genre": "Fantasy",
                "duration": 162,
                "rating": 9.8,
                "releaseYear": -2012
            }
            """;

        mockMvc.perform(post("/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalid))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors").isArray())
            .andExpect(jsonPath("$.errors[0]").value(org.hamcrest.Matchers.containsString("releaseYear")));;
    }

    @Test
    void testDeleteMovie_notFound() throws Exception {
        mockMvc.perform(delete("/movies/UnknownMovie"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("No movie with the specified title exists in the system."));
    }

    @Test
    void testDeleteMovie_success() throws Exception {
        Movie movie = new Movie(null, "Harry Potter", "Fantasy", 162, 9.8, 2012, null);
        movieRepository.save(movie);

        mockMvc.perform(delete("/movies/Harry Potter"))
            .andExpect(status().isOk());
    }

    @Test
    void testUpdateMovie_missingFieldGenre() throws Exception {
        Movie original = new Movie(null, "Harry Potter", "Fantasy", 162, 9.8, 2012, null);
        movieRepository.save(original);

        String partialJson = """
            {
                "title": "Harry Potter",
                
                "duration": 162,
                "rating": 9.8,
                "releaseYear": 2012
            }
            """;

        mockMvc.perform(post("/movies/Harry Potter")
                .contentType(MediaType.APPLICATION_JSON)
                .content(partialJson))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors").exists())
            .andExpect(jsonPath("$.errors[0]").value(org.hamcrest.Matchers.containsString("genre")));
    }

    @Test
    void testUpdateMovie_invalidFieldRating() throws Exception {
        Movie original = new Movie(null, "Harry Potter", "Fantasy", 162, 9.8, 2012, null);
        movieRepository.save(original);

        String partialJson = """
            {
                "title": "Harry Potter",
                "genre": "Fantasy",
                "duration": -162,
                "rating": 9.8,
                "releaseYear": 2012
            }
            """;

        mockMvc.perform(post("/movies/Harry Potter")
                .contentType(MediaType.APPLICATION_JSON)
                .content(partialJson))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors").exists())
            .andExpect(jsonPath("$.errors[0]").value(org.hamcrest.Matchers.containsString("duration")));
    }

    @Test
    void testUpdateMovie_notFound() throws Exception {
        String updated = """
            {
                "title": "Harry Potter",
                "genre": "Fantasy",
                "duration": 162,
                "rating": 9.8,
                "releaseYear": 2012
            }
            """;

        mockMvc.perform(post("/movies/NonExistingMovie")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updated))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("The requested movie does not exist in the system. Please verify the movie title or add it as a new entry."));
    }

    @Test
    void testUpdateMovie_success() throws Exception {
        Movie movie = new Movie(null, "Harry Potter", "Fantasy", 162, 9.8, 2012, null);
        movieRepository.save(movie);

        String updatedJson = """
            {
                "title": "Harry Potter",
                "genre": "Magic",
                "duration": 162,
                "rating": 9.0,
                "releaseYear": 2012
            }
            """;

        mockMvc.perform(post("/movies/Harry Potter")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.genre").value("Magic"))
            .andExpect(jsonPath("$.rating").value(9.0));
    }

    @Test
    void testGetAllMovies_empty() throws Exception {
        mockMvc.perform(get("/movies/all"))
            .andExpect(status().isOk())
            .andExpect(content().json("[]"));
    }
}
