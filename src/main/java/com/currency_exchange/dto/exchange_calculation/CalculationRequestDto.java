package com.currency_exchange.dto.exchange_calculation;

import java.math.BigDecimal;

public record CalculationRequestDto(String from, String to, BigDecimal amount) {
}
