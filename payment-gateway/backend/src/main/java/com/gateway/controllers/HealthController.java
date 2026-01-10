package com.gateway.controllers;

import com.gateway.services.HealthService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HealthController {

    private final HealthService healthService;

    public HealthController(HealthService healthService) {
        this.healthService = healthService;
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of(
                "status", "ok",
                "database", healthService.databaseStatus(),
                "redis", healthService.redisStatus(),
                "worker", healthService.workerStatus()
        );
    }
}
