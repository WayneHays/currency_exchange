package com.currency_exchange.util;

import com.currency_exchange.dto.calculation.CalculationRequestDto;
import com.currency_exchange.dto.calculation.CalculationResponseDto;
import com.currency_exchange.dto.currency.CurrencyPairDto;
import com.currency_exchange.dto.currency.CurrencyRequestDto;
import com.currency_exchange.dto.currency.CurrencyResponseDto;
import com.currency_exchange.dto.exchange_rate.ExchangeRateRequestDto;
import com.currency_exchange.dto.exchange_rate.ExchangeRateResponseDto;
import com.currency_exchange.entity.Currency;
import com.currency_exchange.entity.ExchangeRate;

import java.math.BigDecimal;

public final class Mapper {

    private Mapper() {
    }

    public static Currency toCurrency(CurrencyRequestDto dto) {
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

    public static ExchangeRateResponseDto toExchangeRateResponseDto(
            ExchangeRate exchangeRate,
            Currency base,
            Currency target) {
        CurrencyResponseDto baseResponse = Mapper.toCurrencyResponseDto(base);
        CurrencyResponseDto targetResponse = Mapper.toCurrencyResponseDto(target);
        return new ExchangeRateResponseDto(
                exchangeRate.getId(),
                baseResponse,
                targetResponse,
                exchangeRate.getRate());
    }

    public static ExchangeRateRequestDto toExchangeRateRequestDto(
            String baseCurrencyCode,
            String targetCurrencyCode,
            String rate) {
        BigDecimal rateDecimal = createFromString(rate);
        return new ExchangeRateRequestDto(
                baseCurrencyCode,
                targetCurrencyCode,
                rateDecimal);
    }

    public static CalculationRequestDto toCalculationRequestDto(String from, String to, String amount) {
        BigDecimal amountDecimal = createFromString(amount);
        return new CalculationRequestDto(from, to, amountDecimal);
    }

    public static CurrencyPairDto toCurrencyCodesDto(String baseCurrencyCode, String targetCurrencyCode) {
        return new CurrencyPairDto(baseCurrencyCode, targetCurrencyCode);
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

    public static CurrencyRequestDto toCurrencyRequestDto(String name, String code, String sign) {
        return new CurrencyRequestDto(name, code, sign);
    }

    private static BigDecimal createFromString(String input) {
        if (input.trim().contains(",")) {
            String valid = input.replaceFirst(",", ".");
            return new BigDecimal(valid);
        }
        return new BigDecimal(input.trim());
    }
}
