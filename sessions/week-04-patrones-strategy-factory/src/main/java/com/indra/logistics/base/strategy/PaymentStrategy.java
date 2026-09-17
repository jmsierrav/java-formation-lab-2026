package com.indra.logistics.base.strategy;

import com.indra.logistics.base.Money;

import java.math.BigDecimal;

/** Contrato Strategy: cada método de pago sabe calcular su propia comisión y mensaje. */
public interface PaymentStrategy {

    String methodCode();

    Money calculateFee(BigDecimal amount);

    String confirmationMessage();

}
