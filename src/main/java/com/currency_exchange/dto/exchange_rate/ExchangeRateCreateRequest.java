package com.currency_exchange.dto.exchange_rate;

import java.math.BigDecimal;

public record ExchangeRateCreateRequest(String baseCurrencyCode, String targetCurrencyCode, BigDecimal rate) {
}
