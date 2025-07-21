package com.currency_exchange.util;

import com.currency_exchange.exception.InvalidParameterException;

import java.util.List;
import java.util.Map;
import java.util.Set;

public final class Validator {

    private Validator() {
    }

    public static void validateCurrencyCreateRequest(Map<String, String[]> params) {
        Set<String> requiredParams = Set.of(
                ValidationConstants.CODE_PARAM,
                ValidationConstants.NAME_PARAM,
                ValidationConstants.SIGN_PARAM);

        checkMissingParameters(params, requiredParams);
        validateSingleValueOfEachParam(params, requiredParams);

        String code = params.get(ValidationConstants.CODE_PARAM)[0].trim();
        String name = params.get(ValidationConstants.NAME_PARAM)[0].trim();
        String sign = params.get(ValidationConstants.SIGN_PARAM)[0].trim();

        validateParamRegex(
                code,
                ValidationConstants.CURRENCY_CODE_PATTERN,
                ValidationConstants.CURRENCY_CODE_ERROR_MESSAGE);
        validateParamRegex(
                name,
                ValidationConstants.CURRENCY_NAME_PATTERN,
                ValidationConstants.CURRENCY_NAME_ERROR_MESSAGE);
        validateParamRegex(
                sign,
                ValidationConstants.CURRENCY_SIGN_PATTERN,
                ValidationConstants.CURRENCY_SIGN_ERROR_MESSAGE);
    }

    public static void validateExchangeRateCreateRequest(Map<String, String[]> params) {
        Set<String> requiredParams = Set.of(
                ValidationConstants.BASE_CURRENCY_CODE,
                ValidationConstants.TARGET_CURRENCY_CODE,
                ValidationConstants.RATE);

        checkMissingParameters(params, requiredParams);
        validateSingleValueOfEachParam(params, requiredParams);

        String baseCurrencyCode = params.get(ValidationConstants.BASE_CURRENCY_CODE)[0].trim();
        String targetCurrencyCode = params.get(ValidationConstants.TARGET_CURRENCY_CODE)[0].trim();
        String rate = params.get(ValidationConstants.RATE)[0].trim();

        validateCurrenciesAreDifferent(baseCurrencyCode, targetCurrencyCode);
        validateParamRegex(
                baseCurrencyCode,
                ValidationConstants.CURRENCY_CODE_PATTERN,
                ValidationConstants.CURRENCY_CODE_ERROR_MESSAGE);
        validateParamRegex(
                targetCurrencyCode,
                ValidationConstants.CURRENCY_CODE_PATTERN,
                ValidationConstants.CURRENCY_CODE_ERROR_MESSAGE);
        validateParamRegex(
                rate,
                ValidationConstants.RATE_PATTERN,
                ValidationConstants.RATE_ERROR_MESSAGE);
    }

    public static void validateCalculationRequest(Map<String, String[]> params) {
        Set<String> requiredParams = Set.of(
                ValidationConstants.FROM,
                ValidationConstants.TO,
                ValidationConstants.AMOUNT
        );

        checkMissingParameters(params, requiredParams);
        validateSingleValueOfEachParam(params, requiredParams);

        String from = params.get(ValidationConstants.FROM)[0].trim();
        String to = params.get(ValidationConstants.TO)[0].trim();
        String amount = params.get(ValidationConstants.AMOUNT)[0].trim();

        validateCurrenciesAreDifferent(from, to);
        validateParamRegex(
                from,
                ValidationConstants.CURRENCY_CODE_PATTERN,
                ValidationConstants.CURRENCY_CODE_ERROR_MESSAGE
        );
        validateParamRegex(
                to,
                ValidationConstants.CURRENCY_CODE_PATTERN,
                ValidationConstants.CURRENCY_CODE_ERROR_MESSAGE
        );
        validateParamRegex(
                amount,
                ValidationConstants.RATE_PATTERN,
                ValidationConstants.CALCULATION_AMOUNT_ERROR_MESSAGE
        );
    }

    public static void validatePatchRequest(Map<String, String[]> params) {
        Set<String> requiredParams = Set.of(ValidationConstants.RATE);
        checkMissingParameters(params, requiredParams);
        validateSingleValueOfEachParam(params, requiredParams);

        String rate = params.get(ValidationConstants.RATE)[0].trim();
        validateParamRegex(
                rate,
                ValidationConstants.RATE_PATTERN,
                ValidationConstants.RATE_ERROR_MESSAGE);
    }

    public static void validateParamRegex(String param, String regEx, String errorMessage) {
        if (!param.matches(regEx)) {
            throw new InvalidParameterException(errorMessage);
        }
    }

    public static void validateNotEmpty(String path, String errorMessage) {
        if (path == null || "/".equals(path)) {
            throw new InvalidParameterException(errorMessage);
        }
    }

    public static void validateCurrenciesAreDifferent(String baseCode, String targetCode) {
        if (baseCode.equals(targetCode)) {
            throw new InvalidParameterException(ValidationConstants.WRONG_PAIR_MESSAGE);
        }
    }

    private static void checkMissingParameters(Map<String, String[]> params, Set<String> requiredParams) {
        List<String> missingParameters = requiredParams.stream()
                .filter(paramName ->
                        !params.containsKey(paramName) ||
                        params.get(paramName) == null ||
                        params.get(paramName).length == 0 ||
                        params.get(paramName)[0].isEmpty()
                )
                .toList();

        if (!missingParameters.isEmpty()) {
            throw new InvalidParameterException(
                    "Missing parameters: " + String.join(", ", missingParameters)
            );
        }
    }

    private static void validateSingleValueOfEachParam(Map<String, String[]> params, Set<String> requiredParams) {
        List<String> multiValueParameters = requiredParams.stream()
                .filter(paramName ->
                        params.containsKey(paramName) &&
                        params.get(paramName) != null &&
                        params.get(paramName).length > 1
                )
                .toList();

        if (!multiValueParameters.isEmpty()) {
            throw new InvalidParameterException(
                    "Parameters must be single-valued: " + String.join(", ", multiValueParameters)
            );
        }
    }
}

