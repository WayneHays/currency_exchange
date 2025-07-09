package com.currency_exchange.dto.exchange_calculation;

import com.currency_exchange.dto.currency.CurrencyResponse;

import java.math.BigDecimal;

public record ExchangeCalculationResponse(
        CurrencyResponse baseCurrency,
        CurrencyResponse targetCurrency,
        BigDecimal rate,
        BigDecimal amount,
        BigDecimal convertedAmount) {
}
