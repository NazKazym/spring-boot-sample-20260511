package com.example.auth.controller;

import com.example.auth.model.User;
import com.example.auth.repository.UserRepository;
import com.example.auth.service.ProcessService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ProcessController {

    private final ProcessService processService;
    private final UserRepository userRepository;

    public ProcessController(ProcessService processService, UserRepository userRepository) {
        this.processService = processService;
        this.userRepository = userRepository;
    }

    public record ProcessRequest(String text) {}
    public record ProcessResponse(String result) {}

    @PostMapping("/process")
    public ProcessResponse process(@RequestBody ProcessRequest request) {
        // 1. Identify the authenticated user from the SecurityContext
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Delegate the transformation and logging to the Service layer
        String result = processService.processText(user.getId(), request.text());

        return new ProcessResponse(result);
    }
}