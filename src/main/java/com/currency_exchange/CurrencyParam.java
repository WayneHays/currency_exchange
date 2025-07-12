package com.currency_exchange;

public enum CurrencyParam implements RequestParameter {

    NAME(
            "name",
            "^[\\p{L}\\s\\-]{2,30}$",
            "Currency name must be 2-30 latin letters"),

    CODE(
            "code",
            "^[A-Za-z]{3}$",
            "Currency code must be 3 latin letters"),

    SIGN(
            "sign",
            "^\\p{Sc}",
            "Currency sign must be a valid symbol (e.g. $, €...");

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
