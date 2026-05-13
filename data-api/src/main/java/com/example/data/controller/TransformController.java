package com.example.data.controller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TransformController {
    private static final Logger log = LoggerFactory.getLogger(TransformController.class);

    public record TransformRequest(
            @NotBlank(message = "Text cannot be empty")
            @Size(min = 1, max = 500, message = "Text length must be between 1 and 500 characters")
            String text
    ) {}
    public record TransformResponse(String transformedText) {}

    @PostMapping("/transform")
    public TransformResponse transform(@RequestBody TransformRequest request) {
        log.info("Received transformation request for text length: {}",
                request.text() != null ? request.text().length() : 0);

        try {
            String transformed = new StringBuilder(request.text().toUpperCase()).reverse().toString();
            log.debug("Transformation successful: {} -> {}", request.text(), transformed);
            return new TransformResponse(transformed);
        } catch (Exception e) {
            log.error("Transformation failed for input: {}", request.text(), e);
            throw e;
        }
    }
}