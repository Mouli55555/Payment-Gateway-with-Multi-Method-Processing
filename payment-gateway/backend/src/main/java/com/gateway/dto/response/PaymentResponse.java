package com.gateway.dto.response;

import com.gateway.models.Payment;

import java.time.Instant;

public class PaymentResponse {

    private String id;
    private String order_id;
    private Integer amount;
    private String currency;
    private String method;
    private String status;

    // UPI
    private String vpa;

    // Card
    private String card_network;
    private String card_last4;

    private Instant created_at;
    private Instant updated_at;

    public static PaymentResponse from(Payment p) {
        PaymentResponse r = new PaymentResponse();

        r.id = p.getId();
        r.order_id = p.getOrderId();
        r.amount = p.getAmount();
        r.currency = p.getCurrency();
        r.method = p.getMethod();
        r.status = p.getStatus();
        r.vpa = p.getVpa();
        r.card_network = p.getCardNetwork();
        r.card_last4 = p.getCardLast4();
        r.created_at = p.getCreatedAt();
        r.updated_at = p.getUpdatedAt();

        return r;
    }

    // getters only (no setters)
    public String getId() { return id; }
    public String getOrder_id() { return order_id; }
    public Integer getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public String getMethod() { return method; }
    public String getStatus() { return status; }
    public String getVpa() { return vpa; }
    public String getCard_network() { return card_network; }
    public String getCard_last4() { return card_last4; }
    public Instant getCreated_at() { return created_at; }
    public Instant getUpdated_at() { return updated_at; }
}
