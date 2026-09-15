package com.indra.logistics.base;

import com.indra.logistics.base.factory.PaymentStrategyFactoryImpl;

/**
 * BASE: toda la lógica de comisión vive en un if-else que crece con cada método de pago nuevo.
 * Agregar un método de pago implica editar esta clase y arriesgar los demás casos.
 */
public class PaymentService {

    public PaymentResult process(PaymentRequest request) {
        var method = request.method();
        var paymentStrategyFactory = new PaymentStrategyFactoryImpl();
        var paymentStrategy = paymentStrategyFactory.getStrategy(method);

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
