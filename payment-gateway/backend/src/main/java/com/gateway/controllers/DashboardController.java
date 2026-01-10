package com.gateway.controllers;

import com.gateway.models.Merchant;
import com.gateway.models.Payment;
import com.gateway.repositories.PaymentRepository;
import com.gateway.security.MerchantContext;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    private final PaymentRepository paymentRepository;

    public DashboardController(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @GetMapping
    public Map<String, Object> getStats() {

        Merchant merchant = MerchantContext.get();

        List<Payment> payments =
                paymentRepository.findAllByMerchantId(merchant.getId());

        long totalTransactions = payments.size();

        long successfulPayments = payments.stream()
                .filter(p -> "SUCCESS".equalsIgnoreCase(p.getStatus()))
                .count();

        long totalAmount = payments.stream()
                .filter(p -> "SUCCESS".equalsIgnoreCase(p.getStatus()))
                .mapToLong(Payment::getAmount)
                .sum();

        double successRate = totalTransactions == 0
                ? 0
                : (successfulPayments * 100.0) / totalTransactions;

        return Map.of(
                "totalTransactions", totalTransactions,
                "totalAmount", totalAmount,
                "successRate", Math.round(successRate)
        );
    }
}
