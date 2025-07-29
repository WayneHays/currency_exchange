package com.currency_exchange.entity;

public record ExchangeRateData(ExchangeRate exchangeRate, Currency baseCurrency, Currency targetCurrency) {
}
