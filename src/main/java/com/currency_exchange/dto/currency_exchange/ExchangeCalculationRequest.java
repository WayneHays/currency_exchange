package com.currency_exchange.dto.currency_exchange;

import java.math.BigDecimal;

public record ExchangeCalculationRequest(String from, String to, BigDecimal amount) {
}
