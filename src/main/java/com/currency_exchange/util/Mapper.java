package com.currency_exchange.util;

import com.currency_exchange.dto.currency.CurrencyCodesDto;
import com.currency_exchange.dto.currency.CurrencyCreateDto;
import com.currency_exchange.dto.currency.CurrencyResponseDto;
import com.currency_exchange.dto.exchange_calculation.CalculationRequestDto;
import com.currency_exchange.dto.exchange_rate.ExchangeRateCreateRequest;
import com.currency_exchange.dto.exchange_rate.ExchangeRateResponse;
import com.currency_exchange.dto.exchange_rate.ExchangeRateUpdateDto;
import com.currency_exchange.entity.Currency;
import com.currency_exchange.entity.CurrencyPair;
import com.currency_exchange.entity.ExchangeRate;

import java.math.BigDecimal;

public final class Mapper {

    private Mapper() {
    }

    public static Currency toCurrency(CurrencyCreateDto dto) {
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

    public static CurrencyResponseDto toCurrencyResponse(Currency currency) {
        return new CurrencyResponseDto(
                currency.getId(),
                currency.getCode(),
                currency.getFullName(),
                currency.getSign());
    }

    public static ExchangeRateResponse toExchangeRateResponse(ExchangeRate exchangeRate, CurrencyPair pair) {
        CurrencyResponseDto baseResponse = Mapper.toCurrencyResponse(pair.base());
        CurrencyResponseDto targetResponse = Mapper.toCurrencyResponse(pair.target());
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

    public static ExchangeRateUpdateDto toExchangeRateUpdateDto(String rate) {
        return new ExchangeRateUpdateDto(new BigDecimal(rate));
    }

    public static CalculationRequestDto toCalculationRequestDto(String from, String to, String amount) {
        BigDecimal rateDecimal = new BigDecimal(amount);
        return new CalculationRequestDto(from, to, rateDecimal);
    }

    private static BigDecimal createFromString(String input) {
        if (input.trim().contains(",")) {
            String valid = input.replaceFirst(",", ".");
            return new BigDecimal(valid);
        }
        return new BigDecimal(input.trim());
    }

    public static CurrencyCodesDto toCurrencyCodesRequest(String baseCurrencyCode, String targetCurrencyCode) {
        return new CurrencyCodesDto(baseCurrencyCode, targetCurrencyCode);
    }
}
