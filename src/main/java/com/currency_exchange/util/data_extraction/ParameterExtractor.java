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

import static com.currency_exchange.util.constant.ParameterNames.*;

public final class ParameterExtractor {
    public static final String MISSING_PARAMETER_RATE = "Missing parameter: rate";
    public static final String FAILED_TO_READ_REQUEST_BODY = "Failed to read request body";
    public static final String PARAMETER_NOT_FOUND = "Parameter not found: ";

    private ParameterExtractor() {
    }

    public static CurrencyRequestDto extractCurrencyRequest(HttpServletRequest req) {
        Map<String, String[]> params = normalizeParameterMap(req.getParameterMap());
        CurrencyValidator.validateCreateRequest(params);

        String code = extractParam(params, CODE, true);
        String name = capitalizeWords(extractParam(params, NAME, false));
        String sign = extractParam(params, SIGN, false);

        CurrencyValidator.validateFields(code, name, sign);
        return Mapper.toCurrencyRequestDto(name, code, sign);
    }

    public static ExchangeRateRequestDto extractExchangeRateRequest(HttpServletRequest req) {
        Map<String, String[]> params = normalizeParameterMap(req.getParameterMap());
        ExchangeRateValidator.validateCreateRequest(params);

        String baseCurrencyCode = extractParam(params, BASE_CURRENCY_CODE, true);
        String targetCurrencyCode = extractParam(params, TARGET_CURRENCY_CODE, true);
        String rate = extractParam(params, RATE, true);

        ExchangeRateValidator.validateFields(baseCurrencyCode, targetCurrencyCode, rate);
        return Mapper.toExchangeRateRequestDto(baseCurrencyCode, targetCurrencyCode, rate);
    }

    public static CalculationRequestDto extractCalculationRequest(HttpServletRequest req) {
        Map<String, String[]> params = normalizeParameterMap(req.getParameterMap());
        CalculationValidator.validateRequest(params);

        String from = extractParam(params, FROM, true);
        String to = extractParam(params, TO, true);
        String amount = extractParam(params, AMOUNT, true);

        CalculationValidator.validateFields(from, to, amount);
        return Mapper.toCalculationRequestDto(from, to, amount);
    }

    public static BigDecimal extractRate(HttpServletRequest req) {
        String rate;

        if ("PATCH".equals(req.getMethod())) {
            rate = extractRateFromBody(req);
        } else {
            Map<String, String[]> params = normalizeParameterMap(req.getParameterMap());
            ExchangeRateValidator.validateUpdateRequest(params);
            rate = extractParam(params, RATE, true);
        }

        ExchangeRateValidator.validateRateField(rate);
        return new BigDecimal(rate);
    }

    private static Map<String, String[]> normalizeParameterMap(Map<String, String[]> originalParams) {
        return originalParams.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> normalizeParameterName(entry.getKey()),
                        entry -> normalizeParameterValues(entry.getValue())
                ));
    }

    private static String normalizeParameterName(String parameterName) {
        return parameterName.trim().replaceAll("\\s+", "");
    }

    private static String[] normalizeParameterValues(String[] values) {
        return java.util.Arrays.stream(values)
                .map(value -> value.trim().replaceAll("\\s+", " "))
                .toArray(String[]::new);
    }

    private static String extractParam(Map<String, String[]> params, String name, boolean normalize) {
        String value = params.entrySet().stream()
                .filter(entry -> entry.getKey().equals(name))
                .map(entry -> entry.getValue()[0])
                .findFirst()
                .orElse(null);

        if (value == null) {
            throw new InvalidParameterException(PARAMETER_NOT_FOUND + name);
        }

        if (normalize) {
            value = value.replaceAll("\\s+", "").toUpperCase();
        }

        return value;
    }

    private static String extractRateFromBody(HttpServletRequest req) {
        try (BufferedReader reader = req.getReader()) {
            String body = reader.lines().collect(Collectors.joining()).trim();

            String[] pairs = body.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=", 2);
                if (keyValue.length == 2) {
                    String decodedKey = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
                    decodedKey = normalizeParameterName(decodedKey);

                    if (RATE.equals(decodedKey)) {
                        String decodedValue = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
                        decodedValue = decodedValue.trim().replaceAll("\\s+", "").replace(",", ".");
                        return decodedValue;
                    }
                }
            }
            throw new InvalidParameterException(MISSING_PARAMETER_RATE);

        } catch (IOException e) {
            throw new InvalidParameterException(FAILED_TO_READ_REQUEST_BODY);
        }
    }

    private static String capitalizeWords(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        String[] words = input.split("\\s+");
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < words.length; i++) {
            if (i > 0) result.append(" ");
            if (!words[i].isEmpty()) {
                result.append(words[i].substring(0, 1).toUpperCase())
                        .append(words[i].substring(1).toLowerCase());
            }
        }
        return result.toString();
    }
}

