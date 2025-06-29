package com.currency_exchange.util;

import com.currency_exchange.exception.service_exception.InvalidAttributeException;

public enum ExchangeRateRequest {
    BASE_CURRENCY_CODE("baseCurrencyCode", "^[A-Z]{3}", "Currency code must be 3 uppercase letters"),
    TARGET_CURRENCY_CODE("targetCurrencyCode", "^[A-Z]{3}", "Currency code must be 3 uppercase letters"),
    RATE("rate", "^\\d+\\.\\d{2}$", "Rate must be a decimal number with exactly 2 digits after the point (e.g. 1.00 or 0.44)");

    private final String name;
    private final String regex;
    private final String errorMessage;

    ExchangeRateRequest(String paramName, String regex, String errorMessage) {
        this.name = paramName;
        this.regex = regex;
        this.errorMessage = errorMessage;
    }

    public static ExchangeRateRequest fromParamName(String paramName) {
        for (ExchangeRateRequest param : values()) {
            if (param.name.equals(paramName)) {
                return param;
            }
        }
        throw new InvalidAttributeException("Unknown currency parameter: %s".formatted(paramName));
    }

    public String getName() {
        return name;
    }

    public String getRegex() {
        return regex;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}

