package com.indra.logistics.base.strategy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CashPaymentTest {

    private final CashPayment strategy = new CashPayment();

    @Test
    @DisplayName("expone el codigo del metodo")
    void shouldReturnMethodCode() {
        assertEquals("CASH", strategy.methodCode());
    }

    @DisplayName("no cobra comision para pagos en efectivo")
    @ParameterizedTest(name = "monto {0}")
    @ValueSource(strings = {"0", "50", "250.99"})
    void shouldNotChargeFee(String rawAmount) {
        assertEquals(new BigDecimal("0.00"), strategy.calculateFee(new BigDecimal(rawAmount)).amount());
    }

    @Test
    @DisplayName("retorna el mensaje de confirmacion")
    void shouldReturnConfirmationMessage() {
        assertEquals("Pago en efectivo registrado, sin comisión.", strategy.confirmationMessage());
    }
}
