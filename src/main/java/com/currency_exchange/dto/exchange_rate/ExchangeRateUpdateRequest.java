package com.currency_exchange.dto.exchange_rate;

import java.math.BigDecimal;

public record ExchangeRateUpdateRequest(BigDecimal rate) {
}
