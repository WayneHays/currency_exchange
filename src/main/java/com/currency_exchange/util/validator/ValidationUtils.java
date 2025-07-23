package com.currency_exchange.util.validator;

import com.currency_exchange.exception.InvalidParameterException;

import java.util.Map;

import static com.currency_exchange.util.ValidationConstants.*;

public final class ValidationUtils {

    private ValidationUtils() {
    }

    public static void validateRequest(Map<String, String[]> params, String... requiredParams) {
        RequestValidator.validateRequiredParams(params, requiredParams);
        RequestValidator.validateSingleParamValue(params, requiredParams);
    }

    public static void validateCurrencyCode(String code) {
        RequestValidator.validateStringPattern(code, CODE_PATTERN, CODE_ERROR_MESSAGE);
    }

    public static void validateRate(String rate) {
        RequestValidator.validateStringPattern(rate, RATE_PATTERN, RATE_ERROR_MESSAGE);
    }

    public static void validateAmount(String amount) {
        RequestValidator.validateStringPattern(amount, RATE_PATTERN, AMOUNT_ERROR_MESSAGE);
    }

    public static void validateCurrencyName(String name) {
        RequestValidator.validateStringPattern(name, NAME_PATTERN, NAME_ERROR_MESSAGE);
    }

    public static void validateCurrencySign(String sign) {
        RequestValidator.validateStringPattern(sign, SIGN_PATTERN, SIGN_ERROR_MESSAGE);
    }

    public static void checkCurrenciesAreDifferent(String baseCode, String targetCode) {
        if (baseCode.equals(targetCode)) {
            throw new InvalidParameterException(WRONG_PAIR_MESSAGE);
        }
    }
}
