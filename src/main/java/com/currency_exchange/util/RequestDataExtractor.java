package com.currency_exchange.util;

import com.currency_exchange.CurrencyRequest;
import com.currency_exchange.ExchangeRateRequest;
import jakarta.servlet.http.HttpServletRequest;

public final class RequestDataExtractor {
    public static final String MISSING_CURRENCY_CODE = "Missing currency code";
    public static final String MISSING_CURRENCY_PAIR_CODE = "Missing currency pair code";

    private RequestDataExtractor() {
    }

    public static String extractCurrencyCode(HttpServletRequest req) {
        String path = req.getPathInfo();
        ValidationUtils.validatePath(path, MISSING_CURRENCY_CODE);
        String code = path.substring(1).toUpperCase();
        ValidationUtils.validateCurrencyCode(code);
        return code;
    }

    public static String[] extractCurrencyPairCodes(HttpServletRequest req) {
        String path = req.getPathInfo();
        ValidationUtils.validatePath(path, MISSING_CURRENCY_PAIR_CODE);
        ValidationUtils.validateCurrencyPair(path);

        return new String[]{path.substring(1, 4).toUpperCase(), path.substring(4, 7).toUpperCase()};
    }

    public static String[] extractExchangeRatePostData(HttpServletRequest req) {
        ValidationUtils.validateExchangeRatePostRequest(req);
        String baseCurrencyCode = req.getParameter(ExchangeRateRequest.BASE_CURRENCY_CODE.getParamName()).toUpperCase();
        String targetCurrencyCode = req.getParameter(ExchangeRateRequest.TARGET_CURRENCY_CODE.getParamName()).toUpperCase();
        String rate = req.getParameter(ExchangeRateRequest.RATE.getParamName());
        return new String[]{baseCurrencyCode, targetCurrencyCode, rate};
    }

    public static String[] extractExchangeRatePatchData(HttpServletRequest req) {
        String[] currencyCodes = extractCurrencyPairCodes(req);
        ValidationUtils.validateRequiredPatchParameters(req);
        String rate = req.getParameter(ExchangeRateRequest.RATE.getParamName());
        ValidationUtils.validateRate(rate);
        return new String[]{currencyCodes[0], currencyCodes[1], rate};
    }

    public static String[] extractCurrencyData(HttpServletRequest req) {
        ValidationUtils.validateCurrenciesPostRequest(req);
        String name = req.getParameter(CurrencyRequest.NAME.getParamName()).toUpperCase();
        String code = req.getParameter(CurrencyRequest.CODE.getParamName()).toUpperCase();
        String sign = req.getParameter(CurrencyRequest.SIGN.getParamName());
        return new String[]{name, code, sign};
    }
}
