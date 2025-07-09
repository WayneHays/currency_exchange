package com.currency_exchange.util;

import com.currency_exchange.CurrencyRequiredParams;
import com.currency_exchange.ExchangeCalculationRequiredParams;
import com.currency_exchange.ExchangeRateRequiredParams;
import com.currency_exchange.dto.currency.CurrencyCreateRequest;
import com.currency_exchange.dto.currency.CurrencyPairRequest;
import com.currency_exchange.dto.exchange_calculation.ExchangeCalculationRequest;
import com.currency_exchange.dto.exchange_rate.ExchangeRateCreateRequest;
import com.currency_exchange.dto.exchange_rate.ExchangeRateUpdateRequest;
import jakarta.servlet.http.HttpServletRequest;

public final class RequestDataExtractor {
    public static final String MISSING_CURRENCY_CODE = "Missing currency code";
    public static final String MISSING_CURRENCY_PAIR_CODE = "Missing currency pair code";

    private RequestDataExtractor() {
    }

    public static String extractValidCurrencyCode(HttpServletRequest req) {
        String path = req.getPathInfo();
        ValidationUtils.validatePath(path, MISSING_CURRENCY_CODE);
        String code = path.substring(1).toUpperCase();
        ValidationUtils.validateCurrencyCode(code);
        return code;
    }

    public static CurrencyPairRequest extractValidCurrencyPairData(HttpServletRequest req) {
        String path = req.getPathInfo();
        ValidationUtils.validatePath(path, MISSING_CURRENCY_PAIR_CODE);
        ValidationUtils.validateCurrencyPair(path);
        String baseCurrencyCode = path.substring(1, 4).toUpperCase();
        String targetCurrencyCode = path.substring(4, 7).toUpperCase();
        return new CurrencyPairRequest(baseCurrencyCode, targetCurrencyCode);
    }

    public static ExchangeRateCreateRequest extractValidExchangeRateData(HttpServletRequest req) {
        ValidationUtils.validateExchangeRatePostRequest(req);
        String baseCurrencyCode = req.getParameter(ExchangeRateRequiredParams.BASE_CURRENCY_CODE.getParamName()).toUpperCase();
        String targetCurrencyCode = req.getParameter(ExchangeRateRequiredParams.TARGET_CURRENCY_CODE.getParamName()).toUpperCase();
        String rate = req.getParameter(ExchangeRateRequiredParams.RATE.getParamName());
        return Mapper.mapToExchangeRateCreateRequest(baseCurrencyCode, targetCurrencyCode, rate);

    }

    public static ExchangeRateUpdateRequest extractValidPatchData(HttpServletRequest req) {
        ValidationUtils.validateRequiredPatchParameters(req);
        String rate = req.getParameter(ExchangeRateRequiredParams.RATE.getParamName());
        ValidationUtils.validateRate(rate);
        return Mapper.mapToExchangeRateUpdateRequest(rate);
    }

    public static CurrencyCreateRequest extractValidCurrencyData(HttpServletRequest req) {
        ValidationUtils.validateCurrenciesPostRequest(req);
        String name = req.getParameter(CurrencyRequiredParams.NAME.getParamName()).toUpperCase();
        String code = req.getParameter(CurrencyRequiredParams.CODE.getParamName()).toUpperCase();
        String sign = req.getParameter(CurrencyRequiredParams.SIGN.getParamName());
        return new CurrencyCreateRequest(name, code, sign);
    }

    public static ExchangeCalculationRequest extractValidCalculationData(HttpServletRequest req) {
        ValidationUtils.validateCalculationRequest(req);
        String from = req.getParameter(ExchangeCalculationRequiredParams.FROM.getParamName());
        String to = req.getParameter(ExchangeCalculationRequiredParams.TO.getParamName());
        String amount = req.getParameter(ExchangeCalculationRequiredParams.AMOUNT.getParamName());
        return Mapper.mapToExchangeCalculationRequest(from, to, amount);
    }
}
