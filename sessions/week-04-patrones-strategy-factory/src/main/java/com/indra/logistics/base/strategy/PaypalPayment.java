package com.indra.logistics.base.strategy;

import com.indra.logistics.base.Money;

import java.math.BigDecimal;

public class PaypalPayment implements PaymentStrategy {

    @Override
    public String methodCode() {
        return "PAYPAL";
    }

    @Override
    public Money calculateFee(BigDecimal amount) {
        return Money.of(amount.multiply(new BigDecimal("0.02")));
    }

    @Override
    public String confirmationMessage() {
        return "Pago con PayPal procesado, comisión de plataforma aplicada.";
    }

}
