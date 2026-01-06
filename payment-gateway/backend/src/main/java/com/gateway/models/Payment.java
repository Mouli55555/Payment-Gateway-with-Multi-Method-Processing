package com.gateway.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "payments")
@Getter
@Setter
public class Payment {

    @Id
    private String id;

    @Column(name = "order_id", nullable = false)
    private String orderId;

    @Column(name = "merchant_id", nullable = false)
    private UUID merchantId;

    private Integer amount;
    private String currency;
    private String method;
    private String status;

    // UPI
    private String vpa;

    // Card
    private String cardNetwork;
    private String cardLast4;

    // Errors
    private String errorCode;
    private String errorDescription;

    @Column(updatable = false)
    private Instant createdAt = Instant.now();

    private Instant updatedAt = Instant.now();

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }
}
