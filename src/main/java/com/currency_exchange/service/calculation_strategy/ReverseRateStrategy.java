package com.currency_exchange.service.calculation_strategy;

import com.currency_exchange.dto.currency.CurrencyResponseDto;
import com.currency_exchange.dto.exchange_calculation.CalculationResponseDto;
import com.currency_exchange.entity.CurrencyPair;
import com.currency_exchange.entity.ExchangeRate;
import com.currency_exchange.service.ExchangeRateService;
import com.currency_exchange.util.Mapper;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ReverseRateStrategy extends CalculationStrategy {

    public ReverseRateStrategy(ExchangeRateService exchangeRateService) {
        super(exchangeRateService);
    }

    @Override
    public boolean canHandle(CurrencyPair pair) {
        return exchangeRateService.isReversedExchangeRateExists(pair);
    }

    @Override
    public CalculationResponseDto calculate(
            CurrencyPair pair,
            BigDecimal amount,
            CurrencyResponseDto baseResponse,
            CurrencyResponseDto targetResponse) {
        ExchangeRate exchangeRate = exchangeRateService.findEntityByPair(pair);
        BigDecimal rate = exchangeRate.getRate();
        BigDecimal calculatedRate = BigDecimal.ONE.divide(
                rate,
                PRE_ROUNDING,
                RoundingMode.HALF_UP);
        BigDecimal convertedAmount = amount.multiply(calculatedRate);
        BigDecimal roundedAmount = round(convertedAmount);

        return Mapper.toCalculationResponseDto(
                baseResponse,
                targetResponse,
                calculatedRate,
                amount,
                roundedAmount
        );
    }
}
