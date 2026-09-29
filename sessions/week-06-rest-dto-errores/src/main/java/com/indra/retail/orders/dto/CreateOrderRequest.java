package com.indra.retail.orders.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateOrderRequest(
        @NotBlank String customerId,
        @NotEmpty List<Item> items,
        @Size(min = 10) String deliveryAddress
) {
}
