package com.indra.catalog.products.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record CreateProductRequest(
        @NotBlank(message = "es obligatorio") @Size(max = 120, message = "no puede superar 120 caracteres")
        String name,
        @NotNull(message = "es obligatorio") @DecimalMin(value = "0", inclusive = false, message = "debe ser mayor que 0")
        BigDecimal price,
        @NotNull(message = "es obligatorio") @PositiveOrZero(message = "debe ser mayor o igual que 0")
        Integer stock) {
}
