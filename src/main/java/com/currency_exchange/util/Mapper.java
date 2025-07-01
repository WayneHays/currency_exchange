package com.currency_exchange.util;

import com.currency_exchange.dto.request.CurrencyDtoRequest;
import com.currency_exchange.dto.request.ExchangeRateDtoRequest;
import com.currency_exchange.dto.response.CurrencyDtoResponse;
import com.currency_exchange.dto.response.ExchangeRateDtoResponse;
import com.currency_exchange.entity.Currency;
import com.currency_exchange.entity.ExchangeRate;

import java.math.BigDecimal;

public final class Mapper {

    private Mapper() {
    }

    public static Currency mapToCurrency(CurrencyDtoRequest dtoRequest) {
        Currency currency = new Currency();
        currency.setCode(dtoRequest.getCode());
        currency.setSign(dtoRequest.getSign());
        currency.setFullName(dtoRequest.getFullName());
        return currency;
    }

    public static ExchangeRate mapToExchangeRate(ExchangeRateDtoRequest dtoRequest, Long baseId, Long targetId) {
        ExchangeRate exchangeRate = new ExchangeRate();
        exchangeRate.setBaseCurrencyId(baseId);
        exchangeRate.setTargetCurrencyId(targetId);
        exchangeRate.setRate(dtoRequest.getRate());

        return exchangeRate;
    }

    public static CurrencyDtoRequest mapToCurrencyDtoRequest(String name, String code, String sign) {
        name = capitalizeFirstLetter(name);
        code = capitalizeAllLetters(code);
        return new CurrencyDtoRequest(code, name, sign);
    }

    public static ExchangeRateDtoRequest mapToExchangeRateDtoRequest(String baseCurrencyCode, String targetCurrencyCode, String rate) throws NumberFormatException {
        BigDecimal rateDecimal = new BigDecimal(rate.trim());
        return new ExchangeRateDtoRequest(baseCurrencyCode, targetCurrencyCode, rateDecimal);
    }

    public static CurrencyDtoResponse mapToCurrencyDtoResponse(Currency currency) {
        return new CurrencyDtoResponse(currency.getId(),
                currency.getFullName(), currency.getCode(),
                currency.getSign());
    }

    public static ExchangeRateDtoResponse mapToExchangeRateDtoResponse(ExchangeRate exchangeRate, CurrencyDtoResponse[] dtoResponses) {
        return new ExchangeRateDtoResponse(
                exchangeRate.getId(),
                dtoResponses[0],
                dtoResponses[1],
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
