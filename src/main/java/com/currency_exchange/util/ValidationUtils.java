package com.currency_exchange.util;

import com.currency_exchange.CurrencyParam;
import com.currency_exchange.ExchangeCalculationParam;
import com.currency_exchange.ExchangeRateParam;
import com.currency_exchange.RequestParameter;
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
    public static final String WRONG_CURRENCY_PAIR_PATH = "Path must be 6 latin letters like /USDRUB";
    public static final String WRONG_PAIR_MESSAGE = "Base and target currencies must be different";
    public static final String CURRENCY_PAIR_PATTERN = "^/([A-Za-z]{3})([A-Za-z]{3})$";

    private ValidationUtils() {
    }

    public static void validateGetRequest(HttpServletRequest request) {
        if (hasParameters(request)) {
            throw new InvalidParameterException(PARAMETERS_NOT_ALLOWED);
        }
    }

    public static void validateCurrenciesPostRequest(HttpServletRequest request) {
        Map<String, String[]> params = request.getParameterMap();
        Set<String> requiredParamNames = RequestParameter.getRequiredParamNames(CurrencyParam.class);

        validateRequiredParameters(params, requiredParamNames);

        params.forEach((param, values) -> {
            validateSingleValue(param, values);
            validateCurrencyParameter(param, values[0]);
        });
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

    public static void validateExchangeRatePostRequest(HttpServletRequest request) {
        Map<String, String[]> params = request.getParameterMap();
        Set<String> requiredParamNames = RequestParameter.getRequiredParamNames(ExchangeRateParam.class);

        validateRequiredParameters(params, requiredParamNames);
        validateCurrencyIsTheSame(request);

        params.forEach((param, values) -> {
            validateSingleValue(param, values);
            validateExchangeRateParameter(param, values[0]);
        });
    }

    private static void validateCurrencyIsTheSame(HttpServletRequest request) {
        String baseParam = ExchangeRateParam.BASE_CURRENCY_CODE.getParamName();
        String targetParam = ExchangeRateParam.TARGET_CURRENCY_CODE.getParamName();

        String baseCurrencyCode = request.getParameter(baseParam);
        String targetCurrencyCode = request.getParameter(targetParam);

        if (baseCurrencyCode.equals(targetCurrencyCode)) {
            throw new InvalidParameterException(WRONG_PAIR_MESSAGE);
        }
    }

    public static void validateCalculationRequest(HttpServletRequest req) {
        Map<String, String[]> params = req.getParameterMap();
        Set<String> requiredParamNames = RequestParameter.getRequiredParamNames(ExchangeRateParam.class);

        validateRequiredParameters(params, requiredParamNames);

        params.forEach((param, values) -> {
            validateSingleValue(param, values);
            validateCalculationParameter(param, values[0]);
        });
    }

    public static void validatePath(String input, String errorMessage) {
        if (input == null || input.equals("/")) {
            throw new InvalidParameterException(errorMessage);
        }
    }

    public static void validateCurrencyCode(String code) {
        if (!code.matches(CurrencyParam.CODE.getRegex())) {
            throw new InvalidParameterException(CurrencyParam.CODE.getErrorMessage());
        }
    }

    public static void validateCurrencyPair(String pair) {
        if (!pair.matches(CURRENCY_PAIR_PATTERN)) {
            throw new InvalidParameterException(WRONG_CURRENCY_PAIR_PATH);
        }
    }

    public static void validateRate(String rate) {
        if (!rate.matches(ExchangeRateParam.RATE.getRegex())) {
            throw new InvalidParameterException(ExchangeRateParam.RATE.getErrorMessage());
        }
    }

    public static void validateRequiredPatchParameters(HttpServletRequest req) {
        String rateParamName = ExchangeRateParam.RATE.getParamName();

        if (req.getParameter(rateParamName) == null) {
            throw new InvalidParameterException(MISSING_REQUIRED_PARAMS.formatted(rateParamName));
        }
    }

    private static void validateCalculationParameter(String paramName, String value) {
        ExchangeCalculationParam param = RequestParameter.fromParamName(ExchangeCalculationParam.class, paramName);

        if (!value.trim().matches(param.getRegex())) {
            throw new InvalidParameterException(INVALID_REQUEST.formatted(param.getErrorMessage()));
        }
    }

    private static void validateCurrencyParameter(String paramName, String value) {
        CurrencyParam param = RequestParameter.fromParamName(CurrencyParam.class, paramName);

        if (!value.trim().matches(param.getRegex())) {
            throw new InvalidParameterException(INVALID_REQUEST.formatted(param.getErrorMessage()));
        }
    }

    private static void validateExchangeRateParameter(String paramName, String value) {
        ExchangeRateParam param = RequestParameter.fromParamName(ExchangeRateParam.class, paramName);
        String regex = param.getRegex();

        if (!value.trim().matches(regex)) {
            throw new InvalidParameterException(INVALID_REQUEST.formatted(param.getErrorMessage()));
        }
    }

    private static boolean hasParameters(HttpServletRequest request) {
        return !request.getParameterMap().isEmpty();
    }
}
