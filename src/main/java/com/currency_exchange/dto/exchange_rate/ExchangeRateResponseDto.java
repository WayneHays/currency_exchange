package com.currency_exchange.dto.exchange_rate;

import com.currency_exchange.dto.currency.CurrencyResponseDto;

import java.math.BigDecimal;

public record ExchangeRateResponseDto(Long id,
                                      CurrencyResponseDto baseCurrency,
                                      CurrencyResponseDto targetCurrency,
                                      BigDecimal rate) {
}
