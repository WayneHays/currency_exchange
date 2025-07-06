package com.currency_exchange.util;

import com.currency_exchange.CurrencyExchangeRequest;
import com.currency_exchange.CurrencyRequest;
import com.currency_exchange.ExchangeRateRequest;
import com.currency_exchange.dto.currency.CurrencyCreateRequest;
import com.currency_exchange.dto.currency.CurrencyPairDto;
import com.currency_exchange.dto.currency_exchange.ExchangeCalculationRequest;
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

    public static CurrencyPairDto extractValidCurrencyPairData(HttpServletRequest req) {
        String path = req.getPathInfo();
        ValidationUtils.validatePath(path, MISSING_CURRENCY_PAIR_CODE);
        ValidationUtils.validateCurrencyPair(path);
        String baseCurrencyCode = path.substring(1, 4).toUpperCase();
        String targetCurrencyCode = path.substring(4, 7).toUpperCase();

        return new CurrencyPairDto(baseCurrencyCode, targetCurrencyCode);
    }

    public static ExchangeRateCreateRequest extractValidExchangeRatePostData(HttpServletRequest req) {
        ValidationUtils.validateExchangeRatePostRequest(req);
        String baseCurrencyCode = req.getParameter(ExchangeRateRequest.BASE_CURRENCY_CODE.getParamName()).toUpperCase();
        String targetCurrencyCode = req.getParameter(ExchangeRateRequest.TARGET_CURRENCY_CODE.getParamName()).toUpperCase();
        String rate = req.getParameter(ExchangeRateRequest.RATE.getParamName());
        return Mapper.mapToExchangeRateCreateRequest(baseCurrencyCode, targetCurrencyCode, rate);

    }

    public static ExchangeRateUpdateRequest extractValidPatchData(HttpServletRequest req) {
        ValidationUtils.validateRequiredPatchParameters(req);
        String rate = req.getParameter(ExchangeRateRequest.RATE.getParamName());
        ValidationUtils.validateRate(rate);
        return Mapper.mapToExchangeRateUpdateRequest(rate);
    }

    public static CurrencyCreateRequest extractCurrencyData(HttpServletRequest req) {
        ValidationUtils.validateCurrenciesPostRequest(req);
        String name = req.getParameter(CurrencyRequest.NAME.getParamName()).toUpperCase();
        String code = req.getParameter(CurrencyRequest.CODE.getParamName()).toUpperCase();
        String sign = req.getParameter(CurrencyRequest.SIGN.getParamName());
        return new CurrencyCreateRequest(name, code, sign);
    }

    public static ExchangeCalculationRequest extractValidExchangeData(HttpServletRequest req) {
        ValidationUtils.validateCurrencyExchangeRequest(req);
        String from = req.getParameter(CurrencyExchangeRequest.FROM.getParamName());
        String to = req.getParameter(CurrencyExchangeRequest.TO.getParamName());
        String amount = req.getParameter(CurrencyExchangeRequest.AMOUNT.getParamName());
        return Mapper.mapToExchangeCalculationRequest(from, to, amount);
    }
}
