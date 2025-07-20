package com.currency_exchange.util;

import com.currency_exchange.dto.currency.CurrencyCodesDto;
import com.currency_exchange.dto.currency.CurrencyCreateDto;
import com.currency_exchange.dto.currency.CurrencyResponseDto;
import com.currency_exchange.dto.exchange_calculation.CalculationRequestDto;
import com.currency_exchange.dto.exchange_calculation.CalculationResponseDto;
import com.currency_exchange.dto.exchange_rate.ExchangeRateCreateDto;
import com.currency_exchange.dto.exchange_rate.ExchangeRateResponseDto;
import com.currency_exchange.dto.exchange_rate.ExchangeRateUpdateDto;
import com.currency_exchange.entity.Currency;
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

    public static ExchangeRate toExchangeRate(BigDecimal rate, Currency base, Currency target) {
        ExchangeRate exchangeRate = new ExchangeRate();
        exchangeRate.setBaseCurrencyId(base.getId());
        exchangeRate.setTargetCurrencyId(target.getId());
        exchangeRate.setRate(rate);

        return exchangeRate;
    }

    public static CurrencyResponseDto toCurrencyResponseDto(Currency currency) {
        return new CurrencyResponseDto(
                currency.getId(),
                currency.getCode(),
                currency.getFullName(),
                currency.getSign());
    }

    public static ExchangeRateResponseDto toExchangeRateResponseDto(ExchangeRate exchangeRate, Currency base, Currency target) {
        CurrencyResponseDto baseResponse = Mapper.toCurrencyResponseDto(base);
        CurrencyResponseDto targetResponse = Mapper.toCurrencyResponseDto(target);
        return new ExchangeRateResponseDto(
                exchangeRate.getId(),
                baseResponse,
                targetResponse,
                exchangeRate.getRate());
    }

    public static ExchangeRateCreateDto toExchangeRateCreateDto(
            String baseCurrencyCode,
            String targetCurrencyCode,
            String rate) {
        BigDecimal rateDecimal = createFromString(rate);
        return new ExchangeRateCreateDto(
                baseCurrencyCode,
                targetCurrencyCode,
                rateDecimal);
    }

    public static ExchangeRateUpdateDto toExchangeRateUpdateDto(CurrencyCodesDto codesDto, String rate) {
        return new ExchangeRateUpdateDto(codesDto, new BigDecimal(rate));
    }

    public static CalculationRequestDto toCalculationRequestDto(String from, String to, String amount) {
        BigDecimal rateDecimal = new BigDecimal(amount);
        return new CalculationRequestDto(from, to, rateDecimal);
    }

    public static CurrencyCodesDto toCurrencyCodesDto(String baseCurrencyCode, String targetCurrencyCode) {
        return new CurrencyCodesDto(baseCurrencyCode, targetCurrencyCode);
    }

    public static CalculationResponseDto toCalculationResponseDto(
            CurrencyResponseDto base,
            CurrencyResponseDto target,
            BigDecimal calculatedRate,
            BigDecimal amount,
            BigDecimal roundedAmount
    ) {
        return new CalculationResponseDto(
                base,
                target,
                calculatedRate,
                amount,
                roundedAmount
        );
    }

    private static BigDecimal createFromString(String input) {
        if (input.trim().contains(",")) {
            String valid = input.replaceFirst(",", ".");
            return new BigDecimal(valid);
        }
        return new BigDecimal(input.trim());
    }
}
