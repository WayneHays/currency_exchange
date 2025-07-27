package com.currency_exchange.service.calculation_strategy;

import com.currency_exchange.dao.ExchangeRatesDao;
import com.currency_exchange.dto.calculation.CalculationResponseDto;
import com.currency_exchange.dto.currency.CurrencyResponseDto;
import com.currency_exchange.entity.Currency;
import com.currency_exchange.entity.ExchangeRate;
import com.currency_exchange.exception.ExchangeRateNotFoundException;
import com.currency_exchange.util.Mapper;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CrossRateStrategy extends CalculationStrategy {
    private final Long crossCurrencyId;

    public CrossRateStrategy(ExchangeRatesDao exchangeRatesDao, Long crossCurrencyId) {
        super(exchangeRatesDao);
        this.crossCurrencyId = crossCurrencyId;
    }

    @Override
    public CalculationResponseDto calculate(
            Currency base,
            Currency target,
            BigDecimal amount,
            CurrencyResponseDto baseResponse,
            CurrencyResponseDto targetResponse) {

        BigDecimal usdToBaseRate = findCrossRate(crossCurrencyId, base.getId());
        BigDecimal usdToTargetRate = findCrossRate(crossCurrencyId, target.getId());
        BigDecimal rate = usdToTargetRate.divide(usdToBaseRate, PRE_ROUNDING, RoundingMode.HALF_UP);

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

    private BigDecimal findCrossRate(Long crossCurrencyId, Long currencyId) {
        try {
            ExchangeRate rate = exchangeRatesDao.findByCurrencyIds(crossCurrencyId, currencyId);
            return rate.getRate();
        } catch (ExchangeRateNotFoundException e) {
            try {
                ExchangeRate reverseRate = exchangeRatesDao.findByCurrencyIds(currencyId, crossCurrencyId);
                return BigDecimal.ONE.divide(reverseRate.getRate(), PRE_ROUNDING, RoundingMode.HALF_UP);
            } catch (ExchangeRateNotFoundException reverseException) {
                throw new ExchangeRateNotFoundException(crossCurrencyId, currencyId);
            }
        }
    }
}