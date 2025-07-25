package com.currency_exchange.util.data_extraction;

import com.currency_exchange.dto.calculation.CalculationRequestDto;
import com.currency_exchange.dto.currency.CurrencyRequestDto;
import com.currency_exchange.dto.exchange_rate.ExchangeRateRequestDto;
import com.currency_exchange.exception.InvalidParameterException;
import com.currency_exchange.util.Mapper;
import com.currency_exchange.util.validator.CalculationValidator;
import com.currency_exchange.util.validator.CurrencyValidator;
import com.currency_exchange.util.validator.ExchangeRateValidator;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

import static com.currency_exchange.util.ValidationConstants.*;

public final class ParameterExtractor {

    private ParameterExtractor() {
    }

    public static CurrencyRequestDto extractCurrencyRequest(HttpServletRequest req) {
        Map<String, String[]> params = req.getParameterMap();

        CurrencyValidator.validateCreateRequest(params);

        String code = extractAndNormalize(req, CODE);
        String name = extractAndTrim(req, NAME);
        String sign = extractAndRemoveSpaces(req, SIGN);

        CurrencyValidator.validateFields(code, name, sign);

        name = StringFormatter.capitalizeRequiredLetters(name);

        return Mapper.toCurrencyRequestDto(name, code, sign);
    }

    public static ExchangeRateRequestDto extractExchangeRateRequest(HttpServletRequest req) {
        Map<String, String[]> params = req.getParameterMap();
        ExchangeRateValidator.validateCreateRequest(params);

        String baseCurrencyCode = extractAndNormalize(req, BASE_CURRENCY_CODE);
        String targetCurrencyCode = extractAndNormalize(req, TARGET_CURRENCY_CODE);
        String rate = extractAndRemoveSpaces(req, RATE);

        ExchangeRateValidator.validateFields(baseCurrencyCode, targetCurrencyCode, rate);

        return Mapper.toExchangeRateRequestDto(baseCurrencyCode, targetCurrencyCode, rate);
    }

    public static CalculationRequestDto extractCalculationRequest(HttpServletRequest req) {
        Map<String, String[]> params = req.getParameterMap();
        CalculationValidator.validateRequest(params);

        String from = extractAndNormalize(req, FROM);
        String to = extractAndNormalize(req, TO);
        String amount = extractAndRemoveSpaces(req, AMOUNT);

        CalculationValidator.validateFields(from, to, amount);

        return Mapper.toCalculationRequestDto(from, to, amount);
    }

    public static BigDecimal extractRate(HttpServletRequest req) {
        String rate;

        if ("PATCH".equals(req.getMethod())) {
            rate = extractRateFromRequestBody(req);
        } else {
            Map<String, String[]> params = req.getParameterMap();
            ExchangeRateValidator.validateUpdateRequest(params);
            rate = extractFromParamsAndRemoveSpaces(params, RATE);
        }

        ExchangeRateValidator.validateRateField(rate);
        return new BigDecimal(rate);
    }

    private static String extractRateFromRequestBody(HttpServletRequest req) {
        try {
            StringBuilder buffer = new StringBuilder();
            String line;

            while ((line = req.getReader().readLine()) != null) {
                buffer.append(line);
            }
            String body = buffer.toString();

            String[] pairs = body.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=", 2);
                if (keyValue.length == 2 && RATE.equals(keyValue[0])) {
                    return keyValue[1].trim();
                }
            }
            throw new InvalidParameterException("Missing parameter: rate");

        } catch (IOException e) {
            throw new InvalidParameterException("Failed to read request body");
        }
    }

    private static String extractAndTrim(HttpServletRequest req, String paramName) {
        String value = req.getParameter(paramName);
        return value.trim();
    }

    private static String extractAndNormalize(HttpServletRequest req, String paramName) {
        return extractAndRemoveSpaces(req, paramName).toUpperCase();
    }

    private static String extractAndRemoveSpaces(HttpServletRequest req, String paramName) {
        String value = req.getParameter(paramName);
        return value.trim().replaceAll("\\s+", "");
    }

    private static String extractFromParamsAndRemoveSpaces(Map<String, String[]> params, String paramName) {
        return params.get(paramName)[0].trim().replaceAll("\\s+", "");
    }
}

