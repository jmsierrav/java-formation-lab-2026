package com.indra.logistics.base.strategy;

import com.indra.logistics.base.Money;

import java.math.BigDecimal;

public class AmexPayment implements PaymentStrategy {

    private String message;

    @Override
    public String methodCode() {
        return "AMEX";
    }

    @Override
    public Money calculateFee(BigDecimal amount) {
        Money fee;

        if (amount.compareTo(BigDecimal.valueOf(100)) < 0) {
            fee = Money.of(BigDecimal.ZERO);
            message = "Pago con tarjeta American Express procesado, monto no aplica comisión bancaria.";
        } else {
            BigDecimal tariff = creditCardTariff().add(BigDecimal.ZERO); // TODO
            fee = Money.of(amount.multiply(tariff));
            message = "Pago con tarjeta American Express procesado, se aplica comisión bancaria.";
        }

        return fee;
    }

    @Override
    public String confirmationMessage() {
        return message;
    }

    private BigDecimal creditCardTariff() {
        return new BigDecimal("0.03");
    }

}
