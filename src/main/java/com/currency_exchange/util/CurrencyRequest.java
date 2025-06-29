package com.currency_exchange.util;

import com.currency_exchange.exception.service_exception.InvalidAttributeException;

public enum CurrencyRequest {
    NAME("name", "^[\\p{L}\\s\\-]{2,50}$", "Currency name must be 2-50 letters"),
    CODE("code", "^[A-Za-z]{3}$", "Currency code must be 3 latin letters"),
    SIGN("sign", "^\\p{Sc}", "Currency sign must be a valid symbol (e.g. $, €...");

    private final String paramName;
    private final String regex;
    private final String errorMessage;

    CurrencyRequest(String paramName, String regex, String errorMessage) {
        this.paramName = paramName;
        this.regex = regex;
        this.errorMessage = errorMessage;
    }

    public static CurrencyRequest fromParamName(String paramName) {
        for (CurrencyRequest param : values()) {
            if (param.paramName.equals(paramName)) {
                return param;
            }
        }
        throw new InvalidAttributeException("Unknown currency parameter: %s".formatted(paramName));
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
