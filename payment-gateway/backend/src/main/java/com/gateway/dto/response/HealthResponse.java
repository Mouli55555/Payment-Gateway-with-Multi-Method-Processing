package com.gateway.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.Instant;

public class HealthResponse {

    private final String status;
    private final String database;
    private final String redis;
    private final String worker;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private final Instant timestamp;

    public HealthResponse(
            String status,
            String database,
            String redis,
            String worker,
            Instant timestamp
    ) {
        this.status = status;
        this.database = database;
        this.redis = redis;
        this.worker = worker;
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public String getDatabase() {
        return database;
    }

    public String getRedis() {
        return redis;
    }

    public String getWorker() {
        return worker;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
