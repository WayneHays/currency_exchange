package com.currency_exchange.util;

import com.currency_exchange.CurrencyRequest;
import com.currency_exchange.ExchangeRateRequest;
import com.currency_exchange.exception.service_exception.InvalidAttributeException;
import jakarta.servlet.http.HttpServletRequest;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class ValidationUtils {
    public static final String PARAMETERS_NOT_ALLOWED = "Parameters are not allowed here";
    public static final String SINGLE_PARAMETER_VALUE_REQUIRED = "Parameter '%s' should have exactly one value";
    public static final String MISSING_REQUIRED_PARAMS = "Missing required parameters: %s";
    public static final String INVALID_REQUEST = "Invalid request: %s";
    public static final String WRONG_CURRENCY_PAIR_PATH = "Path must be 6 latin letters like /USDRUB";
    public static final String CURRENCY_PAIR_PATTERN = "^/([A-Za-z]{3})([A-Za-z]{3})$";

    private ValidationUtils() {
    }

    public static void validateGetRequest(HttpServletRequest request) {
        if (!request.getParameterMap().isEmpty()) {
            throw new InvalidAttributeException(PARAMETERS_NOT_ALLOWED);
        }
    }

    public static void validateRequiredParameters(Map<String, String[]> parameters,
                                                  Set<String> requiredParams) {
        Set<String> missingParams = new HashSet<>(requiredParams);
        missingParams.removeAll(parameters.keySet());

        if (!missingParams.isEmpty()) {
            throw new InvalidAttributeException(
                    String.format(MISSING_REQUIRED_PARAMS, String.join(", ", missingParams))
            );
        }
    }

    public static void validateSingleValue(String paramName, String[] values) {
        if (values == null || values.length != 1) {
            throw new InvalidAttributeException(
                    String.format(SINGLE_PARAMETER_VALUE_REQUIRED, paramName)
            );
        }
    }

    public static void validateCurrenciesPostRequest(HttpServletRequest request) {
        Map<String, String[]> params = request.getParameterMap();
        validateRequiredParameters(params, CurrencyRequest.getRequiredParamNames());

        params.forEach((param, values) -> {
            validateSingleValue(param, values);
            validateCurrencyParameter(param, values[0]);
        });
    }

    public static void validateExchangeRatePostRequest(HttpServletRequest request) {
        Map<String, String[]> params = request.getParameterMap();
        validateRequiredParameters(params, ExchangeRateRequest.getRequiredParamNames());

        params.forEach((param, values) -> {
            validateSingleValue(param, values);
            validateExchangeRateParameter(param, values[0]);
        });
    }

    private static void validateCurrencyParameter(String paramName, String value) {
        CurrencyRequest param = CurrencyRequest.fromParamName(paramName);
        if (!value.trim().matches(param.getRegex())) {
            throw new InvalidAttributeException(
                    String.format(INVALID_REQUEST, param.getErrorMessage())
            );
        }
    }

    private static void validateExchangeRateParameter(String paramName, String value) {
        ExchangeRateRequest param = ExchangeRateRequest.fromParamName(paramName);
        if (!value.trim().matches(param.getRegex())) {
            throw new InvalidAttributeException(
                    String.format(INVALID_REQUEST, param.getErrorMessage()));
        }
    }

    public static void validatePath(String input, String errorMessage) {
        if (input == null || input.equals("/")) {
            throw new InvalidAttributeException(errorMessage);
        }
    }

    public static void validateCurrencyCode(String code) {
        if (!code.matches(CurrencyRequest.CODE.getRegex())) {
            throw new InvalidAttributeException(CurrencyRequest.CODE.getErrorMessage());
        }
    }

    public static void validateCurrencyPair(String pair) {
        if (!pair.matches(CURRENCY_PAIR_PATTERN)) {
            throw new InvalidAttributeException(WRONG_CURRENCY_PAIR_PATH);
        }
    }
}
