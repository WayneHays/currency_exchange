package com.currency_exchange.service.calculation_strategy;

import com.currency_exchange.dao.ExchangeRatesDao;
import com.currency_exchange.dto.calculation.CalculationResponseDto;
import com.currency_exchange.dto.currency.CurrencyResponseDto;
import com.currency_exchange.entity.Currency;
import com.currency_exchange.entity.ExchangeRate;
import com.currency_exchange.service.RateType;
import com.currency_exchange.util.Mapper;

import java.math.BigDecimal;

public class DirectRateStrategy extends CalculationStrategy {

    public DirectRateStrategy(ExchangeRatesDao exchangeRatesDao) {
        super(exchangeRatesDao);
    }

    @Override
    public boolean canHandle(Currency base, Currency target) {
        return exchangeRatesDao.isRateExists(base, target, RateType.DIRECT);
    }

    @Override
    public CalculationResponseDto calculate(
            Currency base,
            Currency target,
            BigDecimal amount,
            CurrencyResponseDto baseResponse,
            CurrencyResponseDto targetResponse) {
        ExchangeRate exchangeRate = exchangeRatesDao.findByCurrencyIds(base.getId(), target.getId());
        BigDecimal rate = exchangeRate.getRate();
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