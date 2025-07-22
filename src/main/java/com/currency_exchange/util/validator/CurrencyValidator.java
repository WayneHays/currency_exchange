package com.currency_exchange.util.validator;

import java.util.Map;

import static com.currency_exchange.util.ValidationConstants.*;

public final class CurrencyValidator {

    private CurrencyValidator() {
    }

    public static void validateCreateRequest(Map<String, String[]> params) {
        ValidationUtils.validateRequest(params, CODE, NAME, SIGN);
    }

    public static void validateFields(String code, String name, String sign) {
        ValidationUtils.validateCurrencyCode(code);
        ValidationUtils.validateCurrencyName(name);
        ValidationUtils.validateCurrencySign(sign);
    }

    public static void validateCurrenciesAreDifferent(String baseCode, String targetCode) {
        ValidationUtils.checkCurrenciesAreDifferent(baseCode, targetCode);
    }
}
