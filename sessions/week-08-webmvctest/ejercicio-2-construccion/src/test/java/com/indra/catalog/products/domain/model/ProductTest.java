package com.indra.catalog.products.domain.model;

import com.indra.catalog.products.domain.exception.InvalidProductException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductTest {

    @Test
    @DisplayName("Conserva los valores válidos del producto y el precio exacto")
    void keepsValidProductValuesAndExactPrice() {
        Product product = new Product("PRD-0001", "Laptop", new BigDecimal("3500000.00"), 10);

        assertThat(product.id()).isEqualTo("PRD-0001");
        assertThat(product.name()).isEqualTo("Laptop");
        assertThat(product.price()).isEqualByComparingTo("3500000.00");
        assertThat(product.stock()).isEqualTo(10);
    }

    @Test
    @DisplayName("Rechaza un precio no positivo")
    void rejectsNonPositivePrice() {
        assertThatThrownBy(() -> new Product("PRD-0001", "Laptop", BigDecimal.ZERO, 10))
                .isInstanceOf(InvalidProductException.class)
                .hasMessageContaining("price");
    }

    @Test
    @DisplayName("Rechaza nombres vacíos o demasiado largos")
    void rejectsBlankOrLongNames() {
        assertThatThrownBy(() -> new Product("PRD-0001", " ", BigDecimal.ONE, 0))
                .isInstanceOf(InvalidProductException.class)
                .hasMessageContaining("name");
        assertThatThrownBy(() -> new Product("PRD-0001", "a".repeat(121), BigDecimal.ONE, 0))
                .isInstanceOf(InvalidProductException.class)
                .hasMessageContaining("120");
    }

    @Test
    @DisplayName("Rechaza un inventario negativo")
    void rejectsNegativeStock() {
        assertThatThrownBy(() -> new Product("PRD-0001", "Laptop", BigDecimal.ONE, -1))
                .isInstanceOf(InvalidProductException.class)
                .hasMessageContaining("stock");
    }
}
