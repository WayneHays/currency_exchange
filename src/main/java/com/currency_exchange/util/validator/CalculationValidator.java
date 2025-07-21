package com.currency_exchange.util.validator;

import java.util.Map;

import static com.currency_exchange.util.ValidationConstants.*;
import static com.currency_exchange.util.validator.RequestValidator.*;

public final class CalculationValidator {

    private CalculationValidator() {
    }

    public static void validateCalculationRequest(Map<String, String[]> params) {
        checkRequired(params, FROM, TO, AMOUNT);
        checkSingle(params, FROM, TO, AMOUNT);
        validateParamPattern(params, FROM, CODE_PATTERN, CODE_ERROR_MESSAGE);
        validateParamPattern(params, TO, CODE_PATTERN, CODE_ERROR_MESSAGE);
        validateParamPattern(params, AMOUNT, RATE_PATTERN, AMOUNT_ERROR_MESSAGE);

        String fromCode = params.get("from")[0].trim();
        String toCode = params.get("to")[0].trim();
        validateCurrenciesAreDifferent(fromCode, toCode);
    }
}
