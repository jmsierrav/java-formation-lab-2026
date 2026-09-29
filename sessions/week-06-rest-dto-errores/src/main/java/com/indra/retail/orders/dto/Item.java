package com.indra.retail.orders.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record Item(
        @NotBlank String sku,
        @Min(1) int quantity,
        @Min(0) double unitPrice
) {
}
