package com.currency_exchange.dto.calculation;

import java.math.BigDecimal;

public record CalculationRequestDto(String from, String to, BigDecimal amount) {
}
