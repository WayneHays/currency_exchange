package com.currency_exchange.util.validator;

import com.currency_exchange.exception.service_exception.InvalidAttributeException;
import com.currency_exchange.util.ExchangeRateRequest;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;
import java.util.Set;

public final class ExchangeRateValidator {
    private static final int REQUIRED_LENGTH_OF_RESPONSE = 6;
    private static final Set<String> REQUIRED_EXCHANGE_RATE_PARAMS = Set.of(
            ExchangeRateRequest.BASE_CURRENCY_CODE.getName(),
            ExchangeRateRequest.TARGET_CURRENCY_CODE.getName(),
            ExchangeRateRequest.RATE.getName());

    private ExchangeRateValidator() {
    }

    public static void validateNoParameters(HttpServletRequest request) throws InvalidAttributeException {
        if (!request.getParameterMap().isEmpty()) {
            throw new InvalidAttributeException("Parameters are not allowed here");
        }
    }

    public static void validateRequestParameters(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        validateCorrectParameters(parameterMap);

        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            String paramName = entry.getKey();
            String[] paramValues = entry.getValue();
            validateSingleValueOfParam(paramValues);
            validateRegex(paramName, paramValues);
        }
    }

    public static void validate(String pathInfo) {
        if (pathInfo == null || !pathInfo.matches("[A-Za-z]{6}")) {
            throw new InvalidAttributeException("Currency pair must be 6 latin letters (e.g. USDRUB)");
        }
    }


    private static void validateRequestLength(HttpServletRequest request) {
        String pair = request.getPathInfo().substring(1);
        if (pair.length() != REQUIRED_LENGTH_OF_RESPONSE) {
            throw new InvalidAttributeException("Wrong currency codes");
        }
    }

    private static void validateCorrectParameters(Map<String, String[]> parameterMap) {
        if (!parameterMap.keySet().containsAll(REQUIRED_EXCHANGE_RATE_PARAMS)) {
            throw new InvalidAttributeException("Not enough exchangeRate parameters");
        }
    }

    private static void validateSingleValueOfParam(String[] paramValues) {
        if (paramValues.length > 1) {
            throw new InvalidAttributeException("Invalid request: only one param value required");
        }
    }

    private static void validateRegex(String paramName, String[] paramValues) {
        ExchangeRateRequest exchangeRateRequest = ExchangeRateRequest.fromParamName(paramName);
        String paramValue = paramValues[0];

        if (!paramValue.matches(exchangeRateRequest.getRegex())) {
            throw new InvalidAttributeException("Invalid request: %s".formatted(exchangeRateRequest.getErrorMessage()));
        }
    }
}
