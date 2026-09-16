package com.indra.logistics.base.strategy;

import com.indra.logistics.base.Money;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class VisaPayment extends AbstractCreditCardPayment {

    @Override
    public String methodCode() {
        return "VISA";
    }

    @Override
    protected BigDecimal additionalTariff() {
        return new BigDecimal("0.005");
    }

    @Override
    protected String noCommissionMessage() {
        return "Pago con tarjeta de crédito Visa procesado, monto no aplica comisión bancaria.";
    }

    @Override
    protected String commissionAppliedMessage() {
        return "Pago con tarjeta de crédito Visa procesado, se aplica comisión bancaria.";
    }

}
