package com.currency_exchange.util;

import com.currency_exchange.dto.request.CurrencyRequest;
import com.currency_exchange.dto.request.ExchangeRateRequest;
import com.currency_exchange.dto.response.CurrencyResponse;
import com.currency_exchange.dto.response.ExchangeRateResponse;
import com.currency_exchange.entity.Currency;
import com.currency_exchange.entity.ExchangeRate;

import java.math.BigDecimal;

public final class Mapper {

    private Mapper() {
    }

    public static Currency mapToCurrency(CurrencyRequest dtoRequest) {
        Currency currency = new Currency();
        currency.setCode(dtoRequest.getCode());
        currency.setSign(dtoRequest.getSign());
        currency.setFullName(dtoRequest.getFullName());
        return currency;
    }

    public static ExchangeRate mapToExchangeRate(ExchangeRateRequest dtoRequest, Long baseId, Long targetId) {
        ExchangeRate exchangeRate = new ExchangeRate();
        exchangeRate.setBaseCurrencyId(baseId);
        exchangeRate.setTargetCurrencyId(targetId);
        exchangeRate.setRate(dtoRequest.getRate());

        return exchangeRate;
    }

    public static CurrencyRequest mapToCurrencyDtoRequest(String[] currencyData) {
        String name = currencyData[0];
        String code = currencyData[1];
        String sign = currencyData[2];
        name = capitalizeFirstLetter(name);
        code = capitalizeAllLetters(code);
        return new CurrencyRequest(code, name, sign);
    }

    public static ExchangeRateRequest mapToExchangeRateDtoRequest(String[] exchangeRateData) throws NumberFormatException {
        String baseCurrencyCode = exchangeRateData[0];
        String targetCurrencyCode = exchangeRateData[1];
        String rate = exchangeRateData[2];
        BigDecimal rateDecimal = new BigDecimal(rate.trim());
        return new ExchangeRateRequest(baseCurrencyCode, targetCurrencyCode, rateDecimal);
    }

    public static CurrencyResponse mapToCurrencyDtoResponse(Currency currency) {
        return new CurrencyResponse(
                currency.getId(),
                currency.getFullName(),
                currency.getCode(),
                currency.getSign());
    }

    public static ExchangeRateResponse mapToExchangeRateDtoResponse(ExchangeRate exchangeRate, CurrencyResponse base, CurrencyResponse target) {
        return new ExchangeRateResponse(
                exchangeRate.getId(),
                base,
                target,
                exchangeRate.getRate()
        );
    }

    private static String capitalizeFirstLetter(String input) {
        char[] chars = input.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return new String(chars);
    }

    private static String capitalizeAllLetters(String input) {
        char[] chars = input.toCharArray();
        StringBuilder upperCaseWord = new StringBuilder();

        for (char aChar : chars) {
            upperCaseWord.append(Character.toUpperCase(aChar));
        }
        return upperCaseWord.toString();
    }
}
