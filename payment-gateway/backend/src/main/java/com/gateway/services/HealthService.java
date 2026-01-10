package com.gateway.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class HealthService {

    private final JdbcTemplate jdbcTemplate;

    @Autowired(required = false)
    private RedisConnectionFactory redisConnectionFactory;

    public HealthService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /* ===============================
       DATABASE CHECK
       =============================== */
    public String databaseStatus() {
        try {
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            return "connected";
        } catch (Exception e) {
            return "disconnected";
        }
    }

    /* ===============================
       REDIS CHECK (SAFE)
       =============================== */
    public String redisStatus() {
        if (redisConnectionFactory == null) {
            return "disconnected";
        }

        try {
            redisConnectionFactory.getConnection().ping();
            return "connected";
        } catch (Exception e) {
            return "disconnected";
        }
    }

    /* ===============================
       WORKER STATUS (STATIC)
       =============================== */
    public String workerStatus() {
        return "disabled";
    }
}
