package com.indra.logistics.base.strategy;

import com.indra.logistics.base.Money;

import java.math.BigDecimal;

public abstract class AbstractCreditCardPayment implements PaymentStrategy {

    private static final BigDecimal COMMISSION_THRESHOLD = new BigDecimal("100");
    private static final BigDecimal BASE_CREDIT_CARD_TARIFF = new BigDecimal("0.03");

    private String message;

    @Override
    public Money calculateFee(BigDecimal amount) {
        if (amount.compareTo(COMMISSION_THRESHOLD) < 0) {
            message = noCommissionMessage();
            return Money.of(BigDecimal.ZERO);
        }

        message = commissionAppliedMessage();
        return Money.of(amount.multiply(creditCardTariff()));
    }

    @Override
    public String confirmationMessage() {
        return message;
    }

    protected BigDecimal creditCardTariff() {
        return BASE_CREDIT_CARD_TARIFF.add(additionalTariff());
    }

    protected BigDecimal additionalTariff() {
        return BigDecimal.ZERO;
    }

    protected abstract String noCommissionMessage();

    protected abstract String commissionAppliedMessage();

}
