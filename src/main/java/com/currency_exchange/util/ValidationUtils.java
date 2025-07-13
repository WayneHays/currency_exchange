package com.currency_exchange.util;

import com.currency_exchange.CalculationParam;
import com.currency_exchange.CurrencyParam;
import com.currency_exchange.ExchangeRateParam;
import com.currency_exchange.exception.service_exception.InvalidParameterException;

public final class ValidationUtils {
    public static final String WRONG_PAIR_MESSAGE = "Base and target currencies must be different";

    private ValidationUtils() {
    }

    public static void validateExchangeRateCreateRequest(
            String baseCurrencyCode,
            String targetCurrencyCode,
            String rate) {
        validateCurrenciesAreDifferent(baseCurrencyCode, targetCurrencyCode);
        validateCurrencyCode(baseCurrencyCode);
        validateCurrencyCode(targetCurrencyCode);
        validateRate(String.valueOf(rate));
    }

    public static void validateCurrencyCreateRequest(String code, String name, String sign) {
        validateCurrencyCode(code);
        validateCurrencyName(name);
        validateCurrencySign(sign);
    }

    private static void validateCurrencyName(String name) {
        if (!name.matches(CurrencyParam.NAME.getRegex())) {
            throw new InvalidParameterException(CurrencyParam.CODE.getErrorMessage());
        }
    }

    private static void validateCurrencySign(String sign) {
        if (!sign.matches(CurrencyParam.SIGN.getRegex())) {
            throw new InvalidParameterException(CurrencyParam.CODE.getErrorMessage());
        }
    }

    public static void validateExchangeCalculationRequest(String from, String to, String amount) {
        validateCurrenciesAreDifferent(from, to);
        validateCurrencyCode(from);
        validateCurrencyCode(to);
        validateAmount(amount);
    }

    private static void validateAmount(String amount) {
        if (!amount.matches(CalculationParam.AMOUNT.getRegex())) {
            throw new InvalidParameterException(CurrencyParam.CODE.getErrorMessage());
        }
    }

    public static void validateCurrenciesAreDifferent(String baseCode, String targetCode) {
        if (baseCode.equals(targetCode)) {
            throw new InvalidParameterException(ValidationUtils.WRONG_PAIR_MESSAGE);
        }
    }

    public static void validateCurrencyCode(String currencyCode) {
        if (!currencyCode.matches(CurrencyParam.CODE.getRegex())) {
            throw new InvalidParameterException(CurrencyParam.CODE.getErrorMessage());
        }
    }

    public static void validateRate(String rate) {
        if (!rate.matches(ExchangeRateParam.RATE.getRegex())) {
            throw new InvalidParameterException(ExchangeRateParam.RATE.getErrorMessage());
        }
    }
}
