package com.currency_exchange.dto.exchange_rate;

import java.util.List;

public record ExchangeRateListResponse(List<ExchangeRateResponse> exchangeRates) {
}
