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

import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
@CrossOrigin(origins = "*")
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
       CREATE PAYMENT (MERCHANT)
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
       CREATE PAYMENT (PUBLIC / CHECKOUT)
       ============================= */
    @PostMapping("/public")
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponse createPaymentPublic(
            @RequestBody CreatePaymentRequest request
    ) {
        Payment payment = paymentService.createPaymentPublic(request);
        return PaymentResponse.from(payment);
    }

    /* =============================
       GET PAYMENT (PUBLIC POLLING)
       ============================= */
    @GetMapping("/public/{paymentId}")
    public PaymentResponse getPaymentPublic(@PathVariable String paymentId) {

        Payment payment = paymentRepository
                .findById(paymentId)
                .orElseThrow(() -> new NotFoundException("Payment not found"));

        return PaymentResponse.from(payment);
    }

    /* =============================
       LIST PAYMENTS (DASHBOARD / TRANSACTIONS)
       ============================= */
    @GetMapping
    public List<PaymentResponse> listPayments() {

        Merchant merchant = MerchantContext.get();

        return paymentRepository
                .findAllByMerchantId(merchant.getId())
                .stream()
                .map(PaymentResponse::from)
                .toList();
    }
}
