package com.indra.logistics.base.strategy;

import com.indra.logistics.base.Money;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class VisaPayment implements PaymentStrategy {

    private String message;

    @Override
    public String methodCode() {
        return "VISA";
    }

    @Override
    public Money calculateFee(BigDecimal amount) {
        Money fee;

        if (amount.compareTo(BigDecimal.valueOf(100)) < 0) {
            fee = Money.of(BigDecimal.ZERO);
            message = "Pago con tarjeta de crédito Visa procesado, monto no aplica comisión bancaria.";
        } else {
            var tariff = creditCardTariff().add(new BigDecimal("0.005"));
            fee = Money.of(amount.multiply(tariff));
            message = "Pago con tarjeta de crédito Visa procesado, se aplica comisión bancaria.";
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
