package com.currency_exchange.util;

import com.currency_exchange.CurrencyParam;
import com.currency_exchange.RequestParameter;
import com.currency_exchange.exception.service_exception.InvalidParameterException;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;
import java.util.Set;

public final class CurrencyValidator {

    private CurrencyValidator() {
    }

    public static void validateCurrenciesPostRequest(HttpServletRequest request) {
        Map<String, String[]> params = request.getParameterMap();
        Set<String> requiredParamNames = RequestParameter.getRequiredParamNames(CurrencyParam.class);

        ValidationUtils.validateRequiredParameters(params, requiredParamNames);

        params.forEach((param, values) -> {
            ValidationUtils.validateSingleValue(param, values);
            validateCurrencyParameter(param, values[0]);
        });
    }

    public static void validateCurrencyCode(String currencyCode) {
        if (!currencyCode.matches(CurrencyParam.CODE.getRegex())) {
            throw new InvalidParameterException(CurrencyParam.CODE.getErrorMessage());
        }
    }

    private static void validateCurrencyParameter(String paramName, String value) {
        CurrencyParam param = RequestParameter.fromParamName(CurrencyParam.class, paramName);

        if (!value.trim().matches(param.getRegex())) {
            throw new InvalidParameterException(
                    String.format(ValidationUtils.INVALID_REQUEST, param.getErrorMessage())
            );
        }
    }
}
