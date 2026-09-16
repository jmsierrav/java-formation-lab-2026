package com.indra.logistics.base.factory;

import com.indra.logistics.base.UnknownPaymentMethodException;
import com.indra.logistics.base.strategy.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/** Factory */
@Component
public class PaymentStrategyFactoryImpl implements PaymentStrategyFactory {

    private final List<PaymentStrategy> strategies;

    @Autowired
    public PaymentStrategyFactoryImpl() {
        this.strategies = List.of(
                new CashPayment(),
                new VisaPayment(),
                new MastercardPayment(),
                new AmexPayment(),
                new PaypalPayment(),
                new BankTransferPayment(),
                new DebitcardPayment()
        );
    }

    @Override
    public PaymentStrategy getStrategy(String methodCode) { 
       if (null == methodCode) {
           throw new UnknownPaymentMethodException("null");
       }

        return strategies.stream()
                .filter(strategy -> strategy.methodCode().equals(methodCode))
                .findFirst()
                .orElseThrow(() -> new UnknownPaymentMethodException(methodCode));
    }

}
