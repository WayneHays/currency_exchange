package com.currency_exchange.util;

import com.currency_exchange.dto.currency.CurrencyCreateRequest;
import com.currency_exchange.dto.currency.CurrencyResponse;
import com.currency_exchange.dto.exchange_rate.ExchangeRateCreateRequest;
import com.currency_exchange.dto.exchange_rate.ExchangeRateResponse;
import com.currency_exchange.dto.exchange_rate.ExchangeRateUpdateRequest;
import com.currency_exchange.entity.Currency;
import com.currency_exchange.entity.ExchangeRate;

import java.math.BigDecimal;

public final class Mapper {

    private Mapper() {
    }

    public static Currency mapToCurrency(CurrencyCreateRequest dto) {
        Currency currency = new Currency();
        currency.setCode(dto.code());
        currency.setSign(dto.sign());
        currency.setFullName(dto.name());
        return currency;
    }

    public static ExchangeRate mapToExchangeRate(ExchangeRateCreateRequest dto, Currency baseCurrency, Currency targetCurrency) {
        ExchangeRate exchangeRate = new ExchangeRate();
        exchangeRate.setBaseCurrencyId(baseCurrency.getId());
        exchangeRate.setTargetCurrencyId(targetCurrency.getId());
        exchangeRate.setRate(dto.rate());

        return exchangeRate;
    }

//    public static CurrencyRequestDto mapToCurrencyDtoRequest(String[] currencyData) {
//        String name = currencyData[0];
//        String code = currencyData[1];
//        String sign = currencyData[2];
//        name = capitalizeFirstLetter(name);
//        code = capitalizeAllLetters(code);
//        return new CurrencyRequestDto(code, name, sign);
//    }

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

    public static CurrencyResponse mapToCurrencyResponse(Currency currency) {
        return new CurrencyResponse(
                currency.getId(),
                currency.getCode(),
                currency.getFullName(),
                currency.getSign());
    }

    public static ExchangeRateResponse mapToExchangeRateResponse(ExchangeRate exchangeRate, Currency base, Currency target) {
        CurrencyResponse baseCurrency = Mapper.mapToCurrencyResponse(base);
        CurrencyResponse targetCurrency = Mapper.mapToCurrencyResponse(target);
        return new ExchangeRateResponse(exchangeRate.getId(), baseCurrency, targetCurrency, exchangeRate.getRate());
    }

    public static ExchangeRateCreateRequest mapToExchangeRateCreateRequest(String baseCurrencyCode, String targetCurrencyCode, String rate) {
        BigDecimal rateDecimal = new BigDecimal(rate.trim());
        return new ExchangeRateCreateRequest(baseCurrencyCode, targetCurrencyCode, rateDecimal);
    }

    public static ExchangeRateUpdateRequest mapToExchangeRateUpdateRequest(String rate) {
        return new ExchangeRateUpdateRequest(new BigDecimal(rate));
    }
}
