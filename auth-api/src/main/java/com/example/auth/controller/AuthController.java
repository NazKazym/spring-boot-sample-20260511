package com.example.auth.controller;

import com.example.auth.model.User;
import com.example.auth.repository.UserRepository;
import com.example.auth.security.JwtService;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(UserRepository userRepository,
                          BCryptPasswordEncoder passwordEncoder,
                          JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public record LoginRequest(
            @NotBlank(message = "Email is required")
            @Email(message = "Email should be valid")
            String email,

            @NotBlank(message = "Password is required")
            @Size(min = 8, message = "Password must be at least 8 characters long")
            String password
    ) {}

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody LoginRequest request) {
        log.info("Received registration request for email: {}", request.email());
        if (userRepository.findByEmail(request.email()).isPresent()) {
            log.warn("Registration failed: Email {} already exists", request.email());
            return ResponseEntity.badRequest().body("Email already exists");
        }
        User user = new User(request.email(), passwordEncoder.encode(request.password()));
        userRepository.save(user);
        log.info("User {} successfully registered", request.email());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        log.info("Login attempt for email: {}", request.email());

        return userRepository.findByEmail(request.email())
                .filter(user -> {
                    boolean matches = passwordEncoder.matches(request.password(), user.getPassword());
                    if (!matches) log.warn("Invalid password for user: {}", request.email());
                    return matches;
                })
                .map(user -> {
                    log.info("User {} logged in successfully", request.email());
                    return ResponseEntity.ok(Map.of("token", jwtService.generateToken(user.getEmail())));
                })
                .orElseGet(() -> {
                    log.warn("Login failed for user: {} (User not found or incorrect credentials)", request.email());
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
                });
    }
}