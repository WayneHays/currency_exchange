package com.currency_exchange.util.data_extraction;

import com.currency_exchange.dto.currency.CurrencyPairDto;
import com.currency_exchange.exception.InvalidParameterException;
import com.currency_exchange.util.Mapper;
import com.currency_exchange.util.validator.RequestValidator;
import jakarta.servlet.http.HttpServletRequest;

import static com.currency_exchange.util.ValidationConstants.*;

public final class PathExtractor {

    private PathExtractor() {

    }

    public static String extractCurrencyCode(HttpServletRequest req) {
        String path = req.getPathInfo();

        if (path == null || "/".equals(path)) {
            throw new InvalidParameterException(MISSING_CURRENCY_CODE_MESSAGE);
        }
        String code = path.substring(1).toUpperCase();

        RequestValidator.validateStringPattern(code, CODE_PATTERN, CODE_ERROR_MESSAGE);
        return code;
    }

    public static CurrencyPairDto extractCurrencyPair(HttpServletRequest req) {
        String path = req.getPathInfo();

        if (path == null || "/".equals(path)) {
            throw new InvalidParameterException(MISSING_PAIR_MESSAGE);
        }

        String pair = path.substring(1);

        RequestValidator.validateStringPattern(pair, PAIR_PATTERN, PAIR_ERROR_MESSAGE);

        String baseCurrencyCode = pair.substring(0, 3).toUpperCase();
        String targetCurrencyCode = pair.substring(3).toUpperCase();
        RequestValidator.validateCurrenciesAreDifferent(baseCurrencyCode, targetCurrencyCode);

        return Mapper.toCurrencyCodesDto(baseCurrencyCode, targetCurrencyCode);
    }
}
