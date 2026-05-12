package com.example.auth.service;

import com.example.auth.model.ProcessingLog;
import com.example.auth.repository.ProcessingLogRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;
import java.util.UUID;

@Service
public class ProcessService {
    private final RestClient restClient;
    private final ProcessingLogRepository logRepository;

    @Value("${INTERNAL_TOKEN}")
    private String internalToken;

    public ProcessService(ProcessingLogRepository logRepository) {
        this.logRepository = logRepository;
        this.restClient = RestClient.builder().baseUrl("http://data-api:8081").build();
    }

    public record TransformResponse(String result) {}

    public String processText(UUID userId, String text) {
        TransformResponse response = restClient.post()
                .uri("/api/transform")
                .header("X-Internal-Token", internalToken)
                .body(Map.of("text", text))
                .retrieve()
                .body(TransformResponse.class);

        logRepository.save(new ProcessingLog(userId, text, response.result()));
        return response.result();
    }
}