package com.currency_exchange.service.calculation_strategy;

import com.currency_exchange.dto.currency.CurrencyResponse;
import com.currency_exchange.dto.exchange_calculation.ExchangeCalculationResponse;
import com.currency_exchange.entity.Currency;
import com.currency_exchange.entity.CurrencyPair;
import com.currency_exchange.service.ExchangeRateService;

import java.math.BigDecimal;

public abstract class CalculationStrategy {
    protected final ExchangeRateService exchangeRateService;

    protected CalculationStrategy(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    public abstract boolean canHandle(CurrencyPair pair);

    public abstract ExchangeCalculationResponse calculate(Currency base,
                                                          Currency target,
                                                          BigDecimal amount,
                                                          CurrencyResponse baseResponse,
                                                          CurrencyResponse targetResponse);
}
