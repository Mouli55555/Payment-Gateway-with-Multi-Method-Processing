package com.gateway.services;

import com.gateway.dto.request.CreatePaymentRequest;
import com.gateway.exception.*;
import com.gateway.models.Merchant;
import com.gateway.models.Order;
import com.gateway.models.Payment;
import com.gateway.repositories.OrderRepository;
import com.gateway.repositories.PaymentRepository;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.Random;
import java.util.UUID;

@Service
public class PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final Environment env;
    private final Random random = new Random();

    public PaymentService(
            OrderRepository orderRepository,
            PaymentRepository paymentRepository,
            Environment env
    ) {
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
        this.env = env;
    }

    // =============================
    // CREATE PAYMENT
    // =============================
    public Payment createPayment(CreatePaymentRequest req, Merchant merchant) {

        // 1️⃣ Fetch & verify order
        Order order = orderRepository
                .findByIdAndMerchantId(req.getOrderId(), merchant.getId())
                .orElseThrow(() ->
                        new NotFoundException("Order not found")
                );

        // 2️⃣ Validate payment method
        String method = req.getMethod();
        if (!method.equals("upi") && !method.equals("card")) {
            throw new BadRequestException("Invalid payment method");
        }

        // 3️⃣ Create payment with PROCESSING status
        Payment payment = new Payment();
        payment.setId(generatePaymentId());
        payment.setOrderId(order.getId());
        payment.setMerchantId(merchant.getId());
        payment.setAmount(order.getAmount());
        payment.setCurrency(order.getCurrency());
        payment.setMethod(method);
        payment.setStatus("processing");

        // 4️⃣ Method-specific validation
        if (method.equals("upi")) {
            validateUpi(req.getVpa());
            payment.setVpa(req.getVpa());
        } else {
            validateCard(req);
            String clean = cleanCardNumber(req.getCard().getNumber());
            payment.setCardNetwork(detectCardNetwork(clean));
            payment.setCardLast4(clean.substring(clean.length() - 4));
        }

        // Save immediately (processing state)
        paymentRepository.save(payment);

        // 5️⃣ Simulate processing delay
        simulateDelay();

        // 6️⃣ Determine success/failure
        boolean success = determineOutcome(method);

        if (success) {
            payment.setStatus("success");
        } else {
            payment.setStatus("failed");
            payment.setErrorCode("PAYMENT_FAILED");
            payment.setErrorDescription("Payment processing failed");
        }

        return paymentRepository.save(payment);
    }

    // =============================
    // HELPER METHODS
    // =============================

    private void simulateDelay() {
        boolean testMode = Boolean.parseBoolean(env.getProperty("TEST_MODE", "false"));

        try {
            if (testMode) {
                long delay = Long.parseLong(
                        env.getProperty("TEST_PROCESSING_DELAY", "1000")
                );
                Thread.sleep(delay);
            } else {
                int delay = 5000 + random.nextInt(5000);
                Thread.sleep(delay);
            }
        } catch (InterruptedException ignored) {}
    }

    private boolean determineOutcome(String method) {
        boolean testMode = Boolean.parseBoolean(env.getProperty("TEST_MODE", "false"));

        if (testMode) {
            return Boolean.parseBoolean(
                    env.getProperty("TEST_PAYMENT_SUCCESS", "true")
            );
        }

        int chance = random.nextInt(100);
        return method.equals("upi") ? chance < 90 : chance < 95;
    }

    // =============================
    // VALIDATIONS
    // =============================

    private void validateUpi(String vpa) {
        if (vpa == null || !vpa.matches("^[a-zA-Z0-9._-]+@[a-zA-Z0-9]+$")) {
            throw new InvalidVpaException();
        }
    }

    private void validateCard(CreatePaymentRequest req) {

        if (req.getCard() == null) {
            throw ApiException.badRequest("INVALID_CARD", "Card details missing");
        }

        String number = cleanCardNumber(req.getCard().getNumber());

        // ✅ 1. Digits only + length check FIRST
        if (!number.matches("\\d{13,19}")) {
            throw ApiException.badRequest("INVALID_CARD", "Invalid card number");
        }

        // ✅ 2. Luhn check
        if (!isValidLuhn(number)) {
            throw ApiException.badRequest("INVALID_CARD", "Invalid card number");
        }

        // ✅ 3. Expiry check
        if (!isExpiryValid(
                req.getCard().getExpiryMonth(),
                req.getCard().getExpiryYear()
        )) {
            throw ApiException.badRequest("EXPIRED_CARD", "Card expired");
        }
    }


    // =============================
    // CARD UTILITIES
    // =============================

    private String cleanCardNumber(String number) {
        return number.replaceAll("[\\s-]", "");
    }

    private boolean isValidLuhn(String number) {
        int sum = 0;
        boolean alternate = false;

        for (int i = number.length() - 1; i >= 0; i--) {
            int n = number.charAt(i) - '0';
            if (alternate) {
                n *= 2;
                if (n > 9) n -= 9;
            }
            sum += n;
            alternate = !alternate;
        }
        return sum % 10 == 0;
    }


    private String detectCardNetwork(String number) {
        try {
            if (number.startsWith("4")) return "visa";

            int firstTwo = Integer.parseInt(number.substring(0, 2));

            if (firstTwo >= 51 && firstTwo <= 55) return "mastercard";
            if (firstTwo == 34 || firstTwo == 37) return "amex";
            if (firstTwo == 60 || firstTwo == 65 || (firstTwo >= 81 && firstTwo <= 89))
                return "rupay";

            return "unknown";
        } catch (Exception e) {
            return "unknown";
        }
    }


    private boolean isExpiryValid(String monthStr, String yearStr) {
        int month = Integer.parseInt(monthStr);
        int year = Integer.parseInt(yearStr.length() == 2 ? "20" + yearStr : yearStr);

        if (month < 1 || month > 12) return false;

        YearMonth expiry = YearMonth.of(year, month);
        return !expiry.isBefore(YearMonth.now());
    }

    // =============================
    // PAYMENT ID
    // =============================

    private String generatePaymentId() {
        return "pay_" + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 16);
    }
}
