package com.indra.catalog.products.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.indra.catalog.products.domain.exception.InvalidProductException;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class ProductTest {

    @Test
    void keepsValidProductValuesAndExactPrice() {
        Product product = new Product("PRD-0001", "Laptop", new BigDecimal("3500000.00"), 10);

        assertThat(product.id()).isEqualTo("PRD-0001");
        assertThat(product.name()).isEqualTo("Laptop");
        assertThat(product.price()).isEqualByComparingTo("3500000.00");
        assertThat(product.stock()).isEqualTo(10);
    }

    @Test
    void rejectsNonPositivePrice() {
        assertThatThrownBy(() -> new Product("PRD-0001", "Laptop", BigDecimal.ZERO, 10))
                .isInstanceOf(InvalidProductException.class)
                .hasMessageContaining("price");
    }

    @Test
    void rejectsBlankOrLongNames() {
        assertThatThrownBy(() -> new Product("PRD-0001", " ", BigDecimal.ONE, 0))
                .isInstanceOf(InvalidProductException.class)
                .hasMessageContaining("name");
        assertThatThrownBy(() -> new Product("PRD-0001", "a".repeat(121), BigDecimal.ONE, 0))
                .isInstanceOf(InvalidProductException.class)
                .hasMessageContaining("120");
    }

    @Test
    void rejectsNegativeStock() {
        assertThatThrownBy(() -> new Product("PRD-0001", "Laptop", BigDecimal.ONE, -1))
                .isInstanceOf(InvalidProductException.class)
                .hasMessageContaining("stock");
    }
}
