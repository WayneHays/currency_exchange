package com.currency_exchange.util.validation;

import com.currency_exchange.CalculationParam;
import com.currency_exchange.RequestParameter;
import com.currency_exchange.exception.service_exception.InvalidParameterException;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;
import java.util.Set;

public final class CalculationValidator {

    private CalculationValidator() {
    }

    public static void validateCalculationRequest(HttpServletRequest request) {
        Map<String, String[]> params = request.getParameterMap();
        Set<String> requiredParamNames = RequestParameter.getRequiredParamNames(CalculationParam.class);

        ValidationUtils.validateRequiredParameters(params, requiredParamNames);


        String fromParam = CalculationParam.FROM.getParamName();
        String toParam = CalculationParam.TO.getParamName();

        String baseCurrencyCode = request.getParameter(fromParam);
        String targetCurrencyCode = request.getParameter(toParam);

        if (baseCurrencyCode.equals(targetCurrencyCode)) {
            throw new InvalidParameterException(ValidationUtils.WRONG_PAIR_MESSAGE);
        }

        params.forEach((param, values) -> {
            ValidationUtils.validateSingleValue(param, values);
            validateCalculationParameter(param, values[0]);
        });
    }

    private static void validateCalculationParameter(String paramName, String value) {
        CalculationParam param = RequestParameter.fromParamName(CalculationParam.class, paramName);

        if (!value.trim().matches(param.getRegex())) {
            throw new InvalidParameterException(
                    String.format(ValidationUtils.INVALID_REQUEST, param.getErrorMessage())
            );
        }
    }
}
