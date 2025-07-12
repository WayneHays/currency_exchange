package com.currency_exchange.service.calculation_strategy;

import com.currency_exchange.dto.currency.CurrencyResponse;
import com.currency_exchange.dto.exchange_calculation.ExchangeCalculationResponse;
import com.currency_exchange.entity.CurrencyPair;
import com.currency_exchange.entity.ExchangeRate;
import com.currency_exchange.service.ExchangeRateService;

import java.math.BigDecimal;

public class DirectRateStrategy extends CalculationStrategy {

    public DirectRateStrategy(ExchangeRateService exchangeRateService) {
        super(exchangeRateService);
    }

    @Override
    public boolean canHandle(CurrencyPair pair) {
        return exchangeRateService.isExchangeRateExists(pair);
    }

    @Override
    public ExchangeCalculationResponse calculate(CurrencyPair pair, BigDecimal amount, CurrencyResponse baseResponse, CurrencyResponse targetResponse) {
        ExchangeRate exchangeRate = exchangeRateService.findEntityByPair(pair);
        BigDecimal rate = exchangeRate.getRate();
        BigDecimal convertedAmount = amount.multiply(rate);

        return new ExchangeCalculationResponse(baseResponse, targetResponse, rate, amount, convertedAmount);
    }
}
