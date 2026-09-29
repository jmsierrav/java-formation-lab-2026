package com.indra.retail.orders.web;

import com.indra.retail.orders.dto.CreateOrderRequest;
import com.indra.retail.orders.dto.OrderResponse;
import com.indra.retail.orders.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody CreateOrderRequest createOrderRequest) {
        var orderResponse = orderService.create(createOrderRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(orderResponse);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getById(@PathVariable String orderId) {
        var orderResponse = orderService.findById(orderId);

        return ResponseEntity.ok(orderResponse);
    }

}
