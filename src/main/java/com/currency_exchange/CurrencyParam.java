package com.currency_exchange;

import com.currency_exchange.util.RequiredParam;
import com.currency_exchange.util.ValidationConstants;

import java.util.Set;

public enum CurrencyParam implements RequiredParam {

    NAME(
            "name",
            ValidationConstants.CURRENCY_NAME_PATTERN,
            ValidationConstants.CURRENCY_NAME_ERROR_MESSAGE),

    CODE(
            "code",
            ValidationConstants.CURRENCY_CODE_PATTERN,
            ValidationConstants.CURRENCY_CODE_ERROR_MESSAGE),

    SIGN(
            "sign",
            ValidationConstants.CURRENCY_SIGN_PATTERN,
            ValidationConstants.CURRENCY_SIGN_ERROR_MESSAGE);

    private final String paramName;
    private final String regex;
    private final String errorMessage;

    CurrencyParam(String paramName, String regex, String errorMessage) {
        this.paramName = paramName;
        this.regex = regex;
        this.errorMessage = errorMessage;
    }

    public static Set<String> getAllNames() {
        return Set.of(NAME.paramName, CODE.paramName, SIGN.paramName);
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
