package com.currency_exchange.util;

import com.currency_exchange.CurrencyParam;
import com.currency_exchange.ExchangeCalculationParam;
import com.currency_exchange.ExchangeRateParam;
import com.currency_exchange.dto.currency.CurrencyCodesRequest;
import com.currency_exchange.dto.currency.CurrencyCreateRequest;
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
        CurrencyValidator.validateCurrencyCode(code);
        return code;
    }

    public static CurrencyCodesRequest extractValidCurrencyPairData(HttpServletRequest req) {
        String path = req.getPathInfo();
        ValidationUtils.validatePath(path, MISSING_CURRENCY_PAIR_CODE);
        ExchangeRateValidator.validateCurrencyPair(path);
        String baseCurrencyCode = path.substring(1, 4).toUpperCase();
        String targetCurrencyCode = path.substring(4, 7).toUpperCase();
        return new CurrencyCodesRequest(baseCurrencyCode, targetCurrencyCode);
    }

    public static ExchangeRateCreateRequest extractValidPostData(HttpServletRequest req) {
        ExchangeRateValidator.validateExchangeRatePostRequest(req);
        String baseName = ExchangeRateParam.BASE_CURRENCY_CODE.getParamName();
        String requiredTargetCurrencyParamName = ExchangeRateParam.TARGET_CURRENCY_CODE.getParamName();

        String baseCurrencyCode = req.getParameter(baseName).toUpperCase();
        String targetCurrencyCode = req.getParameter(requiredTargetCurrencyParamName).toUpperCase();
        String rate = req.getParameter(ExchangeRateParam.RATE.getParamName());

        return Mapper.toExchangeRateCreateRequest(baseCurrencyCode, targetCurrencyCode, rate);

    }

    public static ExchangeRateUpdateRequest extractValidPatchData(HttpServletRequest req) {
        ExchangeRateValidator.validateRequiredPatchParameters(req);
        String rate = req.getParameter(ExchangeRateParam.RATE.getParamName());
        ExchangeRateValidator.validateRate(rate);
        return Mapper.toExchangeRateUpdateRequest(rate);
    }

    public static CurrencyCreateRequest extractValidCurrencyData(HttpServletRequest req) {
        CurrencyValidator.validateCurrenciesPostRequest(req);
        String name = capitalizeRequiredLetters(req.getParameter(CurrencyParam.NAME.getParamName()));
        String code = req.getParameter(CurrencyParam.CODE.getParamName()).toUpperCase();
        String sign = req.getParameter(CurrencyParam.SIGN.getParamName());
        return new CurrencyCreateRequest(name, code, sign);
    }

    public static ExchangeCalculationRequest extractValidCalculationData(HttpServletRequest req) {
        ExchangeRateValidator.validateCalculationRequest(req);
        String from = req.getParameter(ExchangeCalculationParam.FROM.getParamName());
        String to = req.getParameter(ExchangeCalculationParam.TO.getParamName());
        String amount = req.getParameter(ExchangeCalculationParam.AMOUNT.getParamName());
        return Mapper.toExchangeCalculationRequest(from, to, amount);
    }


    public static String capitalizeRequiredLetters(String input) {
        if (input.contains(" ")) {
            return capitalizeFirstLetterOfEachPart(input);
        }
        return capitalizeFirstLetter(input);
    }

    private static String capitalizeFirstLetterOfEachPart(String input) {
        String[] parts = input.split(" ");
        String firstName = capitalizeFirstLetter(parts[0]);
        String secondName = capitalizeFirstLetter(parts[1]);
        return "%s %s".formatted(firstName, secondName);
    }

    private static String capitalizeFirstLetter(String input) {
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }
}
