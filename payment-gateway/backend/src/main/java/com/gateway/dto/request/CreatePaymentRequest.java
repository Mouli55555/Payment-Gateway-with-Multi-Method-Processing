package com.gateway.dto.request;

import jakarta.validation.constraints.NotBlank;

public class CreatePaymentRequest {

    @NotBlank
    private String order_id;

    @NotBlank
    private String method;

    // UPI
    private String vpa;

    // Card
    private CardDetails card;

    // ===== getters =====
    public String getOrderId() {
        return order_id;
    }

    public String getMethod() {
        return method;
    }

    public String getVpa() {
        return vpa;
    }

    public CardDetails getCard() {
        return card;
    }

    // ===== setters =====
    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setVpa(String vpa) {
        this.vpa = vpa;
    }

    public void setCard(CardDetails card) {
        this.card = card;
    }

    public String getOrder_id() {
        return order_id;
    }
}
