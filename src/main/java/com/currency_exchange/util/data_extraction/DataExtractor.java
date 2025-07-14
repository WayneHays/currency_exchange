package com.currency_exchange.util.data_extraction;

import com.currency_exchange.CalculationParam;
import com.currency_exchange.CurrencyParam;
import com.currency_exchange.ExchangeRateParam;
import com.currency_exchange.dto.currency.CurrencyCodesRequest;
import com.currency_exchange.dto.currency.CurrencyCreateRequest;
import com.currency_exchange.dto.exchange_calculation.ExchangeCalculationRequest;
import com.currency_exchange.dto.exchange_rate.ExchangeRateCreateRequest;
import com.currency_exchange.dto.exchange_rate.ExchangeRateUpdateRequest;
import com.currency_exchange.exception.service_exception.InvalidParameterException;
import com.currency_exchange.util.Mapper;
import com.currency_exchange.util.ValidationConstants;
import jakarta.servlet.http.HttpServletRequest;

public final class DataExtractor {
    private static final String MISSING_CURRENCY_PAIR_CODE = "Missing currency pair code";
    private static final String MISSING_CURRENCY_CODE = "Missing currency code";
    private static final String WRONG_CURRENCY_PAIR_PATH = "Path must be 6 latin letters like /USDRUB";

    private DataExtractor() {
    }

    public static String extractCurrencyCode(HttpServletRequest req) {
        String pathInfo = req.getPathInfo();
        validatePath(pathInfo, MISSING_CURRENCY_CODE);
        return pathInfo.substring(1).toUpperCase();
    }

    public static CurrencyCodesRequest extractCurrencyPairData(HttpServletRequest req) {
        String pathInfo = req.getPathInfo();
        validatePath(pathInfo, MISSING_CURRENCY_PAIR_CODE);

        if (!ValidationConstants.CURRENCY_PAIR_PATTERN.matches(pathInfo)) {
            throw new InvalidParameterException(WRONG_CURRENCY_PAIR_PATH);
        }

        String baseCurrencyCode = pathInfo.substring(1, 4).toUpperCase();
        String targetCurrencyCode = pathInfo.substring(4, 7).toUpperCase();

        return new CurrencyCodesRequest(baseCurrencyCode, targetCurrencyCode);
    }

    public static ExchangeRateCreateRequest extractExchangeRatePostData(HttpServletRequest req) {
        String baseCurrencyCode = getRequiredParameter(req, ExchangeRateParam.BASE.getParamName());
        String targetCurrencyCode = getRequiredParameter(req, ExchangeRateParam.TARGET.getParamName());
        String rate = getRequiredParameter(req, ExchangeRateParam.RATE.getParamName());

        return Mapper.toExchangeRateCreateRequest(baseCurrencyCode, targetCurrencyCode, rate);
    }

    public static ExchangeRateUpdateRequest extractPatchData(HttpServletRequest req) {
        String rate = getRequiredParameter(req, ExchangeRateParam.RATE.getParamName());
        return Mapper.toExchangeRateUpdateRequest(rate);
    }

    public static CurrencyCreateRequest extractCurrenciesPostData(HttpServletRequest req) {
        String name = getRequiredParameter(req, CurrencyParam.NAME.getParamName());
        String code = getRequiredParameter(req, CurrencyParam.CODE.getParamName()).toUpperCase();
        String sign = getRequiredParameter(req, CurrencyParam.SIGN.getParamName());
        name = DataFormatter.capitalizeRequiredLetters(name);

        return new CurrencyCreateRequest(name, code, sign);
    }

    public static ExchangeCalculationRequest extractCalculationData(HttpServletRequest req) {
        String from = getRequiredParameter(req, CalculationParam.FROM.getParamName());
        String to = getRequiredParameter(req, CalculationParam.TO.getParamName());
        String amount = getRequiredParameter(req, CalculationParam.AMOUNT.getParamName());

        return Mapper.toExchangeCalculationRequest(from, to, amount);
    }

    private static String getRequiredParameter(HttpServletRequest req, String paramName) {
        String value = req.getParameter(paramName);

        if (value == null || value.trim().isEmpty()) {
            throw new InvalidParameterException("Missing required parameter: %s".formatted(paramName));
        }
        return value.trim();
    }

    private static void validatePath(String pathInfo, String errorMessage) {
        if (pathInfo == null || "/".equals(pathInfo)) {
            throw new InvalidParameterException(errorMessage);
        }
    }
}
