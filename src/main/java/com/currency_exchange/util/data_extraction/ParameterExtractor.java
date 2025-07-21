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

        String code = req.getParameter(CODE.trim()).toUpperCase();
        String name = req.getParameter(NAME.trim());
        String sign = req.getParameter(SIGN.trim());

        name = StringFormatter.capitalizeRequiredLetters(name);

        return Mapper.toCurrencyRequestDto(name, code, sign);
    }

    public static ExchangeRateRequestDto extractExchangeRateRequest(HttpServletRequest req) {
        Map<String, String[]> params = req.getParameterMap();
        ExchangeRateValidator.validateCreateRequest(params);

        String baseCurrencyCode = req.getParameter(BASE_CURRENCY_CODE).trim().toUpperCase();
        String targetCurrencyCode = req.getParameter(TARGET_CURRENCY_CODE).trim().toUpperCase();
        String rate = req.getParameter(RATE);

        return Mapper.toExchangeRateRequestDto(baseCurrencyCode, targetCurrencyCode, rate);
    }

    public static CalculationRequestDto extractCalculationRequest(HttpServletRequest req) {
        Map<String, String[]> params = req.getParameterMap();
        CalculationValidator.validateCalculationRequest(params);

        String from = req.getParameter(FROM).trim().toUpperCase();
        String to = req.getParameter(TO).trim().toUpperCase();
        String amount = req.getParameter(AMOUNT).trim();

        return Mapper.toCalculationRequestDto(from, to, amount);
    }

    public static BigDecimal extractPatchDto(HttpServletRequest req) {
        Map<String, String[]> params = req.getParameterMap();
        ExchangeRateValidator.validateUpdateRequest(params);

        String amount = params.get(AMOUNT)[0].trim();
        return new BigDecimal(amount);
    }
}

