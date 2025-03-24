package com.att.tdp.popcorn_palace.services;


import com.att.tdp.popcorn_palace.entities.Showtime;
import com.att.tdp.popcorn_palace.repositories.MovieRepository;
import com.att.tdp.popcorn_palace.repositories.ShowtimeRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;

import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ShowtimeService {

    private static final Logger logger = LoggerFactory.getLogger(MovieService.class);

    private final ShowtimeRepository showtimeRepository;
    private final MovieRepository movieRepository;

    public Showtime addShowtime(Long movieId, String theater, LocalDateTime startTime, LocalDateTime endTime, double price) {
        // check if the movie exist in the system
        movieRepository.findById(movieId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot create showtime. The specified movie does not exist in the system. Please verify the movie ID"));

        // Validate No Overlapping Showtimes in the Same Theater
        long conflicts = showtimeRepository.countOverlappingShowtimes(theater, startTime, endTime, null);
        if (conflicts > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Showtime overlaps with another showtime in the same theater.");
        }

        Showtime showtime = new Showtime(null, movieId, theater, startTime, endTime, price);
        Showtime savedShowtime = showtimeRepository.save(showtime);
        logger.info("New showtime addded with ID: {})", savedShowtime.getId());
        return savedShowtime;
    }

    public Showtime editShowtime(Long id, Long movieId, String theater, LocalDateTime startTime, LocalDateTime endTime, double price) {
        // Find showtime or throw error if it doesn't exist
        Showtime showtime = showtimeRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No showtime with the given ID exists in the system"));

        // Validate Movie exists
        movieRepository.findById(movieId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "The specified movie does not exist in the system. Please verify the movie ID"));

        // Validate No Overlapping Showtimes in the Same Theater
        long conflicts = showtimeRepository.countOverlappingShowtimes(theater, startTime, endTime, id);
        if (conflicts > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Showtime overlaps with another showtime in the same theater.");
        }

        // Update Showtime Details
        showtime.setMovieId(movieId);
        showtime.setTheater(theater);
        showtime.setStartTime(startTime);
        showtime.setEndTime(endTime);
        showtime.setPrice(price);

        // Save and return the updated showtime
        Showtime savedShowtime = showtimeRepository.save(showtime);
        logger.info("Showtime with ID: {} has been updated)", savedShowtime.getId());
        return savedShowtime;
    }

    @Transactional
    public void deleteShowtime(Long showtimeId) {
        // Validate Showtime exists
        if (!showtimeRepository.existsById(showtimeId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No showtime with the specified id exists in the system.");
        }
        
        showtimeRepository.deleteById(showtimeId);
        logger.info("Showtime with ID: {} has been deleted)", showtimeId);
    }

    // public List<Showtime> getAllShowtimes() {
    //     return showtimeRepository.findAll();
    // }

    public Optional<Showtime> getShowtimeById(Long id) {
        return showtimeRepository.findById(id);
    }
}
