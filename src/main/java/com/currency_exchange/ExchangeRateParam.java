package com.currency_exchange;

import com.currency_exchange.util.RequiredParam;
import com.currency_exchange.util.ValidationConstants;

import java.util.Set;

public enum ExchangeRateParam implements RequiredParam {

    BASE(
            "baseCurrencyCode",
            ValidationConstants.CURRENCY_CODE_PATTERN,
            ValidationConstants.CURRENCY_CODE_ERROR_MESSAGE),

    TARGET(
            "targetCurrencyCode",
            ValidationConstants.CURRENCY_CODE_PATTERN,
            ValidationConstants.CURRENCY_CODE_ERROR_MESSAGE),

    RATE(
            "rate",
            ValidationConstants.EXCHANGE_RATE_RATE_PATTERN
            , ValidationConstants.EXCHANGE_RATE_RATE_ERROR_MESSAGE);

    private final String paramName;
    private final String regex;
    private final String errorMessage;

    ExchangeRateParam(String paramName, String regex, String errorMessage) {
        this.paramName = paramName;
        this.regex = regex;
        this.errorMessage = errorMessage;
    }

    public static Set<String> getAllNames() {
        return Set.of(BASE.paramName, TARGET.paramName, RATE.paramName);
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

