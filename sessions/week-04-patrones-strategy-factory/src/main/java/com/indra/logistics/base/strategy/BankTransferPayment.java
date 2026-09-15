package com.indra.logistics.base.strategy;

import com.indra.logistics.base.Money;

import java.math.BigDecimal;

public class BankTransferPayment implements PaymentStrategy {

    @Override
    public String methodCode() {
        return "BANK_TRANSFER";
    }

    @Override
    public Money calculateFee(BigDecimal amount) {
        return Money.of(amount.multiply(new BigDecimal("0.025")));
    }

    @Override
    public String confirmationMessage() {
        return "Pago por transferencia bancaria registrado, comisión bancaria aplicada.";
    }

}
