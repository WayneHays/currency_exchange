package com.currency_exchange.util.validator;

import com.currency_exchange.exception.InvalidParameterException;

import java.util.Map;

import static com.currency_exchange.util.ValidationConstants.*;
import static com.currency_exchange.util.validator.RequestValidator.*;

public final class ExchangeRateValidator {

    private ExchangeRateValidator() {
    }

    public static void validateCreateRequest(Map<String, String[]> params) {
        checkRequired(params, BASE_CURRENCY_CODE, TARGET_CURRENCY_CODE, RATE);
        checkSingle(params, BASE_CURRENCY_CODE, TARGET_CURRENCY_CODE, RATE);
        validateParamPattern(params, BASE_CURRENCY_CODE, CODE_PATTERN, CODE_ERROR_MESSAGE);
        validateParamPattern(params, TARGET_CURRENCY_CODE, CODE_PATTERN, CODE_ERROR_MESSAGE);
        validateParamPattern(params, RATE, RATE_PATTERN, RATE_ERROR_MESSAGE);

        String baseCode = params.get(BASE_CURRENCY_CODE)[0].trim();
        String targetCode = params.get(TARGET_CURRENCY_CODE)[0].trim();
        if (baseCode.equals(targetCode)) {
            throw new InvalidParameterException(WRONG_PAIR_MESSAGE);
        }
    }

    public static void validateUpdateRequest(Map<String, String[]> params) {
        checkRequired(params, RATE);
        checkSingle(params, RATE);
        validateParamPattern(params, RATE, RATE_PATTERN, RATE_ERROR_MESSAGE);
    }
}
