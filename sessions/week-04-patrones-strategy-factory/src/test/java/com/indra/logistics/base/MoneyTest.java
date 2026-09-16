package com.indra.logistics.base;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MoneyTest {

    @DisplayName("redondea el monto a dos decimales")
    @ParameterizedTest(name = "monto {0} -> {1}")
    @CsvSource({
            "10, 10.00",
            "10.234, 10.23",
            "10.235, 10.24"
    })
    void shouldRoundAmountToTwoDecimals(String rawAmount, String expectedAmount) {
        Money money = Money.of(new BigDecimal(rawAmount));

        assertEquals(new BigDecimal(expectedAmount), money.amount());
    }

    @DisplayName("rechaza montos negativos")
    @ParameterizedTest(name = "monto invalido {0}")
    @ValueSource(strings = {"-0.01", "-10", "-100.99"})
    void shouldRejectNegativeAmounts(String rawAmount) {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Money.of(new BigDecimal(rawAmount))
        );

        assertEquals("El precio no puede ser negativo", exception.getMessage());
    }

    @Test
    @DisplayName("rechaza monto nulo")
    void shouldRejectNullAmount() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Money.of(null)
        );

        assertEquals("El precio no puede ser null", exception.getMessage());
    }
}
