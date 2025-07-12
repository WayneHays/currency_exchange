package com.currency_exchange.service.calculation_strategy;

import com.currency_exchange.dto.currency.CurrencyResponse;
import com.currency_exchange.dto.exchange_calculation.ExchangeCalculationResponse;
import com.currency_exchange.entity.CurrencyPair;
import com.currency_exchange.entity.ExchangeRate;
import com.currency_exchange.service.ExchangeRateService;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ReverseRateStrategy extends CalculationStrategy {
    private static final int PRECISION = 6;

    public ReverseRateStrategy(ExchangeRateService exchangeRateService) {
        super(exchangeRateService);
    }

    @Override
    public boolean canHandle(CurrencyPair pair) {
        return exchangeRateService.isReversedExchangeRateExists(pair);
    }

    @Override
    public ExchangeCalculationResponse calculate(
            CurrencyPair pair,
            BigDecimal amount,
            CurrencyResponse baseResponse,
            CurrencyResponse targetResponse) {
        ExchangeRate exchangeRate = exchangeRateService.findEntityByPair(pair);
        BigDecimal directRate = exchangeRate.getRate();
        BigDecimal reversedRate = BigDecimal.ONE.divide(directRate, PRECISION, RoundingMode.HALF_UP);
        BigDecimal convertedAmount = amount.multiply(reversedRate);

        return new ExchangeCalculationResponse(baseResponse, targetResponse, reversedRate, amount, convertedAmount);
    }
}
