package com.currency_exchange;

public enum ExchangeCalculationParam implements RequestParameter {

    FROM(
            "from",
            CurrencyParam.CODE.getRegex(),
            CurrencyParam.CODE.getErrorMessage()),

    TO(
            "to",
            CurrencyParam.CODE.getRegex(),
            CurrencyParam.CODE.getErrorMessage()),

    AMOUNT(
            "amount",
            ExchangeRateParam.RATE.getRegex(),
            ExchangeRateParam.RATE.getErrorMessage());

    private final String paramName;
    private final String regex;
    private final String errorMessage;

    ExchangeCalculationParam(String paramName, String regex, String errorMessage) {
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
