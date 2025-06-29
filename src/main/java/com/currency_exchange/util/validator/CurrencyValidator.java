package com.currency_exchange.util.validator;

import com.currency_exchange.exception.service_exception.InvalidAttributeException;
import com.currency_exchange.util.CurrencyRequest;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;
import java.util.Set;

public final class CurrencyValidator {
    public static final String MISSING_CURRENCY_CODE = "Currency code is missing in URL (expected format: /currency/USD)";
    public static final String WRONG_CURRENCY_CODE = "Currency code must be 3 latin letters (e.g. USD)";
    public static final String MISSING_REQUIRED_CURRENCY_PARAM = "Missing required currency param";
    public static final String PARAMETERS_NOT_ALLOWED = "Parameters are not allowed here";
    public static final String WRONG_PARAM_NAME = "Invalid request: wrong param name -> %s";
    public static final String ONE_PARAM_REQUIRED = "Invalid request: only one param value required";

    public static final String CODE_REGEX = "[A-Za-z]{3}";

    private static final Set<String> REQUIRED_CURRENCY_PARAMS = Set.of(
            CurrencyRequest.NAME.getParamName(),
            CurrencyRequest.CODE.getParamName(),
            CurrencyRequest.SIGN.getParamName()
    );
    public static final String INVALID_REQUEST = "Invalid request: %s";

    private CurrencyValidator() {
    }

    public static void validateGet(HttpServletRequest request) {
        if (!request.getParameterMap().isEmpty()) {
            throw new InvalidAttributeException(PARAMETERS_NOT_ALLOWED);
        }
    }

    public static void validatePost(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();

        validateCorrectParameters(parameterMap);

        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            String paramName = entry.getKey();
            String[] paramValues = entry.getValue();

            validateParamName(paramName);
            validateSingleValueOfParam(paramValues);
            validateRegex(paramName, paramValues);
        }
    }

    public static void validate(String pathInfo) {
        if (pathInfo == null || pathInfo.matches("/")) {
            throw new InvalidAttributeException(MISSING_CURRENCY_CODE);
        }
    }

    public static void validateCode(String code) {
        if (!code.matches(CODE_REGEX)) {
            throw new InvalidAttributeException(WRONG_CURRENCY_CODE);
        }
    }

    private static void validateParamName(String paramName) {
        if (!REQUIRED_CURRENCY_PARAMS.contains(paramName)) {
            throw new InvalidAttributeException(WRONG_PARAM_NAME.formatted(paramName));
        }
    }

    private static void validateSingleValueOfParam(String[] paramValues) {
        if (paramValues.length > 1) {
            throw new InvalidAttributeException(ONE_PARAM_REQUIRED);
        }
    }

    private static void validateRegex(String paramName, String[] paramValues) {
        CurrencyRequest currencyRequest = CurrencyRequest.fromParamName(paramName);
        String paramValue = paramValues[0];

        if (!paramValue.matches(currencyRequest.getRegex())) {
            throw new InvalidAttributeException(INVALID_REQUEST.formatted(currencyRequest.getErrorMessage()));
        }
    }

    private static void validateCorrectParameters(Map<String, String[]> parameterMap) {
        if (!parameterMap.keySet().containsAll(REQUIRED_CURRENCY_PARAMS)) {
            throw new InvalidAttributeException(MISSING_REQUIRED_CURRENCY_PARAM);
        }
    }
}
