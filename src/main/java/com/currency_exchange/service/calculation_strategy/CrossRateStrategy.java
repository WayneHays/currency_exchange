package com.currency_exchange.service.calculation_strategy;

import com.currency_exchange.dao.ExchangeRatesDao;
import com.currency_exchange.dto.calculation.CalculationResponseDto;
import com.currency_exchange.dto.currency.CurrencyResponseDto;
import com.currency_exchange.entity.Currency;
import com.currency_exchange.entity.ExchangeRate;
import com.currency_exchange.service.RateType;
import com.currency_exchange.util.Mapper;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CrossRateStrategy extends CalculationStrategy {

    public CrossRateStrategy(ExchangeRatesDao exchangeRatesDao) {
        super(exchangeRatesDao);
    }

    @Override
    public boolean canHandle(Currency base, Currency target) {
        return exchangeRatesDao.isRateExists(base, target, RateType.CROSS);
    }

    @Override
    public CalculationResponseDto calculate(
            Currency base,
            Currency target,
            BigDecimal amount,
            CurrencyResponseDto baseResponse,
            CurrencyResponseDto targetResponse) {
        ExchangeRate usdToBase = exchangeRatesDao.findByUsd(base);
        ExchangeRate usdToTarget = exchangeRatesDao.findByUsd(target);

        BigDecimal rate = usdToTarget.getRate().divide(
                usdToBase.getRate(),
                PRE_ROUNDING,
                RoundingMode.HALF_UP);
        BigDecimal convertedAmount = amount.multiply(rate);
        BigDecimal roundedAmount = round(convertedAmount);

        return Mapper.toCalculationResponseDto(
                baseResponse,
                targetResponse,
                rate,
                amount,
                roundedAmount
        );
    }
}