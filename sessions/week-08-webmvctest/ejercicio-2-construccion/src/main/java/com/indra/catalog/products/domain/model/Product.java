package com.indra.catalog.products.domain.model;

import com.indra.catalog.products.domain.exception.InvalidProductException;
import java.math.BigDecimal;

public record Product(String id, String name, BigDecimal price, int stock) {

    public Product {
        if (id == null || id.isBlank()) {
            throw new InvalidProductException("id: es obligatorio");
        }
        if (name == null || name.isBlank()) {
            throw new InvalidProductException("name: es obligatorio");
        }
        if (name.length() > 120) {
            throw new InvalidProductException("name: no puede superar 120 caracteres");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidProductException("price: debe ser mayor que 0");
        }
        if (stock < 0) {
            throw new InvalidProductException("stock: debe ser mayor o igual que 0");
        }
    }
}
