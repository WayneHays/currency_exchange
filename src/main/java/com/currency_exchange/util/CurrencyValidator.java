package com.currency_exchange.util;

import com.currency_exchange.exception.service_exception.InvalidAttributeException;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Set;
import java.util.stream.Collectors;

public final class CurrencyValidator {
    private static final Set<String> REQUIRED_CURRENCY_PARAMS = Set.of("name", "code", "sign");
    private static final String CURRENCY_NAME_REGEX = "^[\\p{L}\\s\\-] {2,50}$";
    private static final String CURRENCY_CODE_REGEX = "^[A-Z]{3}$";
    private static final String CURRENCY_SIGN_REGEX = "^\\p{Sc}";

    private CurrencyValidator() {
    }

    public static void validateNoParameters(HttpServletRequest request) throws InvalidAttributeException {
        if (!request.getParameterMap().isEmpty()) {
            throw new InvalidAttributeException("Parameters are not allowed");
        }
    }

    public static void validate(HttpServletRequest request) {
        validateMissingParameters(request);

        if (!isValidCurrencyName(request.getParameter("name"))) {
            throw new InvalidAttributeException("Invalid currency name");
        }
        if (!isValidCurrencyCode(request.getParameter("code"))) {
            throw new InvalidAttributeException("Invalid currency code");
        }

        if (!isValidCurrencySign(request.getParameter("sign"))) {
            throw new InvalidAttributeException("Invalid currency sign");
        }

    }

    public static void validateMissingParameters(HttpServletRequest request) throws InvalidAttributeException {
        Set<String> requestParameters = request.getParameterMap().keySet();
        String missingParameters = REQUIRED_CURRENCY_PARAMS.stream()
                .filter(param -> !requestParameters.contains(param))
                .collect(Collectors.joining(", "));

        if (!missingParameters.isEmpty()) {
            throw new InvalidAttributeException("Missing required parameters: %s".formatted(missingParameters));
        }
    }

    private static boolean isValidCurrencyName(String name) {
        return name.matches(CURRENCY_NAME_REGEX);
    }

    private static boolean isValidCurrencyCode(String code) {
        return code.matches(CURRENCY_CODE_REGEX);
    }

    private static boolean isValidCurrencySign(String sign) {
        return sign.matches(CURRENCY_SIGN_REGEX);
    }
}
