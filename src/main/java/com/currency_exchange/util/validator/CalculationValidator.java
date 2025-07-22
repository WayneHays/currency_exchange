package com.currency_exchange.util.validator;

import java.util.Map;

import static com.currency_exchange.util.ValidationConstants.*;

public final class CalculationValidator {

    private CalculationValidator() {
    }

    public static void validateRequest(Map<String, String[]> params) {
        ValidationUtils.validateRequest(params, FROM, TO, AMOUNT);
    }

    public static void validateFields(String from, String to, String amount) {
        ValidationUtils.validateCurrencyCode(from);
        ValidationUtils.validateCurrencyCode(to);
        ValidationUtils.validateAmount(amount);
        ValidationUtils.checkCurrenciesAreDifferent(from, to);
    }
}
