package com.currency_exchange;

import com.currency_exchange.util.RequiredParam;
import com.currency_exchange.util.ValidationConstants;

public enum CurrencyPairParam implements RequiredParam {

    PAIR(
            "pair",
            ValidationConstants.CURRENCY_PAIR_PATTERN,
            ValidationConstants.CURRENCY_PAIR_ERROR_MESSAGE);


    private final String paramName;
    private final String regex;
    private final String errorMessage;

    CurrencyPairParam(String paramName, String regex, String errorMessage) {
        this.paramName = paramName;
        this.regex = regex;
        this.errorMessage = errorMessage;
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
