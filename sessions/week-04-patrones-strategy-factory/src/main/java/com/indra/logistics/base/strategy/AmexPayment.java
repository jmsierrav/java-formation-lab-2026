package com.indra.logistics.base.strategy;

import org.springframework.stereotype.Component;

@Component
public class AmexPayment extends AbstractCreditCardPayment {

    @Override
    public String methodCode() {
        return "AMEX";
    }

    @Override
    protected String noCommissionMessage() {
        return "Pago con tarjeta American Express procesado, monto no aplica comisión bancaria.";
    }

    @Override
    protected String commissionAppliedMessage() {
        return "Pago con tarjeta American Express procesado, se aplica comisión bancaria.";
    }

}
