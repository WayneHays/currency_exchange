package com.currency_exchange.service.calculation_strategy;

import com.currency_exchange.dto.currency.CurrencyResponseDto;
import com.currency_exchange.dto.exchange_calculation.ExchangeCalculationResponse;
import com.currency_exchange.entity.CurrencyPair;
import com.currency_exchange.service.ExchangeRateService;

import java.math.BigDecimal;
import java.math.RoundingMode;

public abstract class CalculationStrategy {
    protected static final int PRE_ROUNDING = 6;
    protected static final int RESULT_ROUNDING = 2;

    protected final ExchangeRateService exchangeRateService;

    protected CalculationStrategy(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    public abstract boolean canHandle(CurrencyPair pair);

    public abstract ExchangeCalculationResponse calculate(CurrencyPair pair,
                                                          BigDecimal amount,
                                                          CurrencyResponseDto baseResponse,
                                                          CurrencyResponseDto targetResponse);

    protected BigDecimal round(BigDecimal convertedAmount) {
        return convertedAmount.setScale(RESULT_ROUNDING, RoundingMode.HALF_UP);
    }
}
