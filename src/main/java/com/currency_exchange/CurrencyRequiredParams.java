package com.currency_exchange;

import com.currency_exchange.exception.service_exception.InvalidParameterException;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum CurrencyRequiredParams {
    NAME("name", "^[\\p{L}\\s\\-]{2,30}$", "Currency name must be 2-30 latin letters"),
    CODE("code", "^[A-Za-z]{3}$", "Currency code must be 3 latin letters"),
    SIGN("sign", "^\\p{Sc}", "Currency sign must be a valid symbol (e.g. $, €...");

    private final String paramName;
    private final String regex;
    private final String errorMessage;

    CurrencyRequiredParams(String paramName, String regex, String errorMessage) {
        this.paramName = paramName;
        this.regex = regex;
        this.errorMessage = errorMessage;
    }

    public static CurrencyRequiredParams fromParamName(String paramName) {
        for (CurrencyRequiredParams param : values()) {
            if (param.paramName.equals(paramName)) {
                return param;
            }
        }
        throw new InvalidParameterException("Unknown currency parameter: %s".formatted(paramName));
    }

    public static Set<String> getRequiredParamNames() {
        return Arrays.stream(values())
                .map(CurrencyRequiredParams::getParamName)
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
