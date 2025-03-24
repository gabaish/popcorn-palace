package com.att.tdp.popcorn_palace.controllers;

import com.att.tdp.popcorn_palace.entities.Movie;
import com.att.tdp.popcorn_palace.services.MovieService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/movies")
public class MovieController {
    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping("/all")
    public List<Movie> getAllMovies() {
        return movieService.getAllMovies();
    }

    @PostMapping
    public ResponseEntity<?> addMovie(@Valid @RequestBody Movie movie, BindingResult bindingResult) {
        // catch partial data requests and return an informative message
        if (bindingResult.hasErrors()) {
            List<String> messages = bindingResult.getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .toList();

            return ResponseEntity.badRequest().body(Map.of("errors", messages));
        }
        return ResponseEntity.ok(movieService.addMovie(movie));
    }

    @PostMapping("/{movieTitle}")
    public ResponseEntity<?> updateMovie(@PathVariable String movieTitle,@Valid @RequestBody Movie updatedMovie, BindingResult bindingResult) {
        // catch partial data requests and return an informative message
        if (bindingResult.hasErrors()) {
            List<String> messages = bindingResult.getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .toList();

            return ResponseEntity.badRequest().body(Map.of("errors", messages));
        }
        Movie movie = movieService.updateMovie(movieTitle, updatedMovie);
        return ResponseEntity.ok(movie);
    }

    @DeleteMapping("/{movieTitle}")
    public ResponseEntity<Void> deleteMovie(@PathVariable String movieTitle) {
        movieService.deleteMovie(movieTitle);
        return ResponseEntity.ok().build();
    }
}
