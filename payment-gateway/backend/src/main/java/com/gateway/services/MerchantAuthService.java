package com.gateway.services;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MerchantAuthService {

    private final JdbcTemplate jdbcTemplate;

    public MerchantAuthService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<String> authenticate(String apiKey, String apiSecret) {
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(
                            "SELECT id FROM merchants WHERE api_key = ? AND api_secret = ?",
                            String.class,
                            apiKey,
                            apiSecret
                    )
            );
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
