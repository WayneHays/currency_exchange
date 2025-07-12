package com.currency_exchange.service.calculation_strategy;

import com.currency_exchange.dto.currency.CurrencyResponse;
import com.currency_exchange.dto.exchange_calculation.ExchangeCalculationResponse;
import com.currency_exchange.entity.CurrencyPair;
import com.currency_exchange.entity.ExchangeRate;
import com.currency_exchange.service.ExchangeRateService;

import java.math.BigDecimal;

public class CrossRateStrategy extends CalculationStrategy {

    public CrossRateStrategy(ExchangeRateService exchangeRateService) {
        super(exchangeRateService);
    }

    @Override
    public boolean canHandle(CurrencyPair pair) {
        return exchangeRateService.isCrossCourseExists(pair);
    }

    @Override
    public ExchangeCalculationResponse calculate(
            CurrencyPair pair,
            BigDecimal amount,
            CurrencyResponse baseResponse,
            CurrencyResponse targetResponse) {
        ExchangeRate usdToBase = exchangeRateService.findByUsd(pair.base());
        ExchangeRate usdToTarget = exchangeRateService.findByUsd(pair.target());

        BigDecimal usdToBaseRate = usdToBase.getRate();
        BigDecimal usdToTargetRate = usdToTarget.getRate();
        BigDecimal calculatedRate = usdToBaseRate.multiply(usdToTargetRate);
        BigDecimal convertedAmount = amount.multiply(calculatedRate);

        return new ExchangeCalculationResponse(baseResponse, targetResponse, calculatedRate, amount, convertedAmount);
    }
}
