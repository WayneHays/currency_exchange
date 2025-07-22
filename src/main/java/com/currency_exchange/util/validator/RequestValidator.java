package com.currency_exchange.util.validator;

import com.currency_exchange.exception.InvalidParameterException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class RequestValidator {

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
                    "Missing parameters: %s".formatted(String.join(", ", missing)));
        }
    }

    public static void validateSingleParamValue(Map<String, String[]> params, String... paramNames) {
        for (String param : paramNames) {
            if (params.containsKey(param) &&
                params.get(param) != null &&
                params.get(param).length > 1) {
                throw new InvalidParameterException("Parameter must have single value: %s".formatted(param));
            }
        }
    }

    public static void validateStringPattern(String input, String pattern, String errorMessage) {
        if (!input.matches(pattern)) {
            throw new InvalidParameterException(errorMessage);
        }
    }
}

