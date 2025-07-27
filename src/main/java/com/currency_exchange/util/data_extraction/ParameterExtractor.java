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

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

import static com.currency_exchange.util.ValidationConstants.*;

public final class ParameterExtractor {
    public static final String MISSING_PARAMETER_RATE = "Missing parameter: rate";
    public static final String FAILED_TO_READ_REQUEST_BODY = "Failed to read request body";

    private ParameterExtractor() {
    }

    public static CurrencyRequestDto extractCurrencyRequest(HttpServletRequest req) {
        Map<String, String[]> params = req.getParameterMap();
        CurrencyValidator.validateCreateRequest(params);

        String code = extractParam(req, CODE, true);
        String name = capitalizeWords(req.getParameter(NAME).trim());
        String sign = extractParam(req, SIGN, false);

        CurrencyValidator.validateFields(code, name, sign);
        return Mapper.toCurrencyRequestDto(name, code, sign);
    }

    public static ExchangeRateRequestDto extractExchangeRateRequest(HttpServletRequest req) {
        Map<String, String[]> params = req.getParameterMap();
        ExchangeRateValidator.validateCreateRequest(params);

        String baseCurrencyCode = extractParam(req, BASE_CURRENCY_CODE, true);
        String targetCurrencyCode = extractParam(req, TARGET_CURRENCY_CODE, true);
        String rate = extractParam(req, RATE, false);

        ExchangeRateValidator.validateFields(baseCurrencyCode, targetCurrencyCode, rate);
        return Mapper.toExchangeRateRequestDto(baseCurrencyCode, targetCurrencyCode, rate);
    }

    public static CalculationRequestDto extractCalculationRequest(HttpServletRequest req) {
        Map<String, String[]> params = req.getParameterMap();
        CalculationValidator.validateRequest(params);

        String from = extractParam(req, FROM, true);
        String to = extractParam(req, TO, true);
        String amount = extractParam(req, AMOUNT, false);

        CalculationValidator.validateFields(from, to, amount);
        return Mapper.toCalculationRequestDto(from, to, amount);
    }

    public static BigDecimal extractRate(HttpServletRequest req) {
        String rate = "PATCH".equals(req.getMethod())
                ? extractRateFromBody(req)
                : extractParam(req, RATE, false);

        ExchangeRateValidator.validateRateField(rate);
        return new BigDecimal(rate);
    }

    private static String extractParam(HttpServletRequest req, String name, boolean normalize) {
        String value = req.getParameter(name).trim().replaceAll("\\s+", "");
        return normalize ? value.toUpperCase() : value;
    }

    private static String extractRateFromBody(HttpServletRequest req) {
        try (BufferedReader reader = req.getReader()) {
            String body = reader.lines().collect(Collectors.joining()).trim().replaceAll(" ", "");

            String[] pairs = body.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=", 2);
                if (keyValue.length == 2 && RATE.equals(keyValue[0])) {
                    String decodedValue = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
                    return decodedValue.replace(",", ".");
                }
            }
            throw new InvalidParameterException(MISSING_PARAMETER_RATE);

        } catch (IOException e) {
            throw new InvalidParameterException(FAILED_TO_READ_REQUEST_BODY);
        }
    }

    private static String capitalizeWords(String input) {
        if (!input.contains(" ")) {
            return input.substring(0, 1).toUpperCase() + input.substring(1);
        }

        String[] words = input.split("\\s+");
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < words.length; i++) {
            if (i > 0) result.append(" ");
            if (!words[i].isEmpty()) {
                result.append(words[i].substring(0, 1).toUpperCase())
                        .append(words[i].substring(1));
            }
        }
        return result.toString();
    }
}

