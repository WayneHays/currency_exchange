package com.currency_exchange;

public enum ExchangeRateParam implements RequestParameter {

    BASE(
            "baseCurrencyCode",
            CurrencyParam.CODE.getRegex(),
            CurrencyParam.CODE.getErrorMessage()),

    TARGET(
            "targetCurrencyCode",
            CurrencyParam.CODE.getRegex(),
            CurrencyParam.CODE.getErrorMessage()),

    RATE(
            "rate",
            "^(?!0+([.,]0+)?$)([1-9]\\d{0,5}([.,]\\d{1,6})?|0[.,]\\d{1,6}|[.,]\\d{1,6})$"
            , "Rate must be a positive number with up to 6 digits before and after the point");

    private final String paramName;
    private final String regex;
    private final String errorMessage;

    ExchangeRateParam(String paramName, String regex, String errorMessage) {
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

