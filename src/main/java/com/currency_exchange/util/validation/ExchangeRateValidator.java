package com.currency_exchange.util.validation;

import com.currency_exchange.ExchangeCalculationParam;
import com.currency_exchange.ExchangeRateParam;
import com.currency_exchange.RequestParameter;
import com.currency_exchange.exception.service_exception.InvalidParameterException;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;
import java.util.Set;

public final class ExchangeRateValidator {

    private static final String WRONG_CURRENCY_PAIR_PATH = "Path must be 6 latin letters like /USDRUB";
    private static final String WRONG_PAIR_MESSAGE = "Base and target currencies must be different";
    private static final String CURRENCY_PAIR_PATTERN = "^/([A-Za-z]{3})([A-Za-z]{3})$";

    private ExchangeRateValidator() {
    }

    public static void validateExchangeRatePostRequest(HttpServletRequest request) {
        Map<String, String[]> params = request.getParameterMap();
        Set<String> requiredParamNames = RequestParameter.getRequiredParamNames(ExchangeRateParam.class);

        ValidationUtils.validateRequiredParameters(params, requiredParamNames);
        validateCurrencyIsTheSame(request);

        params.forEach((param, values) -> {
            ValidationUtils.validateSingleValue(param, values);
            validateExchangeRateParameter(param, values[0]);
        });
    }

    public static void validateCalculationRequest(HttpServletRequest request) {
        Map<String, String[]> params = request.getParameterMap();
        Set<String> requiredParamNames = RequestParameter.getRequiredParamNames(ExchangeCalculationParam.class);

        ValidationUtils.validateRequiredParameters(params, requiredParamNames);

        params.forEach((param, values) -> {
            ValidationUtils.validateSingleValue(param, values);
            validateCalculationParameter(param, values[0]);
        });
    }

    public static void validateRequiredPatchParameters(HttpServletRequest request) {
        String rateParamName = ExchangeRateParam.RATE.getParamName();

        if (request.getParameter(rateParamName) == null) {
            throw new InvalidParameterException(
                    String.format(ValidationUtils.MISSING_REQUIRED_PARAMS, rateParamName)
            );
        }
    }

    public static void validateCurrencyPair(String currencyPair) {
        if (!currencyPair.matches(CURRENCY_PAIR_PATTERN)) {
            throw new InvalidParameterException(WRONG_CURRENCY_PAIR_PATH);
        }
    }

    public static void validateRate(String rate) {
        if (!rate.matches(ExchangeRateParam.RATE.getRegex())) {
            throw new InvalidParameterException(ExchangeRateParam.RATE.getErrorMessage());
        }
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

    private static void validateExchangeRateParameter(String paramName, String value) {
        ExchangeRateParam param = RequestParameter.fromParamName(ExchangeRateParam.class, paramName);
        String regex = param.getRegex();

        if (!value.trim().matches(regex)) {
            throw new InvalidParameterException(
                    String.format(ValidationUtils.INVALID_REQUEST, param.getErrorMessage())
            );
        }
    }

    private static void validateCalculationParameter(String paramName, String value) {
        ExchangeCalculationParam param = RequestParameter.fromParamName(ExchangeCalculationParam.class, paramName);

        if (!value.trim().matches(param.getRegex())) {
            throw new InvalidParameterException(
                    String.format(ValidationUtils.INVALID_REQUEST, param.getErrorMessage())
            );
        }
    }
}