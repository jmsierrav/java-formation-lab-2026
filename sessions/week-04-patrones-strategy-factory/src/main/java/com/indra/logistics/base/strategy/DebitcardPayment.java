package com.indra.logistics.base.strategy;

import com.indra.logistics.base.Money;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DebitcardPayment implements PaymentStrategy {

    @Override
    public String methodCode() {
        return "DEBIT_CARD";
    }

    @Override
    public Money calculateFee(BigDecimal amount) {
        return Money.of(amount.multiply(new BigDecimal("0.04")));
    }

    @Override
    public String confirmationMessage() {
        return "Pago con tarjeta de débito procesado, se aplica comisión bancaria.";
    }

}
