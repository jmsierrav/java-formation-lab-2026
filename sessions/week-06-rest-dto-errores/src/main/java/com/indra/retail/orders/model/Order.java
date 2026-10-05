package com.indra.retail.orders.model;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class Order {

    private String id;
    private String customerId;
    private List<OrderItem> items;
    private String deliveryAddress;
    private OrderStatus status;
    private double totalAmount;
    private LocalDate estimatedDelivery;
    private String internalWarehouseCode;
    private String createdByEmployeeId;

    public Order() {
    }

    public Order(String customerId, List<OrderItem> items, String deliveryAddress) {
        this.id = UUID.randomUUID().toString();
        this.customerId = customerId;
        this.items = items;
        this.deliveryAddress = deliveryAddress;
        this.status = OrderStatus.CREATED;
        this.totalAmount = items == null ? 0.0
                : items.stream().mapToDouble(i -> i.getUnitPrice() * i.getQuantity()).sum();
        this.estimatedDelivery = LocalDate.now().plusDays(5);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDate getEstimatedDelivery() {
        return estimatedDelivery;
    }

    public void setEstimatedDelivery(LocalDate estimatedDelivery) {
        this.estimatedDelivery = estimatedDelivery;
    }

    public String getInternalWarehouseCode() {
        return internalWarehouseCode;
    }

    public void setInternalWarehouseCode(String internalWarehouseCode) {
        this.internalWarehouseCode = internalWarehouseCode;
    }

    public String getCreatedByEmployeeId() {
        return createdByEmployeeId;
    }

    public void setCreatedByEmployeeId(String createdByEmployeeId) {
        this.createdByEmployeeId = createdByEmployeeId;
    }
}
