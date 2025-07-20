package com.currency_exchange.service.calculation_strategy;

import com.currency_exchange.dao.ExchangeRatesDao;
import com.currency_exchange.dto.currency.CurrencyResponseDto;
import com.currency_exchange.dto.exchange_calculation.CalculationResponseDto;
import com.currency_exchange.entity.Currency;

import java.math.BigDecimal;
import java.math.RoundingMode;

public abstract class CalculationStrategy {
    protected static final int PRE_ROUNDING = 6;
    protected static final int RESULT_ROUNDING = 2;

    protected final ExchangeRatesDao exchangeRatesDao;

    protected CalculationStrategy(ExchangeRatesDao exchangeRatesDao) {
        this.exchangeRatesDao = exchangeRatesDao;
    }

    public abstract boolean canHandle(Currency base, Currency target);

    public abstract CalculationResponseDto calculate(Currency base,
                                                     Currency target,
                                                     BigDecimal amount,
                                                     CurrencyResponseDto baseResponse,
                                                     CurrencyResponseDto targetResponse);

    protected BigDecimal round(BigDecimal convertedAmount) {
        return convertedAmount.setScale(RESULT_ROUNDING, RoundingMode.HALF_UP);
    }
}