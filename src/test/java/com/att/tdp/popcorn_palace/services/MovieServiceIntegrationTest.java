package com.att.tdp.popcorn_palace.services;

import com.att.tdp.popcorn_palace.entities.Movie;
import com.att.tdp.popcorn_palace.repositories.MovieRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;


import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MovieServiceIntegrationTest {

    @Autowired
    private MovieService movieService;

    @Autowired
    private MovieRepository movieRepository;

    @Test
    void testAddMovie_success() {
        Movie movie = new Movie(null, "Harry Potter", "Fantasy", 162, 9.8, 2012, null);
        Movie saved = movieService.addMovie(movie);

        assertNotNull(saved.getId());
        assertEquals("Harry Potter", saved.getTitle());
    }

    @Test
    void testAddMovie_duplicateTitle_throws() {
        Movie movie = new Movie(null, "Harry Potter", "Fantasy", 162, 9.8, 2012, null);
        movieService.addMovie(movie);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            movieService.addMovie(movie);
        });

        assertEquals(409, ex.getStatusCode().value());
    }

    @Test
    void testDeleteMovie_success() {
        Movie movie = new Movie(null, "Harry Potter", "Fantasy", 162, 9.8, 2012, null);
        movieRepository.save(movie);

        movieService.deleteMovie("Harry Potter");

        assertTrue(movieRepository.findByTitle("Harry Potter").isEmpty());
    }

    @Test
    void testDeleteMovie_notFound() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            movieService.deleteMovie("NonExistMovie");
        });

        assertEquals(404, ex.getStatusCode().value());
    }

    @Test
    void testUpdateMovie_success() {
        Movie original = new Movie(null, "Harry Potter", "Fantasy", 162, 9.8, 2012, null);
        movieRepository.save(original);

        Movie updated = new Movie(null, "Harry Potter", "Comedy", 162, 8.5, 2012, null);
        Movie result = movieService.updateMovie("Harry Potter", updated);

        assertEquals("Comedy", result.getGenre());
        assertEquals(8.5, result.getRating());
    }

    @Test
    void testUpdateMovie_duplicateTitle_throws() {
        Movie movie = new Movie(null, "Harry Potter", "Fantasy", 162, 9.8, 2012, null);
        movieRepository.save(movie);

        Movie original = new Movie(null, "Shrek", "Fantasy", 180, 6.8, 2005, null);
        movieRepository.save(original);

        Movie updated = new Movie(null, "Harry Potter", "Fantasy", 180, 6.8, 2005, null);
        
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            movieService.updateMovie("Shrek", updated);
        });
        assertEquals(409, ex.getStatusCode().value());
    }

    @Test
    void testUpdateMovie_nonExistingMovie_throws(){
        Movie updated = new Movie(null, "Harry Potter", "Fantasy", 180, 6.8, 2005, null);
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            movieService.updateMovie("NonExistMovie", updated);
        });
        assertEquals(404, ex.getStatusCode().value());
    }
}
