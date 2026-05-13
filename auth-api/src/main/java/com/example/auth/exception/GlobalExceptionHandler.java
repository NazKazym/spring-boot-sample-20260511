package com.example.auth.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.sql.SQLException;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 1. Handle Validation Errors (from jakarta.validation-api)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e, HttpServletRequest request) {
        String details = e.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining(", "));

        log.warn("Validation failed at {}: {}", request.getRequestURI(), details);
        return buildResponse("Validation Failed: " + details, HttpStatus.BAD_REQUEST, request);
    }

    // 2. Handle Specific Database/Logic Errors (Prevents leaking SQL info)
    @ExceptionHandler({DataAccessException.class, SQLException.class})
    public ResponseEntity<ErrorResponse> handleDatabaseError(Exception e, HttpServletRequest request) {
        log.error("Database error at {}", request.getRequestURI(), e);
        return buildResponse("A database error occurred. Please try again later.", HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    // 3. Fallback for everything else
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(Exception e, HttpServletRequest request) {
        log.error("Unhandled exception at {}", request.getRequestURI(), e);
        return buildResponse("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    private ResponseEntity<ErrorResponse> buildResponse(String msg, HttpStatus status, HttpServletRequest request) {
        return ResponseEntity.status(status)
                .body(new ErrorResponse(msg, status.value(), System.currentTimeMillis(), request.getRequestURI()));
    }
}