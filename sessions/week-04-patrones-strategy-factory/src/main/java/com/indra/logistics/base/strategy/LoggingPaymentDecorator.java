package com.indra.logistics.base.strategy;

import com.indra.logistics.base.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

public class LoggingPaymentDecorator extends PaymentStrategyDecorator {

    private static final Logger logger = LoggerFactory.getLogger(LoggingPaymentDecorator.class);

    public LoggingPaymentDecorator(PaymentStrategy delegate) {
        super(delegate);
    }

    @Override
    public Money calculateFee(BigDecimal amount) {
        var fee = super.calculateFee(amount);
        logger.info("Comisión calculada: {}", fee.amount());
        return fee;
    }

    @Override
    public String confirmationMessage() {
        var message = super.confirmationMessage();
        logger.info("Confirmación generada: {}", message);
        return message;
    }

}
