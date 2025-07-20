package com.currency_exchange.dto.exchange_rate;

import com.currency_exchange.dto.currency.CurrencyCodesDto;

import java.math.BigDecimal;

public record ExchangeRateUpdateDto(CurrencyCodesDto codesDto, BigDecimal rate) {
}
