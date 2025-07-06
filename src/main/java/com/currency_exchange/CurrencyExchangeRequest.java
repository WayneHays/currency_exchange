package com.currency_exchange;

import com.currency_exchange.exception.service_exception.InvalidAttributeException;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum CurrencyExchangeRequest {
    FROM("from", CurrencyRequest.CODE.getRegex(), CurrencyRequest.CODE.getErrorMessage()),
    TO("to", CurrencyRequest.CODE.getRegex(), CurrencyRequest.CODE.getErrorMessage()),
    AMOUNT("amount", ExchangeRateRequest.RATE.getRegex(), ExchangeRateRequest.RATE.getErrorMessage());


    private final String paramName;
    private final String regex;
    private final String errorMessage;

    CurrencyExchangeRequest(String paramName, String regex, String errorMessage) {
        this.paramName = paramName;
        this.regex = regex;
        this.errorMessage = errorMessage;
    }

    public static CurrencyExchangeRequest fromParamName(String paramName) {
        for (CurrencyExchangeRequest param : values()) {
            if (param.paramName.equals(paramName)) {
                return param;
            }
        }
        throw new InvalidAttributeException("Unknown currency exchange parameter: %s".formatted(paramName));
    }

    public static Set<String> getRequiredParamNames() {
        return Arrays.stream(values())
                .map(CurrencyExchangeRequest::getParamName)
                .collect(Collectors.toSet());
    }

    public String getParamName() {
        return paramName;
    }

    public String getRegex() {
        return regex;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
