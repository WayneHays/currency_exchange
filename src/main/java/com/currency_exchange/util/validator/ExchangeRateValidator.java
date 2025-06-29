package com.currency_exchange.util.validator;

import com.currency_exchange.exception.service_exception.InvalidAttributeException;
import com.currency_exchange.util.ExchangeRateRequest;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;
import java.util.Set;

public final class ExchangeRateValidator {
    public static final String INVALID_CURRENCY_PAIR_FORMAT = "Currency pair must be 6 latin letters (e.g. USDRUB)";
    public static final String PARAMETERS_NOT_ALLOWED = "Parameters are not allowed here";
    public static final String MISSING_EXCHANGE_RATE_PARAMS = "Missing require exchangeRate parameters";
    public static final String SINGLE_PARAMETER_VALUE_REQUIRED = "Parameter '%s' should have exactly one value";

    private static final Set<String> REQUIRED_EXCHANGE_RATE_PARAMS = Set.of(
            ExchangeRateRequest.BASE_CURRENCY_CODE.getName(),
            ExchangeRateRequest.TARGET_CURRENCY_CODE.getName(),
            ExchangeRateRequest.RATE.getName());

    public static final String CURRENCY_PAIR_REGEX = "^/[A-Za-z]{6}$";


    private ExchangeRateValidator() {
    }

    public static void validatePath(String pathInfo) {
        if (pathInfo == null || !pathInfo.toUpperCase().matches(CURRENCY_PAIR_REGEX)) {
            throw new InvalidAttributeException(INVALID_CURRENCY_PAIR_FORMAT);
        }
    }

    public static void validateEmptyRequest(HttpServletRequest request) throws InvalidAttributeException {
        if (!request.getParameterMap().isEmpty()) {
            throw new InvalidAttributeException(PARAMETERS_NOT_ALLOWED);
        }
    }

    public static void validateRequestParameters(HttpServletRequest request) {
        Map<String, String[]> parameters = request.getParameterMap();
        validateRequiredParameters(parameters);

        parameters.forEach((param, values) -> {
            validateSingleValue(param, values);
            validateParameterValue(param, values[0]);
        });
    }

    private static void validateParameterValue(String param, String value) {
        ExchangeRateRequest paramType = ExchangeRateRequest.fromParamName(param);
        if (!value.matches(paramType.getRegex())) {
            throw new InvalidAttributeException(paramType.getErrorMessage());
        }
    }

    private static void validateRequiredParameters(Map<String, String[]> parameterMap) {
        if (!parameterMap.keySet().containsAll(REQUIRED_EXCHANGE_RATE_PARAMS)) {
            throw new InvalidAttributeException(MISSING_EXCHANGE_RATE_PARAMS);
        }
    }

    private static void validateSingleValue(String paramName, String[] values) {
        if (values.length != 1) {
            throw new InvalidAttributeException(
                    String.format(SINGLE_PARAMETER_VALUE_REQUIRED, paramName));
        }
    }
}
