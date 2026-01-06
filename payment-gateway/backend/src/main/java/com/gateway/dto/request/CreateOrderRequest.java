package com.gateway.dto.request;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class CreateOrderRequest {

    @Min(value = 100, message = "Amount must be at least 100")
    private Integer amount;

    private String currency;
    private String receipt;
    private Map<String, Object> notes;
}
