package com.currency_exchange.dto.exchange_calculation;

import com.currency_exchange.dto.currency.CurrencyResponseDto;

import java.math.BigDecimal;

public record CalculationResponseDto(
        CurrencyResponseDto baseCurrency,
        CurrencyResponseDto targetCurrency,
        BigDecimal rate,
        BigDecimal amount,
        BigDecimal convertedAmount) {
}
