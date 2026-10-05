package com.indra.retail.orders.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateOrderRequest(
        @NotBlank(message = "{validation.customer-id.required}") String customerId,
        @NotEmpty(message = "{validation.items.required}") List<@Valid Item> items,
        @Size(min = 10, message = "{validation.delivery-address.size}") String deliveryAddress
) {
}
