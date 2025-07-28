package com.currency_exchange.util.validator;

import com.currency_exchange.constant.HttpParameterNames;

import java.util.Map;

import static com.currency_exchange.constant.HttpParameterNames.AMOUNT;
import static com.currency_exchange.constant.HttpParameterNames.TO;

public final class CalculationValidator {

    private CalculationValidator() {
    }

    public static void validateRequest(Map<String, String[]> params) {
        ValidationUtils.validateRequest(params, HttpParameterNames.FROM, TO, AMOUNT);
    }

    public static void validateFields(String from, String to, String amount) {
        ValidationUtils.validateCurrencyCode(from);
        ValidationUtils.validateCurrencyCode(to);
        ValidationUtils.validateAmount(amount);
        ValidationUtils.checkCurrenciesAreDifferent(from, to);
    }
}
