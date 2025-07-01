package com.currency_exchange.util;

import com.currency_exchange.exception.service_exception.InvalidAttributeException;
import com.currency_exchange.util.validator.CurrencyValidator;
import jakarta.servlet.http.HttpServletRequest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class RequestParser {

    public static final String CURRENCY_PAIR_PATTERN = "^/([A-Za-z]{3})([A-Za-z]{3})$";
    public static final String WRONG_PATH_MESSAGE = "Path must be /XXXYYY";

    private RequestParser() {
    }

    public static String extractCurrencyCode(HttpServletRequest req) {
        String pathInfo = req.getPathInfo();
        CurrencyValidator.validatePathInfo(pathInfo);
        String code = pathInfo.substring(1);
        CurrencyValidator.validateCode(code);
        return code;
    }

    public static String[] extractCurrencyPairCodes(HttpServletRequest req) {
        String path = req.getPathInfo();
        Matcher matcher = Pattern.compile(CURRENCY_PAIR_PATTERN)
                .matcher(path != null ? path : "");

        if (!matcher.matches()) {
            throw new InvalidAttributeException(WRONG_PATH_MESSAGE);
        }

        return new String[]{matcher.group(1), matcher.group(2)};
    }

    public static String[] extractExchangeRateData(HttpServletRequest req) {
        String baseCurrencyCode = req.getParameter(ExchangeRateRequest.BASE_CURRENCY_CODE.getName());
        String targetCurrencyCode = req.getParameter(ExchangeRateRequest.TARGET_CURRENCY_CODE.getName());
        String rate = req.getParameter(ExchangeRateRequest.RATE.getName());
        return new String[]{baseCurrencyCode, targetCurrencyCode, rate};
    }
}
