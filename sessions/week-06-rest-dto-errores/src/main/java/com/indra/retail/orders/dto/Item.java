package com.indra.retail.orders.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record Item(
        @NotBlank(message = "{validation.sku.required}") String sku,
        @Min(value = 1, message = "{validation.quantity.minimum}") int quantity,
        @Min(value = 0, message = "{validation.unit-price.minimum}") double unitPrice
) {
}
