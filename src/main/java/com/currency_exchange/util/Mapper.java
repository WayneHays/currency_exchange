package com.currency_exchange.util;

import com.currency_exchange.dto.currency.CurrencyCreateRequest;
import com.currency_exchange.dto.currency.CurrencyResponse;
import com.currency_exchange.dto.exchange_calculation.ExchangeCalculationRequest;
import com.currency_exchange.dto.exchange_calculation.ExchangeCalculationResponse;
import com.currency_exchange.dto.exchange_rate.ExchangeRateCreateRequest;
import com.currency_exchange.dto.exchange_rate.ExchangeRateResponse;
import com.currency_exchange.dto.exchange_rate.ExchangeRateUpdateRequest;
import com.currency_exchange.entity.Currency;
import com.currency_exchange.entity.ExchangeRate;

import java.math.BigDecimal;

public final class Mapper {

    private Mapper() {
    }

    public static Currency toCurrency(CurrencyCreateRequest dto) {
        Currency currency = new Currency();
        currency.setCode(dto.code());
        currency.setSign(dto.sign());
        currency.setFullName(dto.name());
        return currency;
    }

    public static ExchangeRate toExchangeRate(ExchangeRateCreateRequest dto, Currency baseCurrency, Currency targetCurrency) {
        ExchangeRate exchangeRate = new ExchangeRate();
        exchangeRate.setBaseCurrencyId(baseCurrency.getId());
        exchangeRate.setTargetCurrencyId(targetCurrency.getId());
        exchangeRate.setRate(dto.rate());

        return exchangeRate;
    }

    public static CurrencyResponse toCurrencyResponse(Currency currency) {
        return new CurrencyResponse(
                currency.getId(),
                currency.getCode(),
                currency.getFullName(),
                currency.getSign());
    }

    public static ExchangeRateResponse toExchangeRateResponse(ExchangeRate exchangeRate, Currency base, Currency target) {
        CurrencyResponse baseCurrency = Mapper.toCurrencyResponse(base);
        CurrencyResponse targetCurrency = Mapper.toCurrencyResponse(target);
        return new ExchangeRateResponse(exchangeRate.getId(), baseCurrency, targetCurrency, exchangeRate.getRate());
    }

    public static ExchangeRateCreateRequest toExchangeRateCreateRequest(
            String baseCurrencyCode,
            String targetCurrencyCode,
            String rate) {
        BigDecimal rateDecimal = new BigDecimal(rate.trim());
        return new ExchangeRateCreateRequest(baseCurrencyCode, targetCurrencyCode, rateDecimal);
    }

    public static ExchangeRateUpdateRequest toExchangeRateUpdateRequest(String rate) {
        return new ExchangeRateUpdateRequest(new BigDecimal(rate));
    }

    public static ExchangeCalculationRequest toExchangeCalculationRequest(String from, String to, String amount) {
        BigDecimal rateDecimal = new BigDecimal(amount);
        return new ExchangeCalculationRequest(from, to, rateDecimal);
    }

    public static ExchangeCalculationResponse mapToExchangeCalculationResponse(
            BigDecimal rate,
            CurrencyResponse baseResponse,
            CurrencyResponse targetResponse,
            BigDecimal amount,
            BigDecimal convertedAmount) {
        return new ExchangeCalculationResponse(
                baseResponse,
                targetResponse,
                rate,
                amount,
                convertedAmount
        );
    }
}
