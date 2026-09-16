package com.indra.logistics.base;

import com.indra.logistics.base.factory.PaymentStrategyFactory;
import com.indra.logistics.base.strategy.PaymentStrategy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PaymentServiceTest {

    private final PaymentStrategyFactory paymentStrategyFactory = mock(PaymentStrategyFactory.class);
    private final PaymentStrategy paymentStrategy = mock(PaymentStrategy.class);
    private final PaymentService paymentService = new PaymentService(paymentStrategyFactory);

    @Test
    @DisplayName("procesa el pago usando la estrategia seleccionada")
    void shouldProcessPaymentUsingSelectedStrategy() {
        PaymentRequest request = new PaymentRequest(new BigDecimal("200.00"), "VISA");
        Money fee = Money.of(new BigDecimal("7.00"));

        when(paymentStrategyFactory.getStrategy("VISA")).thenReturn(paymentStrategy);
        when(paymentStrategy.calculateFee(new BigDecimal("200.00"))).thenReturn(fee);
        when(paymentStrategy.confirmationMessage()).thenReturn("Pago con tarjeta de crédito Visa procesado, se aplica comisión bancaria.");

        PaymentResult result = paymentService.process(request);

        assertEquals("VISA", result.method());
        assertEquals(new BigDecimal("200.00"), result.amount());
        assertEquals(new BigDecimal("7.00"), result.fee().amount());
        assertEquals(new BigDecimal("207.00"), result.total().amount());
        assertEquals("Pago con tarjeta de crédito Visa procesado, se aplica comisión bancaria.", result.message());
        verify(paymentStrategyFactory).getStrategy("VISA");
        verify(paymentStrategy).calculateFee(new BigDecimal("200.00"));
        verify(paymentStrategy).confirmationMessage();
    }
}
