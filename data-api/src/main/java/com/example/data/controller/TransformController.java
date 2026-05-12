package com.example.data.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class TransformController {

    public record TransformRequest(String text) {}
    public record TransformResponse(String result) {}

    @PostMapping("/transform")
    public TransformResponse transform(@RequestBody TransformRequest request) {
        // Simple logic: Uppercase + Reverse
        String transformed = new StringBuilder(request.text().toUpperCase()).reverse().toString();
        return new TransformResponse(transformed);
    }
}