package com.att.tdp.popcorn_palace.services;

import com.att.tdp.popcorn_palace.entities.Movie;
import com.att.tdp.popcorn_palace.repositories.MovieRepository;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class MovieService {

    private static final Logger logger = LoggerFactory.getLogger(MovieService.class);

    private final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }
    
    public Movie addMovie(Movie movie) {
        // check if a movie with the same name already exist (avoid duplicates)
        if (movieRepository.existsByTitle(movie.getTitle())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A movie with this title already exists in the system. To update its details, please use the appropriate endpoint for editing.");
        }
        Movie savedMovie = movieRepository.save(movie);
        logger.info("New movie addded: {} (ID: {})", savedMovie.getTitle(), savedMovie.getId());
        return savedMovie;
    }

    public Movie updateMovie(String title, Movie updatedMovie) {
        Movie existingMovie = movieRepository.findByTitle(title)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "The requested movie does not exist in the system. Please verify the movie title or add it as a new entry."));

        // Check if the new title is already used by another movie
        if (!existingMovie.getTitle().equals(updatedMovie.getTitle()) &&
            movieRepository.existsByTitle(updatedMovie.getTitle())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot update the movie title because the new title already exists in the system. Please choose a unique title.");
        }   

        // Update movie details
        existingMovie.setTitle(updatedMovie.getTitle());
        existingMovie.setGenre(updatedMovie.getGenre());
        existingMovie.setDuration(updatedMovie.getDuration());
        existingMovie.setRating(updatedMovie.getRating());
        existingMovie.setReleaseYear(updatedMovie.getReleaseYear());

        Movie savedMovie = movieRepository.save(existingMovie);
        logger.info("Movie {} has been updated", title);
        return savedMovie;
    }

    @Transactional
    public void deleteMovie(String title) {
        // check if the movie exits in the system
        if (!movieRepository.existsByTitle(title)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No movie with the specified title exists in the system.");
        }
        
        movieRepository.deleteByTitle(title);
        logger.info("Movie {} has been deleted", title);
    }

    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }
    
}
