package com.currency_exchange;

import com.currency_exchange.util.ValidationConstants;

public enum CurrencyParam {

    NAME(
            "name",
            ValidationConstants.CURRENCY_NAME_REGEX,
            ValidationConstants.CURRENCY_NAME_ERROR_MESSAGE),

    CODE(
            "code",
            ValidationConstants.CURRENCY_CODE_REGEX,
            ValidationConstants.CURRENCY_CODE_ERROR_MESSAGE),

    SIGN(
            "sign",
            ValidationConstants.CURRENCY_SIGN_REGEX,
            ValidationConstants.CURRENCY_SIGN_ERROR_MESSAGE);

    private final String paramName;
    private final String regex;
    private final String errorMessage;

    CurrencyParam(String paramName, String regex, String errorMessage) {
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
