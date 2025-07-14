package com.currency_exchange.service.calculation_strategy;

import com.currency_exchange.dto.currency.CurrencyResponseDto;
import com.currency_exchange.dto.exchange_calculation.CalculationResponseDto;
import com.currency_exchange.entity.CurrencyPair;
import com.currency_exchange.entity.ExchangeRate;
import com.currency_exchange.service.ExchangeRateService;
import com.currency_exchange.util.Mapper;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CrossRateStrategy extends CalculationStrategy {

    public CrossRateStrategy(ExchangeRateService exchangeRateService) {
        super(exchangeRateService);
    }

    @Override
    public boolean canHandle(CurrencyPair pair) {
        return exchangeRateService.isCrossCourseExists(pair);
    }

    @Override
    public CalculationResponseDto calculate(
            CurrencyPair pair,
            BigDecimal amount,
            CurrencyResponseDto baseResponse,
            CurrencyResponseDto targetResponse) {
        ExchangeRate usdToBase = exchangeRateService.findByUsd(pair.base());
        ExchangeRate usdToTarget = exchangeRateService.findByUsd(pair.target());

        BigDecimal usdToBaseRate = usdToBase.getRate();
        BigDecimal usdToTargetRate = usdToTarget.getRate();
        BigDecimal rate = usdToTargetRate.divide(
                usdToBaseRate,
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
