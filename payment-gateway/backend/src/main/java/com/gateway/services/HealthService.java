package com.gateway.services;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class HealthService {

    private final JdbcTemplate jdbcTemplate;

    @Autowired(required = false)
    private RedisConnectionFactory redisConnectionFactory;

    // Worker state flag
    private final AtomicBoolean workerRunning = new AtomicBoolean(false);

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

    public String redisStatus() {
        if (redisConnectionFactory == null) {
            return "disconnected";
        }

        try {
            if (redisConnectionFactory instanceof LettuceConnectionFactory lettuce) {
                lettuce.afterPropertiesSet();
            }

            redisConnectionFactory.getConnection().ping();
            return "connected";

        } catch (Exception e) {
            e.printStackTrace(); // TEMP for debugging
            return "disconnected";
        }
    }



    /* ===============================
       WORKER STATUS
       =============================== */
    public String workerStatus() {
        return workerRunning.get() ? "running" : "stopped";
    }

    public void markWorkerRunning() {
        workerRunning.set(true);
    }

    public void markWorkerStopped() {
        workerRunning.set(false);
    }

    @PostConstruct
    public void debugRedis() {
        System.out.println(">>> RedisConnectionFactory = " + redisConnectionFactory);
    }

    @PostConstruct
    public void startWorker() {
        if (redisConnectionFactory == null) {
            System.out.println(">>> Worker not started (Redis not configured)");
            return;
        }

        Thread workerThread = new Thread(() -> {
            try {
                markWorkerRunning();
                System.out.println(">>> Worker started");

                while (true) {
                    try {
                        // Heartbeat to Redis (optional but ideal)
                        redisConnectionFactory
                                .getConnection()
                                .set("worker:heartbeat".getBytes(),
                                        String.valueOf(System.currentTimeMillis()).getBytes());

                        Thread.sleep(5000); // 5 sec heartbeat
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } finally {
                markWorkerStopped();
            }
        });

        workerThread.setDaemon(true); // important
        workerThread.setName("gateway-worker");
        workerThread.start();
    }



}
