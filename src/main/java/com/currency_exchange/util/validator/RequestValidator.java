package com.currency_exchange.util.validator;

import com.currency_exchange.exception.InvalidParameterException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class RequestValidator {

    public static final String SINGLE_VALUE_MESSAGE = "Parameter must have single value: %s";
    public static final String MISSING_PARAMS_MESSAGE = "Missing parameters: %s";

    private RequestValidator() {
    }

    public static void validateRequiredParams(Map<String, String[]> params, String... requiredParams) {
        List<String> missing = new ArrayList<>();
        for (String param : requiredParams) {
            if (!params.containsKey(param) ||
                params.get(param) == null ||
                params.get(param).length == 0 ||
                params.get(param)[0].trim().isEmpty()) {
                missing.add(param);
            }
        }
        if (!missing.isEmpty()) {
            throw new InvalidParameterException(
                    MISSING_PARAMS_MESSAGE.formatted(String.join(", ", missing)));
        }
    }

    public static void validateSingleParamValue(Map<String, String[]> params, String... paramNames) {
        for (String param : paramNames) {
            if (params.containsKey(param) &&
                params.get(param) != null &&
                params.get(param).length > 1) {
                throw new InvalidParameterException(SINGLE_VALUE_MESSAGE.formatted(param));
            }
        }
    }

    public static void validateStringPattern(String input, String pattern, String errorMessage) {
        if (!input.matches(pattern)) {
            throw new InvalidParameterException(errorMessage);
        }
    }
}

