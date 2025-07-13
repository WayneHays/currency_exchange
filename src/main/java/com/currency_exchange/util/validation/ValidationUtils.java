package com.currency_exchange.util.validation;

import com.currency_exchange.exception.service_exception.InvalidParameterException;
import jakarta.servlet.http.HttpServletRequest;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class ValidationUtils {

    public static final String PARAMETERS_NOT_ALLOWED = "Parameters are not allowed here";
    public static final String SINGLE_PARAMETER_VALUE_REQUIRED = "Parameter '%s' should have exactly one value";
    public static final String MISSING_REQUIRED_PARAMS = "Missing required parameters: %s";
    public static final String INVALID_REQUEST = "Invalid request: %s";
    public static final String WRONG_PAIR_MESSAGE = "Base and target currencies must be different";

    private ValidationUtils() {
    }

    public static void validateGetRequest(HttpServletRequest request) {
        if (!request.getParameterMap().isEmpty()) {
            throw new InvalidParameterException(PARAMETERS_NOT_ALLOWED);
        }
    }

    public static void validatePath(String input, String errorMessage) {
        if (input == null || input.equals("/")) {
            throw new InvalidParameterException(errorMessage);
        }
    }

    public static void validateRequiredParameters(Map<String, String[]> parameters,
                                                  Set<String> requiredParams) {
        Set<String> missingParams = new HashSet<>(requiredParams);
        missingParams.removeAll(parameters.keySet());

        if (!missingParams.isEmpty()) {
            throw new InvalidParameterException(
                    String.format(MISSING_REQUIRED_PARAMS, String.join(", ", missingParams))
            );
        }
    }

    public static void validateSingleValue(String paramName, String[] values) {
        if (values == null || values.length != 1) {
            throw new InvalidParameterException(
                    String.format(SINGLE_PARAMETER_VALUE_REQUIRED, paramName)
            );
        }
    }

    public static void validateCurrenciesAreDifferent(HttpServletRequest request,
                                                      String baseCode,
                                                      String targetCode) {

        String baseCurrencyCode = request.getParameter(baseCode);
        String targetCurrencyCode = request.getParameter(targetCode);

        if (baseCurrencyCode.equals(targetCurrencyCode)) {
            throw new InvalidParameterException(ValidationUtils.WRONG_PAIR_MESSAGE);
        }
    }
}
