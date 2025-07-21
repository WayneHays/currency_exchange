package com.currency_exchange.util.validator;

import java.util.Map;

import static com.currency_exchange.util.ValidationConstants.*;
import static com.currency_exchange.util.validator.RequestValidator.*;

public final class CurrencyValidator {

    private CurrencyValidator() {

    }

    public static void validateCreateRequest(Map<String, String[]> params) {
        checkRequired(params, CODE, NAME, SIGN);
        checkSingle(params, CODE, NAME, SIGN);
        validateParamPattern(params, CODE, CODE_PATTERN, CODE_ERROR_MESSAGE);
        validateParamPattern(params, NAME, NAME_PATTERN, NAME_ERROR_MESSAGE);
        validateParamPattern(params, SIGN, SIGN_PATTERN, SIGN_ERROR_MESSAGE);
    }
}
