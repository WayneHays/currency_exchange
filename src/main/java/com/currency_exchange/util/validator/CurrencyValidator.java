package com.currency_exchange.util.validator;

import com.currency_exchange.constant.HttpParameterNames;

import java.util.Map;

import static com.currency_exchange.constant.HttpParameterNames.NAME;
import static com.currency_exchange.constant.HttpParameterNames.SIGN;

public final class CurrencyValidator {

    private CurrencyValidator() {
    }

    public static void validateCreateRequest(Map<String, String[]> params) {
        ValidationUtils.validateRequest(params, HttpParameterNames.CODE, NAME, SIGN);
    }

    public static void validateFields(String code, String name, String sign) {
        ValidationUtils.validateCurrencyCode(code);
        ValidationUtils.validateCurrencyName(name);
        ValidationUtils.validateCurrencySign(sign);
    }
}
