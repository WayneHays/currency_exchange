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

    public static CurrencyDtoRequest mapToCurrencyDtoRequest(String name, String code, String sigh) {
        return new CurrencyDtoRequest(code, name, sigh);
    }

    public static ExchangeRateDtoRequest mapToExchangeRateDtoRequest(String baseCurrencyCode, String targetCurrencyCode, String rate) throws NumberFormatException {
        double rateDouble = Double.parseDouble(rate);
        BigDecimal rateDecimal = BigDecimal.valueOf(rateDouble);

        return new ExchangeRateDtoRequest(baseCurrencyCode, targetCurrencyCode, rateDecimal);
    }

    public static CurrencyDtoResponse mapToCurrencyDtoResponse(Currency currency) {
        return new CurrencyDtoResponse(currency.getId(),
                currency.getFullName(), currency.getCode(),
                currency.getSign());
    }

    public static ExchangeRateDtoResponse mapToExchangeRateDtoResponse(ExchangeRate exchangeRate, CurrencyDtoResponse base, CurrencyDtoResponse target) {
        return new ExchangeRateDtoResponse(
                exchangeRate.getId(),
                base,
                target,
                exchangeRate.getRate()
        );
    }
}
