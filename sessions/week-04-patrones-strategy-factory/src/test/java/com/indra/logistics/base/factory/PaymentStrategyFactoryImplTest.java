package com.indra.logistics.base.factory;

import com.indra.logistics.base.UnknownPaymentMethodException;
import com.indra.logistics.base.strategy.PaymentStrategy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PaymentStrategyFactoryImplTest {

    private final PaymentStrategyFactoryImpl factory = new PaymentStrategyFactoryImpl();

    @DisplayName("retorna la estrategia correcta para cada metodo soportado")
    @ParameterizedTest(name = "metodo {0} -> {1}")
    @CsvSource({
            "CASH, CashPayment",
            "VISA, VisaPayment",
            "MASTERCARD, MastercardPayment",
            "AMEX, AmexPayment",
            "PAYPAL, PaypalPayment",
            "BANK_TRANSFER, BankTransferPayment",
            "DEBIT_CARD, DebitcardPayment"
    })
    void shouldReturnExpectedStrategyForSupportedMethods(String methodCode, String strategyName) {
        PaymentStrategy strategy = factory.getStrategy(methodCode);

        assertEquals(strategyName, strategy.getClass().getSimpleName());
    }

    @DisplayName("falla cuando el metodo de pago no esta soportado")
    @ParameterizedTest(name = "metodo invalido {0}")
    @ValueSource(strings = {"CRYPTO", "PSE", "visa"})
    void shouldThrowForUnsupportedMethod(String methodCode) {
        UnknownPaymentMethodException exception = assertThrows(
                UnknownPaymentMethodException.class,
                () -> factory.getStrategy(methodCode)
        );

        assertEquals("método de pago '%s' no soportado".formatted(methodCode), exception.getMessage());
    }

    @Test
    @DisplayName("falla cuando el metodo es nulo")
    void shouldThrowForNullMethod() {
        UnknownPaymentMethodException exception = assertThrows(
                UnknownPaymentMethodException.class,
                () -> factory.getStrategy(null)
        );

        assertEquals("método de pago 'null' no soportado", exception.getMessage());
    }
}
