package com.currency_exchange.util;

import com.currency_exchange.dto.currency.CurrencyCreateRequest;
import com.currency_exchange.dto.currency.CurrencyResponse;
import com.currency_exchange.dto.exchange_calculation.ExchangeCalculationRequest;
import com.currency_exchange.dto.exchange_rate.ExchangeRateCreateRequest;
import com.currency_exchange.dto.exchange_rate.ExchangeRateResponse;
import com.currency_exchange.dto.exchange_rate.ExchangeRateUpdateRequest;
import com.currency_exchange.entity.Currency;
import com.currency_exchange.entity.CurrencyPair;
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

    public static ExchangeRate toExchangeRate(ExchangeRateCreateRequest dto, CurrencyPair pair) {
        ExchangeRate exchangeRate = new ExchangeRate();
        exchangeRate.setBaseCurrencyId(pair.base().getId());
        exchangeRate.setTargetCurrencyId(pair.target().getId());
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

    public static ExchangeRateResponse toExchangeRateResponse(ExchangeRate exchangeRate, CurrencyPair pair) {
        CurrencyResponse baseResponse = Mapper.toCurrencyResponse(pair.base());
        CurrencyResponse targetResponse = Mapper.toCurrencyResponse(pair.target());
        return new ExchangeRateResponse(
                exchangeRate.getId(),
                baseResponse,
                targetResponse,
                exchangeRate.getRate());
    }

    public static ExchangeRateCreateRequest toExchangeRateCreateRequest(
            String baseCurrencyCode,
            String targetCurrencyCode,
            String rate) {
        BigDecimal rateDecimal = createFromString(rate);
        return new ExchangeRateCreateRequest(
                baseCurrencyCode,
                targetCurrencyCode,
                rateDecimal);
    }

    public static ExchangeRateUpdateRequest toExchangeRateUpdateRequest(String rate) {
        return new ExchangeRateUpdateRequest(new BigDecimal(rate));
    }

    public static ExchangeCalculationRequest toExchangeCalculationRequest(String from, String to, String amount) {
        BigDecimal rateDecimal = new BigDecimal(amount);
        return new ExchangeCalculationRequest(from, to, rateDecimal);
    }

    private static BigDecimal createFromString(String input) {
        if (input.trim().contains(",")) {
            String valid = input.replaceFirst(",", ".");
            return new BigDecimal(valid);
        }
        return new BigDecimal(input.trim());
    }
}
