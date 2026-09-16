package com.indra.logistics.base.strategy;

import com.indra.logistics.base.Money;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class MastercardPayment implements PaymentStrategy {

    private String message;

    @Override
    public String methodCode() {
        return "MASTERCARD";
    }

    @Override
    public Money calculateFee(BigDecimal amount) {
        Money fee;

        if (amount.compareTo(BigDecimal.valueOf(100)) < 0) {
            fee = Money.of(BigDecimal.ZERO);
            message = "Pago con tarjeta de crédito Mastercard procesado, monto no aplica comisión bancaria.";
        } else {
            BigDecimal tariff = creditCardTariff().add(BigDecimal.ZERO); // TODO
            fee = Money.of(amount.multiply(tariff).setScale(2, RoundingMode.HALF_UP));
            message = "Pago con tarjeta de crédito Mastercard procesado, se aplica comisión bancaria.";
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
