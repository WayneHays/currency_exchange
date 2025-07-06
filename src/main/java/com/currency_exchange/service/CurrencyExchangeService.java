package com.currency_exchange.service;

import com.currency_exchange.dto.currency_exchange.ExchangeCalculationRequest;

import java.math.BigDecimal;

public class CurrencyExchangeService {
    private static final CurrencyExchangeService INSTANCE = new CurrencyExchangeService();
    private final ExchangeRateService exchangeRateService = ExchangeRateService.getInstance();

    private CurrencyExchangeService() {
    }

    public static CurrencyExchangeService getInstance() {
        return INSTANCE;
    }

    public void calculate(ExchangeCalculationRequest currencyExchange) {
        String from = currencyExchange.from();
        String to = currencyExchange.to();
        BigDecimal amount = currencyExchange.amount();

    }
}
