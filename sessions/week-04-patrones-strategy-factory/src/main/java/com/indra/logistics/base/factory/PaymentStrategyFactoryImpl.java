package com.indra.logistics.base.factory;


import com.indra.logistics.base.UnknownPaymentMethodException;
import com.indra.logistics.base.strategy.*;
import org.springframework.stereotype.Component;

/** Factory */
@Component
public class PaymentStrategyFactoryImpl implements PaymentStrategyFactory {

    @Override
    public PaymentStrategy getStrategy(String methodCode) { 
       if (null == methodCode) {
           throw new UnknownPaymentMethodException("null");
       }

        return switch (methodCode) {
            case "AMEX" -> new AmexPayment();
            case "BANK_TRANSFER" -> new BankTransferPayment();
            case "CASH" -> new CashPayment();
            case "DEBIT_CARD" -> new DebitcardPayment();
            case "MASTERCARD" -> new MastercardPayment();
            case "PAYPAL" -> new PaypalPayment();
            case "VISA" -> new VisaPayment();
            default -> throw new UnknownPaymentMethodException(methodCode);
        };
    }

}
