package com.gateway.dto.response;

import java.util.UUID;

public record TestMerchantResponse(
        UUID id,
        String email,
        String api_key,
        boolean seeded
) {}
