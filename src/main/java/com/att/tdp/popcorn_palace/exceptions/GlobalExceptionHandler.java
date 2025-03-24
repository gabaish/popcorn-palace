package com.att.tdp.popcorn_palace.exceptions;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // catch json parsing errors and return an informative message
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        String message = "Invalid request format.";

        Throwable cause = ex.getMostSpecificCause();
        if (cause != null) {
            String causeMsg = cause.getMessage();
            if (cause instanceof java.time.format.DateTimeParseException) {
                message = "Invalid date format. Please use yyyy-MM-dd'T'HH:mm:ss";
            } else if (causeMsg.contains("UUID")) {
                message = "Invalid UUID format. Please provide a valid UUID.";
            }
        }

        return ResponseEntity
                .badRequest()
                .body(Map.of(
                        "timestamp", java.time.LocalDateTime.now(),
                        "status", 400,
                        "error", "Bad Request",
                        "message", message
                ));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> handle(ResponseStatusException ex) {
        return ResponseEntity
                .status(ex.getStatusCode())
                .body(Map.of("message", ex.getReason()));
    }


}