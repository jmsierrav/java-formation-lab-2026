package com.indra.logistics.base.strategy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DebitcardPaymentTest {

    private final DebitcardPayment strategy = new DebitcardPayment();

    @Test
    @DisplayName("expone el codigo del metodo")
    void shouldReturnMethodCode() {
        assertEquals("DEBIT_CARD", strategy.methodCode());
    }

    @DisplayName("calcula la comision de tarjeta debito")
    @ParameterizedTest(name = "monto {0} -> comision {1}")
    @CsvSource({
            "50.00, 2.00",
            "100.00, 4.00",
            "250.50, 10.02"
    })
    void shouldCalculateFee(String rawAmount, String expectedFee) {
        assertEquals(new BigDecimal(expectedFee), strategy.calculateFee(new BigDecimal(rawAmount)).amount());
    }

    @Test
    @DisplayName("retorna el mensaje de confirmacion")
    void shouldReturnConfirmationMessage() {
        assertEquals("Pago con tarjeta de débito procesado, se aplica comisión bancaria.", strategy.confirmationMessage());
    }
}
