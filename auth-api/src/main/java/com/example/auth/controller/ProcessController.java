package com.example.auth.controller;

import com.example.auth.model.User;
import com.example.auth.repository.UserRepository;
import com.example.auth.service.ProcessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@RestController
@RequestMapping("/api")
public class ProcessController {

    private static final Logger log = LoggerFactory.getLogger(ProcessController.class);

    private final ProcessService processService;
    private final UserRepository userRepository;

    public ProcessController(ProcessService processService, UserRepository userRepository) {
        this.processService = processService;
        this.userRepository = userRepository;
    }

    public record ProcessRequest(String text) {}
    public record ProcessResponse(String result) {}

    @PostMapping("/process")
    public ProcessResponse process(
            @RequestBody ProcessRequest request,
            @AuthenticationPrincipal String email // Spring injects the principal from SecurityContext
    ) {
        log.info("Process request started for user: {}", email);

        // 1. Get User ID (Keep this lookup if userId is required for Service B logging)
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("Authenticated user {} not found in database", email);
                    return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User record missing");
                });

        // 2. Delegate to Service
        try {
            log.debug("Sending text to Service B for processing. UserID: {}", user.getId());
            String result = processService.processText(user.getId(), request.text());

            log.info("Process request completed successfully for user: {}", email);
            return new ProcessResponse(result);
        } catch (Exception e) {
            log.error("Processing failed for user {}: {}", email, e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error communicating with downstream service");
        }
    }
}