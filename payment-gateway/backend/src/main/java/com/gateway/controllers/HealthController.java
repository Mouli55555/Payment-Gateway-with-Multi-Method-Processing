package com.gateway.controllers;

import com.gateway.dto.response.HealthResponse;
import com.gateway.services.HealthService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
public class HealthController {

    private final HealthService healthService;

    public HealthController(HealthService healthService) {
        this.healthService = healthService;
    }

    @GetMapping("/health")
    public HealthResponse health() {

        return new HealthResponse(
                "healthy",
                healthService.databaseStatus(),
                healthService.redisStatus(),
                healthService.workerStatus(),
                Instant.now()
        );
    }
}
