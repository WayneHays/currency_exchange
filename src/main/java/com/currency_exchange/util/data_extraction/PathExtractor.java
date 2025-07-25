package com.currency_exchange.util.data_extraction;

import com.currency_exchange.dto.currency.CurrencyPairDto;
import com.currency_exchange.exception.InvalidParameterException;
import com.currency_exchange.util.Mapper;
import com.currency_exchange.util.validator.CurrencyValidator;
import com.currency_exchange.util.validator.HttpRequestValidator;
import jakarta.servlet.http.HttpServletRequest;

import static com.currency_exchange.util.ValidationConstants.*;

public final class PathExtractor {

    private PathExtractor() {
    }

    public static String extractCurrencyCode(HttpServletRequest req) {
        String code = extractAndNormalizePath(req, MISSING_CURRENCY_CODE_MESSAGE);
        HttpRequestValidator.validateStringPattern(code, CODE_PATTERN, CODE_ERROR_MESSAGE);
        return code;
    }

    public static CurrencyPairDto extractCurrencyPair(HttpServletRequest req) {
        String pair = extractAndNormalizePath(req, MISSING_PAIR_MESSAGE);
        HttpRequestValidator.validateStringPattern(pair, PAIR_PATTERN, PAIR_ERROR_MESSAGE);

        String baseCurrencyCode = pair.substring(0, 3);
        String targetCurrencyCode = pair.substring(3);

        CurrencyValidator.validateCurrenciesAreDifferent(baseCurrencyCode, targetCurrencyCode);

        return Mapper.toCurrencyCodesDto(baseCurrencyCode, targetCurrencyCode);
    }

    private static String extractAndNormalizePath(HttpServletRequest req, String errorMessage) {
        String path = req.getPathInfo();
        if (path == null || "/".equals(path)) {
            throw new InvalidParameterException(errorMessage);
        }
        return path.substring(1).trim().replaceAll("\\s+", "").toUpperCase();
    }
}
