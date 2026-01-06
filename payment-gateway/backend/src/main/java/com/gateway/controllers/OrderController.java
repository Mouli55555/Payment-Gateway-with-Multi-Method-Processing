package com.gateway.controllers;

import com.gateway.dto.request.CreateOrderRequest;
import com.gateway.dto.response.OrderResponse;
import com.gateway.models.Merchant;
import com.gateway.models.Order;
import com.gateway.security.MerchantContext;
import com.gateway.services.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(@Valid @RequestBody CreateOrderRequest request) {

        Merchant merchant = MerchantContext.get();
        Order order = orderService.createOrder(request, merchant);

        return OrderResponse.builder()
                .id(order.getId())
                .merchant_id(order.getMerchantId())
                .amount(order.getAmount())
                .currency(order.getCurrency())
                .receipt(order.getReceipt())
                .notes(order.getNotes())
                .status(order.getStatus())
                .created_at(order.getCreatedAt())
                .build();

    }

    @GetMapping("/{orderId}")
    public OrderResponse getOrder(@PathVariable String orderId) {

        Merchant merchant = MerchantContext.get();
        Order order = orderService.getOrder(orderId, merchant.getId());

        return OrderResponse.builder()
                .id(order.getId())
                .amount(order.getAmount())
                .currency(order.getCurrency())
                .receipt(order.getReceipt())
                .status(order.getStatus())
                .build();
    }
}
