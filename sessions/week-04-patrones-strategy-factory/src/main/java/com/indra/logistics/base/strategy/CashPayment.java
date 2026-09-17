package com.indra.logistics.base.strategy;

import com.indra.logistics.base.Money;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class CashPayment implements PaymentStrategy {

    @Override
    public String methodCode() {
        return "CASH";
    }

    @Override
    public Money calculateFee(BigDecimal amount) {
        return Money.of(BigDecimal.ZERO);
    }

    @Override
    public String confirmationMessage() {
        return "Pago en efectivo registrado, sin comisión.";
    }

}
