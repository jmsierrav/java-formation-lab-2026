package com.indra.logistics.base;

import com.indra.logistics.base.factory.PaymentStrategyFactory;
import com.indra.logistics.base.strategy.LoggingPaymentDecorator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * BASE: toda la lógica de comisión vive en un if-else que crece con cada método de pago nuevo.
 * Agregar un método de pago implica editar esta clase y arriesgar los demás casos.
 */
@Service
public class PaymentService {

    private final PaymentStrategyFactory paymentStrategyFactory;

    @Autowired
    public PaymentService(PaymentStrategyFactory paymentStrategyFactory) {
        this.paymentStrategyFactory = paymentStrategyFactory;
    }

    public PaymentResult process(PaymentRequest request) {
        var method = request.method();
        var paymentStrategy = new LoggingPaymentDecorator(paymentStrategyFactory.getStrategy(method));

        var amount = request.amount();
        var fee = paymentStrategy.calculateFee(amount);
        var total = Money.of(amount.add(fee.amount()));

        return new PaymentResult(
                method,
                amount,
                fee,
                total,
                paymentStrategy.confirmationMessage()
        );
    }

}
