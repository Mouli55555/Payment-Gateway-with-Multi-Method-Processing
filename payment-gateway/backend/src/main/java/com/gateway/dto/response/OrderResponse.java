package com.gateway.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Getter
@Builder
public class OrderResponse {

    private String id;
    private UUID merchant_id;
    private Integer amount;
    private String currency;
    private String receipt;
    private Map<String, Object> notes;
    private String status;
    private Instant created_at;
}
