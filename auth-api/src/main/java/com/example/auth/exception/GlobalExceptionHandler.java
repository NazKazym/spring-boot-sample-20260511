package com.example.auth.exception; // Repeat for data-api package

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    public record ErrorResponse(String message, long timestamp) {}

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(Exception e) {
        return ResponseEntity.status(500)
                .body(new ErrorResponse(e.getMessage(), System.currentTimeMillis()));
    }
}