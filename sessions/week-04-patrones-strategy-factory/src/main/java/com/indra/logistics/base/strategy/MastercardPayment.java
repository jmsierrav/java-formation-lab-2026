package com.indra.logistics.base.strategy;

import org.springframework.stereotype.Component;

@Component
public class MastercardPayment extends AbstractCreditCardPayment {

    @Override
    public String methodCode() {
        return "MASTERCARD";
    }

    @Override
    protected String noCommissionMessage() {
        return "Pago con tarjeta de crédito Mastercard procesado, monto no aplica comisión bancaria.";
    }

    @Override
    protected String commissionAppliedMessage() {
        return "Pago con tarjeta de crédito Mastercard procesado, se aplica comisión bancaria.";
    }

}
