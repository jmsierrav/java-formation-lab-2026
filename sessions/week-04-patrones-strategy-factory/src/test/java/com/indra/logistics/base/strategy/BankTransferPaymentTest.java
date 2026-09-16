package com.indra.logistics.base.strategy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BankTransferPaymentTest {

    private final BankTransferPayment strategy = new BankTransferPayment();

    @Test
    @DisplayName("expone el codigo del metodo")
    void shouldReturnMethodCode() {
        assertEquals("BANK_TRANSFER", strategy.methodCode());
    }

    @DisplayName("calcula la comision por transferencia")
    @ParameterizedTest(name = "monto {0} -> comision {1}")
    @CsvSource({
            "50.00, 1.25",
            "100.00, 2.50",
            "333.33, 8.33"
    })
    void shouldCalculateFee(String rawAmount, String expectedFee) {
        assertEquals(new BigDecimal(expectedFee), strategy.calculateFee(new BigDecimal(rawAmount)).amount());
    }

    @Test
    @DisplayName("retorna el mensaje de confirmacion")
    void shouldReturnConfirmationMessage() {
        assertEquals("Pago por transferencia bancaria registrado, comisión bancaria aplicada.", strategy.confirmationMessage());
    }
}
