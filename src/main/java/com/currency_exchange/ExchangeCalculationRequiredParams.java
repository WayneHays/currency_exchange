package com.currency_exchange;

import com.currency_exchange.exception.service_exception.InvalidParameterException;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum ExchangeCalculationRequiredParams {
    FROM("from", CurrencyRequiredParams.CODE.getRegex(), CurrencyRequiredParams.CODE.getErrorMessage()),
    TO("to", CurrencyRequiredParams.CODE.getRegex(), CurrencyRequiredParams.CODE.getErrorMessage()),
    AMOUNT("amount", ExchangeRateRequiredParams.RATE.getRegex(), ExchangeRateRequiredParams.RATE.getErrorMessage());

    private final String paramName;
    private final String regex;
    private final String errorMessage;

    ExchangeCalculationRequiredParams(String paramName, String regex, String errorMessage) {
        this.paramName = paramName;
        this.regex = regex;
        this.errorMessage = errorMessage;
    }

    public static ExchangeCalculationRequiredParams fromParamName(String paramName) {
        for (ExchangeCalculationRequiredParams param : values()) {
            if (param.paramName.equals(paramName)) {
                return param;
            }
        }
        throw new InvalidParameterException("Unknown currency exchange parameter: %s".formatted(paramName));
    }

    public static Set<String> getRequiredParamNames() {
        return Arrays.stream(values())
                .map(ExchangeCalculationRequiredParams::getParamName)
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
