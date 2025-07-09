package com.currency_exchange.service;

import com.currency_exchange.dto.currency.CurrencyResponse;
import com.currency_exchange.dto.exchange_calculation.ExchangeCalculationRequest;
import com.currency_exchange.dto.exchange_calculation.ExchangeCalculationResponse;
import com.currency_exchange.entity.Currency;
import com.currency_exchange.entity.ExchangeRate;
import com.currency_exchange.exception.service_exception.ExchangeRateNotFoundException;
import com.currency_exchange.util.Mapper;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ExchangeCalculationService {
    private static final ExchangeCalculationService INSTANCE = new ExchangeCalculationService();
    private final ExchangeRateService exchangeRateService = ExchangeRateService.getInstance();
    private final CurrencyService currencyService = CurrencyService.getInstance();

    private ExchangeCalculationService() {
    }

    public static ExchangeCalculationService getInstance() {
        return INSTANCE;
    }

    public ExchangeCalculationResponse calculate(ExchangeCalculationRequest calculationRequest) {
        String codeBase = calculationRequest.from();
        String codeTarget = calculationRequest.to();
        BigDecimal amount = calculationRequest.amount();

        Currency base = currencyService.findCurrencyEntityByCode(codeBase);
        Currency target = currencyService.findCurrencyEntityByCode(codeTarget);

        boolean isExchangeRateExists = exchangeRateService.isExchangeRateExists(base, target);
        boolean isReversedExchangeRateExists = exchangeRateService.isReversedExchangeRateExists(base, target);
        boolean isCrossCourseExists = exchangeRateService.isCrossCourseExists(base, target);

        if (isExchangeRateExists) {
            return calculateDirectRate(base, target, amount);
        } else if (isReversedExchangeRateExists) {
            return calculateReverseRate(base, target, amount);
        } else if (isCrossCourseExists) {
            return calculateCrossRate(base, target, amount);
        }
        throw new ExchangeRateNotFoundException(base.getCode(), target.getCode());
    }

    private ExchangeCalculationResponse calculateDirectRate(Currency base, Currency target, BigDecimal amount) {
        ExchangeRate exchangeRate = exchangeRateService.findEntityByPair(base, target);
        CurrencyResponse baseResponse = Mapper.mapToCurrencyResponse(base);
        CurrencyResponse targetResponse = Mapper.mapToCurrencyResponse(target);

        BigDecimal convertedAmount = amount.multiply(exchangeRate.getRate());
        return Mapper.mapToExchangeCalculationResponse(exchangeRate.getRate(), baseResponse, targetResponse, amount, convertedAmount);
    }

    private ExchangeCalculationResponse calculateReverseRate(Currency base, Currency target, BigDecimal amount) {
        ExchangeRate exchangeRate = exchangeRateService.findEntityByPair(target, base);
        CurrencyResponse baseResponse = Mapper.mapToCurrencyResponse(base);
        CurrencyResponse targetResponse = Mapper.mapToCurrencyResponse(target);

        BigDecimal directRate = exchangeRate.getRate();
        BigDecimal reversedRate = BigDecimal.ONE.divide(directRate, 6, RoundingMode.HALF_UP);
        BigDecimal convertedAmount = amount.multiply(reversedRate);

        return Mapper.mapToExchangeCalculationResponse(reversedRate, baseResponse, targetResponse, amount, convertedAmount);
    }

    private ExchangeCalculationResponse calculateCrossRate(Currency base, Currency target, BigDecimal amount) {
        ExchangeRate usdToBase = exchangeRateService.findByUsd(base);
        ExchangeRate usdToTarget = exchangeRateService.findByUsd(target);

        BigDecimal usdToBaseRate = usdToBase.getRate();
        BigDecimal usdToTargetRate = usdToTarget.getRate();
        BigDecimal calculatedRate = usdToBaseRate.multiply(usdToTargetRate);
        BigDecimal convertedAmount = amount.multiply(calculatedRate);

        CurrencyResponse baseResponse = Mapper.mapToCurrencyResponse(base);
        CurrencyResponse targetResponse = Mapper.mapToCurrencyResponse(target);
        return new ExchangeCalculationResponse(baseResponse, targetResponse, calculatedRate, amount, convertedAmount);
    }
}
