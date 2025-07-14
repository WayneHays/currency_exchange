package com.currency_exchange.util.data_extraction;

import com.currency_exchange.CalculationParam;
import com.currency_exchange.CurrencyPairParam;
import com.currency_exchange.CurrencyParam;
import com.currency_exchange.ExchangeRateParam;
import com.currency_exchange.dto.currency.CurrencyCodesDto;
import com.currency_exchange.dto.currency.CurrencyCreateDto;
import com.currency_exchange.dto.exchange_calculation.CalculationRequestDto;
import com.currency_exchange.dto.exchange_rate.ExchangeRateCreateRequest;
import com.currency_exchange.dto.exchange_rate.ExchangeRateUpdateDto;
import com.currency_exchange.exception.service_exception.InvalidParameterException;
import com.currency_exchange.util.Mapper;
import com.currency_exchange.util.ValidationConstants;
import com.currency_exchange.util.Validator;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Set;

public final class DataExtractor {
    private DataExtractor() {
    }

    public static String extractCurrencyCode(String path) {
        Validator.validateNotEmpty(path, ValidationConstants.MISSING_CURRENCY_CODE);
        String code = path.substring(1).toUpperCase();
        Validator.validateParamRegex(code, CurrencyParam.CODE);

        return code;
    }

    public static CurrencyCodesDto extractCurrencyPair(String path) {
        Validator.validateParamRegex(path, CurrencyPairParam.PAIR);

        String baseCurrencyCode = path.substring(1, 4).toUpperCase();
        String targetCurrencyCode = path.substring(4, 7).toUpperCase();

        return Mapper.toCurrencyCodesRequest(baseCurrencyCode, targetCurrencyCode);
    }

    public static ExchangeRateCreateRequest extractExchangeRateCreateRequest(HttpServletRequest req) {
        Set<String> requiredNames = ExchangeRateParam.getAllNames();

        checkMissingParameters(req, requiredNames);

        String baseCurrencyCode = req.getParameter(ExchangeRateParam.BASE.getParamName());
        String targetCurrencyCode = req.getParameter(ExchangeRateParam.TARGET.getParamName());
        String rate = req.getParameter(ExchangeRateParam.RATE.getParamName());

        Validator.validateExchangeRateCreateRequest(baseCurrencyCode, targetCurrencyCode, targetCurrencyCode);

        return Mapper.toExchangeRateCreateRequest(baseCurrencyCode, targetCurrencyCode, rate);
    }

    public static CurrencyCreateDto extractCurrenciesCreateDto(HttpServletRequest req) {
        Set<String> requiredNames = CurrencyParam.getAllNames();

        checkMissingParameters(req, requiredNames);

        String name = req.getParameter(CurrencyParam.NAME.getParamName());
        String code = req.getParameter(CurrencyParam.CODE.getParamName()).toUpperCase();
        String sign = req.getParameter(CurrencyParam.SIGN.getParamName());
        name = DataFormatter.capitalizeRequiredLetters(name);

        Validator.validateCurrencyCreateDto(code, name, sign);

        return new CurrencyCreateDto(name, code, sign);
    }

    public static CalculationRequestDto extractCalculationRequestDto(HttpServletRequest req) {
        Set<String> requiredNames = CalculationParam.getAllNames();

        checkMissingParameters(req, requiredNames);

        String from = req.getParameter(CalculationParam.FROM.getParamName());
        String to = req.getParameter(CalculationParam.TO.getParamName());
        String amount = req.getParameter(CalculationParam.AMOUNT.getParamName());

        Validator.validateExchangeCalculationRequest(from, to, amount);

        return Mapper.toCalculationRequestDto(from, to, amount);
    }

    public static ExchangeRateUpdateDto extractExchangeRateUpdateRequest(HttpServletRequest req) {
        String rate = req.getParameter(ExchangeRateParam.RATE.getParamName());

        if (rate == null) {
            throw new InvalidParameterException(ValidationConstants.MISSING_RATE_MESSAGE);
        }
        Validator.validateParamRegex(rate, ExchangeRateParam.RATE);

        return Mapper.toExchangeRateUpdateDto(rate);
    }

    private static void checkMissingParameters(HttpServletRequest req, Set<String> requiredNames) {
        List<String> missingParameters = requiredNames.stream()
                .filter(name -> req.getParameter(name) == null)
                .toList();

        if (!missingParameters.isEmpty()) {
            throw new InvalidParameterException("Missing parameters: %s".formatted(String.join(", ", missingParameters)));
        }
    }
}
