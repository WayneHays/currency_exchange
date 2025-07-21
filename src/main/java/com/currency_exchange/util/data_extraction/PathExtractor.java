package com.currency_exchange.util.data_extraction;

import com.currency_exchange.dto.currency.CurrencyPairDto;
import com.currency_exchange.util.Mapper;
import com.currency_exchange.util.Validator;
import jakarta.servlet.http.HttpServletRequest;

public final class PathExtractor {
    private static final String CURRENCY_CODE_PATTERN = "[A-Za-z]{3}";
    private static final String CURRENCY_PAIR_PATTERN = "%s%s".formatted(CURRENCY_CODE_PATTERN, CURRENCY_CODE_PATTERN);
    private static final String CURRENCY_CODE_ERROR_MESSAGE = "Currency code must be 3 latin letters";
    private static final String CURRENCY_PAIR_ERROR_MESSAGE = "Path must be 6 latin letters like USDRUB";
    private static final String MISSING_CURRENCY_CODE_MESSAGE = "Missing currency code";

    private PathExtractor() {

    }

    public static String extractCurrencyCode(HttpServletRequest req) {
        String path = req.getPathInfo();
        Validator.validateNotEmpty(path, MISSING_CURRENCY_CODE_MESSAGE);
        String code = path.substring(1).toUpperCase();
        Validator.validateParamRegex(code, CURRENCY_CODE_PATTERN, CURRENCY_CODE_ERROR_MESSAGE);
        return code;
    }

    public static CurrencyPairDto extractCurrencyPair(HttpServletRequest req) {
        String path = req.getPathInfo();
        Validator.validateParamRegex(
                path,
                CURRENCY_PAIR_PATTERN,
                CURRENCY_PAIR_ERROR_MESSAGE
        );
        String baseCurrencyCode = path.substring(0, 3).toUpperCase();
        String targetCurrencyCode = path.substring(3).toUpperCase();
        Validator.validateCurrenciesAreDifferent(baseCurrencyCode, targetCurrencyCode);

        return Mapper.toCurrencyCodesDto(baseCurrencyCode, targetCurrencyCode);
    }
}
