package com.currency_exchange;

import com.currency_exchange.util.RequiredParam;
import com.currency_exchange.util.ValidationConstants;

import java.util.Set;

public enum CalculationParam implements RequiredParam {

    FROM(
            "from",
            ValidationConstants.CURRENCY_CODE_PATTERN,
            ValidationConstants.CURRENCY_CODE_ERROR_MESSAGE),

    TO(
            "to",
            ValidationConstants.CURRENCY_CODE_PATTERN,
            ValidationConstants.CURRENCY_CODE_ERROR_MESSAGE),

    AMOUNT(
            "amount",
            ValidationConstants.EXCHANGE_RATE_RATE_PATTERN,
            ValidationConstants.CALCULATION_AMOUNT_ERROR_MESSAGE);

    private final String paramName;
    private final String regex;
    private final String errorMessage;

    CalculationParam(String paramName, String regex, String errorMessage) {
        this.paramName = paramName;
        this.regex = regex;
        this.errorMessage = errorMessage;
    }

    public static Set<String> getAllNames() {
        return Set.of(FROM.paramName, TO.paramName, AMOUNT.paramName);
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
