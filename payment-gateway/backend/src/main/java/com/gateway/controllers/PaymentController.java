package com.gateway.controllers;

import com.gateway.dto.request.CreatePaymentRequest;
import com.gateway.dto.response.PaymentResponse;
import com.gateway.exception.NotFoundException;
import com.gateway.models.Merchant;
import com.gateway.models.Payment;
import com.gateway.repositories.PaymentRepository;
import com.gateway.security.MerchantContext;
import com.gateway.services.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;

    public PaymentController(
            PaymentService paymentService,
            PaymentRepository paymentRepository
    ) {
        this.paymentService = paymentService;
        this.paymentRepository = paymentRepository;
    }

    /* =============================
       CREATE PAYMENT
       ============================= */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponse createPayment(
            @RequestBody CreatePaymentRequest request
    ) {
        Merchant merchant = MerchantContext.get();

        Payment payment = paymentService.createPayment(request, merchant);

        return PaymentResponse.from(payment);
    }

    /* =============================
       GET PAYMENT
       ============================= */
    @GetMapping("/{paymentId}")
    public PaymentResponse getPayment(@PathVariable String paymentId) {

        Merchant merchant = MerchantContext.get();

        Payment payment = paymentRepository
                .findByIdAndMerchantId(paymentId, merchant.getId())
                .orElseThrow(() ->
                        new NotFoundException("Payment not found")
                );

        return PaymentResponse.from(payment);
    }
}
