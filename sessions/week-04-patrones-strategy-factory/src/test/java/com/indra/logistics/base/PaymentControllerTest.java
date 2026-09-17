package com.indra.logistics.base;

import com.indra.logistics.base.factory.PaymentStrategyFactory;
import com.indra.logistics.base.factory.PaymentStrategyFactoryImpl;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PaymentControllerTest {

    private final PaymentStrategyFactory paymentStrategyFactory = new PaymentStrategyFactoryImpl();
    private final PaymentService paymentService = new PaymentService(paymentStrategyFactory);
    private final PaymentController paymentController = new PaymentController(paymentService);

    @Test
    void getFee_PaymentMethodWithoutAmount_Success() {
        PaymentResult result = paymentController.getFee("CASH");

        assertEquals("CASH", result.method());
        assertEquals(new BigDecimal("100"), result.amount());
        assertEquals(new BigDecimal("0.00"), result.fee().amount());
        assertEquals(new BigDecimal("100.00"), result.total().amount());
        assertEquals("Pago en efectivo registrado, sin comisión.", result.message());
    }

    @Test
    void getFee_PaymentMethodWithAmount_Success() {
        PaymentResult result = paymentController.getFee("VISA", new BigDecimal("200.00"));

        assertEquals("VISA", result.method());
        assertEquals(new BigDecimal("7.00"), result.fee().amount());
        assertEquals(new BigDecimal("207.00"), result.total().amount());
        assertEquals("Pago con tarjeta de crédito Visa procesado, se aplica comisión bancaria.", result.message());
    }

    @Test
    void getFee_InvalidPaymentMethod_BadRequest() {
        UnknownPaymentMethodException exception = null;
        try {
            paymentController.getFee("CRYPTO", new BigDecimal("100.00"));
        } catch (UnknownPaymentMethodException ex) {
            exception = ex;
        }
        ResponseEntity<Map<String, String>> response = paymentController.handleUnknownMethod(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(Map.of("error", "método de pago 'CRYPTO' no soportado"), response.getBody());
    }
}
