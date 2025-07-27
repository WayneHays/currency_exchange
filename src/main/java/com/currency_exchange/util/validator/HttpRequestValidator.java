package com.currency_exchange.util.validator;

import com.currency_exchange.exception.InvalidParameterException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class HttpRequestValidator {
    public static final String SINGLE_VALUE_MESSAGE = "Parameter must have single value: %s";
    public static final String MISSING_PARAMS_MESSAGE = "Missing parameters: %s";

    private HttpRequestValidator() {
    }

    public static void validateRequiredParams(Map<String, String[]> params, String... requiredParams) {
        List<String> missing = new ArrayList<>();

        for (String param : requiredParams) {
            boolean found = params.keySet().stream()
                    .anyMatch(key -> key.trim().equals(param));

            if (!found) {
                missing.add(param);
                continue;
            }

            String[] values = params.entrySet().stream()
                    .filter(entry -> entry.getKey().trim().equals(param))
                    .map(Map.Entry::getValue)
                    .findFirst()
                    .orElse(null);

            if (values == null || values.length == 0 || values[0].trim().isEmpty()) {
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

    private static String[] getParameterByTrimmedKey(Map<String, String[]> params, String key) {
        return params.entrySet().stream()
                .filter(entry -> entry.getKey().trim().equals(key))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
    }
}

