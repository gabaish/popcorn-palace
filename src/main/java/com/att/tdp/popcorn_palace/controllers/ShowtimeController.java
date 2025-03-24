package com.att.tdp.popcorn_palace.controllers;


import com.att.tdp.popcorn_palace.entities.Showtime;
import com.att.tdp.popcorn_palace.services.ShowtimeService;

import jakarta.validation.Valid;

import com.att.tdp.popcorn_palace.dto.ShowtimeRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/showtimes")
@RequiredArgsConstructor
public class ShowtimeController {
    private final ShowtimeService showtimeService;

    @PostMapping
    public ResponseEntity<?> addShowtime(@Valid @RequestBody ShowtimeRequest request, BindingResult bindingResult) {
        // catch partial data requests and return an informative message
        if (bindingResult.hasErrors()) {
            List<String> messages = bindingResult.getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .toList();

            return ResponseEntity.badRequest().body(Map.of("errors", messages));
        }

        // in case of a valid request - add the new showtime
        Showtime showtime = showtimeService.addShowtime(
            request.getMovieId(),
            request.getTheater(),
            request.getStartTime(),
            request.getEndTime(),
            request.getPrice()
        );
        return ResponseEntity.status(HttpStatus.OK).body(showtime);
    }

    @PostMapping("/{id}")
    public ResponseEntity<?> editShowtime(@PathVariable Long id, @Valid @RequestBody ShowtimeRequest request, BindingResult bindingResult) {
        // catch partial data requests and return an informative message
        if (bindingResult.hasErrors()) {
            List<String> messages = bindingResult.getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .toList();

            return ResponseEntity.badRequest().body(Map.of("errors", messages));
        }

        // in case of a valid request - edit the showtime
        Showtime updatedShowtime = showtimeService.editShowtime(
            id,
            request.getMovieId(),
            request.getTheater(),
            request.getStartTime(),
            request.getEndTime(),
            request.getPrice()
        );
        return ResponseEntity.ok(updatedShowtime);
    }
    

    @GetMapping("/{id}")
    public ResponseEntity<Showtime> getShowtime(@PathVariable Long id) {
        return showtimeService.getShowtimeById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "No showtime with the given ID exists in the system. Please verify the ID."
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShowtime(@PathVariable Long id) {
        showtimeService.deleteShowtime(id);
        return ResponseEntity.ok().build();
    }

    // @GetMapping
    // public List<Showtime> getAllShowtimes() {
    //     return showtimeService.getAllShowtimes();
    // }
}
