package com.indra.logistics.base.strategy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VisaPaymentTest {

    private final VisaPayment strategy = new VisaPayment();

    @Test
    @DisplayName("expone el codigo del metodo")
    void shouldReturnMethodCode() {
        assertEquals("VISA", strategy.methodCode());
    }

    @DisplayName("calcula la comision visa segun el monto")
    @ParameterizedTest(name = "monto {0} -> comision {1} y mensaje {2}")
    @CsvSource(value = {
            "99.99|0.00|Pago con tarjeta de crédito Visa procesado, monto no aplica comisión bancaria.",
            "100.00|3.50|Pago con tarjeta de crédito Visa procesado, se aplica comisión bancaria.",
            "250.00|8.75|Pago con tarjeta de crédito Visa procesado, se aplica comisión bancaria."
    }, delimiter = '|')
    void shouldCalculateFeeBasedOnAmount(String rawAmount, String expectedFee, String expectedMessage) {
        assertEquals(new BigDecimal(expectedFee), strategy.calculateFee(new BigDecimal(rawAmount)).amount());
        assertEquals(expectedMessage, strategy.confirmationMessage());
    }
}
