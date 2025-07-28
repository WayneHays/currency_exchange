package com.currency_exchange.util.validator;

import java.util.Map;

import static com.currency_exchange.util.constant.HttpParameterNames.*;

public final class ExchangeRateValidator {

    private ExchangeRateValidator() {
    }

    public static void validateCreateRequest(Map<String, String[]> params) {
        ValidationUtils.validateRequest(params, BASE_CURRENCY_CODE, TARGET_CURRENCY_CODE, RATE);
    }

    public static void validateUpdateRequest(Map<String, String[]> params) {
        ValidationUtils.validateRequest(params, RATE);
    }

    public static void validateFields(String baseCurrencyCode, String targetCurrencyCode, String rate) {
        ValidationUtils.validateCurrencyCode(baseCurrencyCode);
        ValidationUtils.validateCurrencyCode(targetCurrencyCode);
        ValidationUtils.validateRate(rate);
        ValidationUtils.checkCurrenciesAreDifferent(baseCurrencyCode, targetCurrencyCode);
    }

    public static void validateRateField(String rate) {
        ValidationUtils.validateRate(rate);
    }
}
