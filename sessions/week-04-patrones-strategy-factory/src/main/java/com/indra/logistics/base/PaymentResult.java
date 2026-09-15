package com.indra.logistics.base;

import java.math.BigDecimal;

public record PaymentResult(String method, BigDecimal amount, Money fee, Money total, String message) {
}
