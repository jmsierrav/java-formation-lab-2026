package com.indra.logistics.base.strategy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AbstractCreditCardPaymentTest {

    private final TestCreditCardPayment strategy = new TestCreditCardPayment();

    @DisplayName("no cobra comision para montos menores al umbral")
    @ParameterizedTest(name = "monto {0} -> comision 0.00")
    @ValueSource(strings = {"0.00", "50.00", "99.99"})
    void shouldNotChargeCommissionBelowThreshold(String rawAmount) {
        assertEquals(new BigDecimal("0.00"), strategy.calculateFee(new BigDecimal(rawAmount)).amount());
        assertEquals("Sin comision", strategy.confirmationMessage());
    }

    @DisplayName("cobra la tarifa base cuando no hay recargo adicional")
    @ParameterizedTest(name = "monto {0} -> comision {1}")
    @CsvSource({
            "100.00,3.00",
            "250.00,7.50"
    })
    void shouldChargeBaseTariffWhenAmountReachesThreshold(String rawAmount, String expectedFee) {
        assertEquals(new BigDecimal(expectedFee), strategy.calculateFee(new BigDecimal(rawAmount)).amount());
        assertEquals("Con comision", strategy.confirmationMessage());
    }

    @Test
    @DisplayName("permite extender la tarifa con un recargo adicional")
    void shouldIncludeAdditionalTariffInCreditCardTariff() {
        AdditionalTariffCreditCardPayment strategyWithAdditionalTariff = new AdditionalTariffCreditCardPayment();

        assertEquals(new BigDecimal("0.05"), strategyWithAdditionalTariff.creditCardTariff());
        assertEquals(new BigDecimal("5.00"), strategyWithAdditionalTariff.calculateFee(new BigDecimal("100.00")).amount());
        assertEquals("Con comision adicional", strategyWithAdditionalTariff.confirmationMessage());
    }

    private static final class TestCreditCardPayment extends AbstractCreditCardPayment {

        @Override
        protected String noCommissionMessage() {
            return "Sin comision";
        }

        @Override
        protected String commissionAppliedMessage() {
            return "Con comision";
        }

        @Override
        public String methodCode() {
            return "TEST";
        }
    }

    private static final class AdditionalTariffCreditCardPayment extends AbstractCreditCardPayment {

        @Override
        protected BigDecimal additionalTariff() {
            return new BigDecimal("0.02");
        }

        @Override
        protected String noCommissionMessage() {
            return "Sin comision adicional";
        }

        @Override
        protected String commissionAppliedMessage() {
            return "Con comision adicional";
        }

        @Override
        public String methodCode() {
            return "TEST-PLUS";
        }
    }
}
