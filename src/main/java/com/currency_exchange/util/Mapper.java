package com.currency_exchange.util;

import com.currency_exchange.dto.request.CurrencyDtoRequest;
import com.currency_exchange.dto.response.CurrencyDtoResponse;
import com.currency_exchange.entity.Currency;

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

    public static CurrencyDtoResponse mapToDto(Currency currency) {
        return new CurrencyDtoResponse(currency.getId(),
                currency.getCode(),
                currency.getFullName(),
                currency.getSign());
    }

    public static CurrencyDtoRequest mapToDtoRequest(String name, String code, String sigh) {
        return new CurrencyDtoRequest(code, name, sigh);
    }
}
