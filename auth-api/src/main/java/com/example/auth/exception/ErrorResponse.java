package com.example.auth.exception;

public record ErrorResponse(
        String message,
        int status,
        long timestamp,
        String path
) {}
