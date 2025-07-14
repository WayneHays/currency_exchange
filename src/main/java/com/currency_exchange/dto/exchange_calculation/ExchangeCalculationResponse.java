package com.currency_exchange.dto.exchange_calculation;

import com.currency_exchange.dto.currency.CurrencyResponseDto;

import java.math.BigDecimal;

public record ExchangeCalculationResponse(
        CurrencyResponseDto baseCurrency,
        CurrencyResponseDto targetCurrency,
        BigDecimal rate,
        BigDecimal amount,
        BigDecimal convertedAmount) {
}
