package com.gateway.services;

import com.gateway.dto.request.CreatePaymentRequest;
import com.gateway.exception.*;
import com.gateway.models.Merchant;
import com.gateway.models.Order;
import com.gateway.models.Payment;
import com.gateway.repositories.MerchantRepository;
import com.gateway.repositories.OrderRepository;
import com.gateway.repositories.PaymentRepository;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final MerchantRepository merchantRepository;
    private final Environment env;

    private final Random random = new Random();

    public PaymentService(
            OrderRepository orderRepository,
            PaymentRepository paymentRepository,
            MerchantRepository merchantRepository,
            Environment env
    ) {
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
        this.merchantRepository = merchantRepository;
        this.env = env;
    }

    // =============================
    // PUBLIC PAYMENT (CHECKOUT)
    // =============================
    public Payment createPaymentPublic(CreatePaymentRequest request) {

        Order order = orderRepository.findById(request.getOrder_id())
                .orElseThrow(() -> new NotFoundException("Order not found"));

        Merchant merchant = merchantRepository.findById(order.getMerchantId())
                .orElseThrow(() -> new NotFoundException("Merchant not found"));

        return createPayment(request, merchant);
    }

    // =============================
    // CREATE PAYMENT (MERCHANT)
    // =============================
    public Payment createPayment(CreatePaymentRequest req, Merchant merchant) {

        Order order = orderRepository
                .findByIdAndMerchantId(req.getOrderId(), merchant.getId())
                .orElseThrow(() -> new NotFoundException("Order not found"));

        String method = req.getMethod();
        if (!method.equals("upi") && !method.equals("card")) {
            throw new BadRequestException("Invalid payment method");
        }

        Payment payment = new Payment();
        payment.setId(generatePaymentId());
        payment.setOrderId(order.getId());
        payment.setMerchantId(merchant.getId());
        payment.setAmount(order.getAmount());
        payment.setCurrency(order.getCurrency());
        payment.setMethod(method);
        payment.setStatus("PROCESSING");

        if (method.equals("upi")) {
            validateUpi(req.getVpa());
            payment.setVpa(req.getVpa());
        } else {
            validateCard(req);
            String clean = cleanCardNumber(req.getCard().getNumber());
            payment.setCardNetwork(detectCardNetwork(clean));
            payment.setCardLast4(clean.substring(clean.length() - 4));
        }

        paymentRepository.save(payment);

        // ✅ ASYNC PAYMENT PROCESSING (NON-BLOCKING)
        CompletableFuture.runAsync(() -> processPaymentAsync(payment.getId(), method));

        return payment;
    }

    // =============================
    // ASYNC PAYMENT SIMULATION
    // =============================
    private void processPaymentAsync(String paymentId, String method) {

        simulateDelay();
        boolean success = determineOutcome(method);

        Payment payment = paymentRepository.findById(paymentId).orElse(null);
        if (payment == null) return;

        if (success) {
            payment.setStatus("SUCCESS");
        } else {
            payment.setStatus("FAILED");
            payment.setErrorCode("PAYMENT_FAILED");
            payment.setErrorDescription("Payment processing failed");
        }

        paymentRepository.save(payment);
    }

    // =============================
    // SIMULATION HELPERS
    // =============================
    private void simulateDelay() {
        boolean testMode = Boolean.parseBoolean(env.getProperty("TEST_MODE", "false"));
        try {
            if (testMode) {
                Thread.sleep(Long.parseLong(
                        env.getProperty("TEST_PROCESSING_DELAY", "1000")
                ));
            } else {
                Thread.sleep(3000 + random.nextInt(3000));
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

        if (!number.matches("\\d{13,19}")) {
            throw ApiException.badRequest("INVALID_CARD", "Invalid card number");
        }

        if (!isValidLuhn(number)) {
            throw ApiException.badRequest("INVALID_CARD", "Invalid card number");
        }

        if (!isExpiryValid(
                req.getCard().getExpiryMonth(),
                req.getCard().getExpiryYear()
        )) {
            throw ApiException.badRequest("EXPIRED_CARD", "Card expired");
        }
    }

    // =============================
    // CARD UTILS
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
        if (number.startsWith("4")) return "visa";
        int firstTwo = Integer.parseInt(number.substring(0, 2));
        if (firstTwo >= 51 && firstTwo <= 55) return "mastercard";
        if (firstTwo == 34 || firstTwo == 37) return "amex";
        if (firstTwo == 60 || firstTwo == 65) return "rupay";
        return "unknown";
    }

    private boolean isExpiryValid(String monthStr, String yearStr) {
        int month = Integer.parseInt(monthStr);
        int year = Integer.parseInt(yearStr.length() == 2 ? "20" + yearStr : yearStr);
        return !YearMonth.of(year, month).isBefore(YearMonth.now());
    }

    private String generatePaymentId() {
        return "pay_" + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 16);
    }
}
