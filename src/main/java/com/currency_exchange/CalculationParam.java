package com.currency_exchange;

import com.currency_exchange.util.ValidationConstants;

public enum CalculationParam {

    FROM(
            "from",
            ValidationConstants.CURRENCY_CODE_REGEX,
            ValidationConstants.CURRENCY_CODE_ERROR_MESSAGE),

    TO(
            "to",
            ValidationConstants.CURRENCY_CODE_REGEX,
            ValidationConstants.CURRENCY_CODE_ERROR_MESSAGE),

    AMOUNT(
            "amount",
            ValidationConstants.EXCHANGE_RATE_RATE_REGEX,
            ValidationConstants.CALCULATION_AMOUNT_ERROR_MESSAGE);

    private final String paramName;
    private final String regex;
    private final String errorMessage;

    CalculationParam(String paramName, String regex, String errorMessage) {
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
