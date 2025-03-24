package com.att.tdp.popcorn_palace.controllers;

import com.att.tdp.popcorn_palace.dto.BookingRequest;
import com.att.tdp.popcorn_palace.entities.Booking;
import com.att.tdp.popcorn_palace.services.BookingService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<?> bookSeat(@Valid @RequestBody BookingRequest request, BindingResult bindingResult) {

        // catch partial data requests and return an informative message
        if (bindingResult.hasErrors()) {
            List<String> messages = bindingResult.getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .toList();

            return ResponseEntity.badRequest().body(Map.of("errors", messages));
        }

        // in case of a valid request - create the new booking
        UUID bookingId = bookingService.createBooking(
                request.getShowtimeId(),
                request.getSeatNumber(),
                request.getUserId()
        );

        return ResponseEntity.status(200).body("{ \"bookingId\": \"" + bookingId + "\" }");
    }

    // @GetMapping
    // public ResponseEntity<List<Booking>> getAllBookings() {
    //     List<Booking> bookings = bookingService.getAllBookings();
    //     return ResponseEntity.ok(bookings);
    // }
}
