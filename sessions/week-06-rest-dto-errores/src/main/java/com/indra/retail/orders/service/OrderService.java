package com.indra.retail.orders.service;

import com.indra.retail.orders.dto.CreateOrderRequest;
import com.indra.retail.orders.dto.OrderResponse;
import com.indra.retail.orders.exception.OrderNotFoundException;
import com.indra.retail.orders.model.Order;
import com.indra.retail.orders.model.OrderItem;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OrderService {

    private final Map<String, Order> orders = new ConcurrentHashMap<>();

    public OrderResponse create(CreateOrderRequest createOrderRequest) {
        List<OrderItem> items = createOrderRequest.items().stream()
                .map(item -> new OrderItem(item.sku(), item.quantity(), item.unitPrice()))
                .toList();
        var order = new Order(createOrderRequest.customerId(), items, createOrderRequest.deliveryAddress());

        orders.put(order.getId(), order);

        return new OrderResponse(order.getId(), order.getStatus(), order.getTotalAmount(), order.getEstimatedDelivery());
    }

    public OrderResponse findById(String orderId) {
        var order = orders.get(orderId);

        if (order == null) {
            throw new OrderNotFoundException(orderId);
        }

        return new OrderResponse(order.getId(), order.getStatus(), order.getTotalAmount(), order.getEstimatedDelivery());
    }

}
