package com.gateway.services;

import com.gateway.dto.request.CreateOrderRequest;
import com.gateway.exception.BadRequestException;
import com.gateway.exception.NotFoundException;
import com.gateway.models.Merchant;
import com.gateway.models.Order;
import com.gateway.repositories.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order createOrder(CreateOrderRequest req, Merchant merchant) {

        // 🔴 Business validation (extra safety, even though @Valid exists)
        if (req.getAmount() == null || req.getAmount() < 100) {
            throw new BadRequestException("amount must be at least 100");
        }

        Order order = new Order();
        order.setId(generateOrderId());
        order.setAmount(req.getAmount());
        order.setCurrency(req.getCurrency() != null ? req.getCurrency() : "INR");
        order.setReceipt(req.getReceipt());
        order.setNotes(req.getNotes());
        order.setMerchantId(merchant.getId());

        return orderRepository.save(order);
    }

    public Order getOrder(String orderId, UUID merchantId) {
        return orderRepository
                .findByIdAndMerchantId(orderId, merchantId)
                .orElseThrow(() -> new NotFoundException("Order not found"));
    }

    private String generateOrderId() {
        return "order_" + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 14);
    }
    public Order getOrderPublic(String orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));
    }

}
