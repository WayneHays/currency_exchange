package com.currency_exchange.util;

import com.currency_exchange.CalculationParam;
import com.currency_exchange.CurrencyParam;
import com.currency_exchange.ExchangeRateParam;
import com.currency_exchange.exception.InvalidParameterException;

public final class Validator {

    private Validator() {
    }

    public static void validateExchangeRateCreateRequest(
            String baseCurrencyCode,
            String targetCurrencyCode,
            String rate) {
        validateCurrenciesAreDifferent(baseCurrencyCode, targetCurrencyCode);
        validateParamRegex(baseCurrencyCode, CurrencyParam.CODE);
        validateParamRegex(targetCurrencyCode, CurrencyParam.CODE);
        validateParamRegex(rate, ExchangeRateParam.RATE);
    }

    public static void validateCurrencyCreateRequest(String code, String name, String sign) {
        validateParamRegex(code, CurrencyParam.CODE);
        validateParamRegex(name, CurrencyParam.NAME);
        validateParamRegex(sign, CurrencyParam.SIGN);
    }

    public static void validateParamRegex(String param, RequiredParam paramType) {
        if (!param.matches(paramType.getRegex())) {
            throw new InvalidParameterException(paramType.getErrorMessage());
        }
    }

    public static void validateExchangeCalculationRequest(String from, String to, String amount) {
        validateCurrenciesAreDifferent(from, to);
        validateParamRegex(from, CurrencyParam.CODE);
        validateParamRegex(to, CurrencyParam.CODE);
        validateParamRegex(amount, CalculationParam.AMOUNT);
    }

    public static void validateNotEmpty(String path, String errorMessage) {
        if (path == null || "/".equals(path)) {
            throw new InvalidParameterException(errorMessage);
        }
    }

    public static void validateCurrenciesAreDifferent(String baseCode, String targetCode) {
        if (baseCode.equals(targetCode)) {
            throw new InvalidParameterException(ValidationConstants.WRONG_PAIR_MESSAGE);
        }
    }
}

