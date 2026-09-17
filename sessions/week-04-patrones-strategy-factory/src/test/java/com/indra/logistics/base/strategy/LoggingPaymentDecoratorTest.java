package com.indra.logistics.base.strategy;

import com.indra.logistics.base.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LoggingPaymentDecoratorTest {

    @Test
    @DisplayName("el decorador conserva la lógica de la estrategia base")
    void shouldKeepDelegateBehavior() {
        PaymentStrategy strategy = new LoggingPaymentDecorator(new CashPayment());

        assertEquals("CASH", strategy.methodCode());
        assertEquals(Money.of(BigDecimal.ZERO), strategy.calculateFee(new BigDecimal("120.00")));
        assertEquals("Pago en efectivo registrado, sin comisión.", strategy.confirmationMessage());
    }
}
