package com.indra.logistics.base.strategy;

import com.indra.logistics.base.Money;

import java.math.BigDecimal;
import java.util.Objects;

public abstract class PaymentStrategyDecorator implements PaymentStrategy {

    protected final PaymentStrategy delegate;

    protected PaymentStrategyDecorator(PaymentStrategy delegate) {
        this.delegate = Objects.requireNonNull(delegate, "delegate no puede ser null");
    }

    @Override
    public String methodCode() {
        return delegate.methodCode();
    }

    @Override
    public Money calculateFee(BigDecimal amount) {
        return delegate.calculateFee(amount);
    }

    @Override
    public String confirmationMessage() {
        return delegate.confirmationMessage();
    }

}
