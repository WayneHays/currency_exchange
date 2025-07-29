package com.currency_exchange.config;

import com.currency_exchange.dao.CurrencyDao;
import com.currency_exchange.dao.ExchangeRatesDao;
import com.currency_exchange.service.CalculationService;
import com.currency_exchange.service.CurrencyService;
import com.currency_exchange.service.ExchangeRateService;

public class ApplicationContext {
    private final CurrencyService currencyService;
    private final ExchangeRateService exchangeRateService;
    private final CalculationService calculationService;

    public ApplicationContext() {
        CurrencyDao currencyDao = new CurrencyDao();
        ExchangeRatesDao exchangeRatesDao = new ExchangeRatesDao();
        this.currencyService = new CurrencyService(currencyDao);
        this.exchangeRateService = new ExchangeRateService(currencyDao, exchangeRatesDao);
        this.calculationService = new CalculationService(currencyDao, exchangeRatesDao);
    }

    public CurrencyService getCurrencyService() {
        return currencyService;
    }

    public ExchangeRateService getExchangeRateService() {
        return exchangeRateService;
    }

    public CalculationService getCalculationService() {
        return calculationService;
    }
}
