package com.currency_exchange.dto.exchange_rate;

import com.currency_exchange.dto.currency.CurrencyResponse;

import java.math.BigDecimal;

public record ExchangeRateResponse(Long id, CurrencyResponse baseCurrency, CurrencyResponse targetCurrency,
                                   BigDecimal rate) {
}
