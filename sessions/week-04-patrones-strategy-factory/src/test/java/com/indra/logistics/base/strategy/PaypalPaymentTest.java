package com.indra.logistics.base.strategy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PaypalPaymentTest {

    private final PaypalPayment strategy = new PaypalPayment();

    @Test
    @DisplayName("expone el codigo del metodo")
    void shouldReturnMethodCode() {
        assertEquals("PAYPAL", strategy.methodCode());
    }

    @DisplayName("calcula la comision de paypal")
    @ParameterizedTest(name = "monto {0} -> comision {1}")
    @CsvSource({
            "50.00, 1.00",
            "100.00, 2.00",
            "333.33, 6.67"
    })
    void shouldCalculateFee(String rawAmount, String expectedFee) {
        assertEquals(new BigDecimal(expectedFee), strategy.calculateFee(new BigDecimal(rawAmount)).amount());
    }

    @Test
    @DisplayName("retorna el mensaje de confirmacion")
    void shouldReturnConfirmationMessage() {
        assertEquals("Pago con PayPal procesado, comisión de plataforma aplicada.", strategy.confirmationMessage());
    }
}
