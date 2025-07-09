package com.currency_exchange;

import com.currency_exchange.exception.service_exception.InvalidParameterException;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum ExchangeRateRequiredParams {
    BASE_CURRENCY_CODE("baseCurrencyCode", CurrencyRequiredParams.CODE.getRegex(), CurrencyRequiredParams.CODE.getErrorMessage()),
    TARGET_CURRENCY_CODE("targetCurrencyCode", CurrencyRequiredParams.CODE.getRegex(), CurrencyRequiredParams.CODE.getErrorMessage()),
    RATE("rate", "^(?!0\\d)\\d+\\.\\d{1,6}$", "Rate must be a positive decimal number with 1 to 6 digits after the point (e.g., 0.1, 1.23456, or 123.456)");

    private final String paramName;
    private final String regex;
    private final String errorMessage;

    ExchangeRateRequiredParams(String paramName, String regex, String errorMessage) {
        this.paramName = paramName;
        this.regex = regex;
        this.errorMessage = errorMessage;
    }

    public static ExchangeRateRequiredParams fromParamName(String paramName) {
        for (ExchangeRateRequiredParams param : values()) {
            if (param.paramName.equals(paramName)) {
                return param;
            }
        }
        throw new InvalidParameterException("Unknown exchange rate parameter: %s".formatted(paramName));
    }

    public static Set<String> getRequiredParamNames() {
        return Arrays.stream(values())
                .map(ExchangeRateRequiredParams::getParamName)
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

