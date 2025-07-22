package com.currency_exchange.util.data_extraction;

import com.currency_exchange.dto.calculation.CalculationRequestDto;
import com.currency_exchange.dto.currency.CurrencyRequestDto;
import com.currency_exchange.dto.exchange_rate.ExchangeRateRequestDto;
import com.currency_exchange.util.Mapper;
import com.currency_exchange.util.validator.CalculationValidator;
import com.currency_exchange.util.validator.CurrencyValidator;
import com.currency_exchange.util.validator.ExchangeRateValidator;
import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;
import java.util.Map;

import static com.currency_exchange.util.ValidationConstants.*;

public final class ParameterExtractor {
    private ParameterExtractor() {
    }

    public static CurrencyRequestDto extractCurrencyRequest(HttpServletRequest req) {
        Map<String, String[]> params = req.getParameterMap();

        CurrencyValidator.validateCreateRequest(params);

        String code = extractAndNormalizeCode(req, CODE);
        String name = extractAndRemoveSpaces(req, NAME);
        String sign = extractAndRemoveSpaces(req, SIGN);

        CurrencyValidator.validateFields(code, name, sign);

        name = StringFormatter.capitalizeRequiredLetters(name);

        return Mapper.toCurrencyRequestDto(name, code, sign);
    }

    public static ExchangeRateRequestDto extractExchangeRateRequest(HttpServletRequest req) {
        Map<String, String[]> params = req.getParameterMap();
        ExchangeRateValidator.validateCreateRequest(params);

        String baseCurrencyCode = extractAndNormalizeCode(req, BASE_CURRENCY_CODE);
        String targetCurrencyCode = extractAndNormalizeCode(req, TARGET_CURRENCY_CODE);
        String rate = extractAndRemoveSpaces(req, RATE);

        ExchangeRateValidator.validateFields(baseCurrencyCode, targetCurrencyCode, rate);

        return Mapper.toExchangeRateRequestDto(baseCurrencyCode, targetCurrencyCode, rate);
    }

    public static CalculationRequestDto extractCalculationRequest(HttpServletRequest req) {
        Map<String, String[]> params = req.getParameterMap();
        CalculationValidator.validateRequest(params);

        String from = extractAndNormalizeCode(req, FROM);
        String to = extractAndNormalizeCode(req, TO);
        String amount = extractAndRemoveSpaces(req, AMOUNT);

        CalculationValidator.validateFields(from, to, amount);

        return Mapper.toCalculationRequestDto(from, to, amount);
    }

    public static BigDecimal extractRate(HttpServletRequest req) {
        Map<String, String[]> params = req.getParameterMap();
        ExchangeRateValidator.validateUpdateRequest(params);

        String rate = extractFromParamsAndRemoveSpaces(params, RATE);

        ExchangeRateValidator.validateRateField(rate);

        return new BigDecimal(rate);
    }

    private static String extractAndNormalizeCode(HttpServletRequest req, String paramName) {
        return extractAndRemoveSpaces(req, paramName).toUpperCase();
    }

    private static String extractAndRemoveSpaces(HttpServletRequest req, String paramName) {
        String value = req.getParameter(paramName);
        return value != null ? value.trim().replaceAll("\\s+", "") : "";
    }

    private static String extractFromParamsAndRemoveSpaces(Map<String, String[]> params, String paramName) {
        return params.get(paramName)[0].trim().replaceAll("\\s+", "");
    }
}

