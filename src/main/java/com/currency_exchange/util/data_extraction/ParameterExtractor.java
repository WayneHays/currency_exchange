package com.currency_exchange.util.data_extraction;

import com.currency_exchange.dto.calculation.CalculationRequestDto;
import com.currency_exchange.dto.currency.CurrencyRequestDto;
import com.currency_exchange.dto.exchange_rate.ExchangeRateRequestDto;
import com.currency_exchange.util.Mapper;
import com.currency_exchange.util.ValidationConstants;
import com.currency_exchange.util.Validator;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

public final class ParameterExtractor {

    private ParameterExtractor() {
    }

    public static CurrencyRequestDto extractCurrencyRequest(HttpServletRequest req) {
        Map<String, String[]> params = req.getParameterMap();
        Validator.validateCurrencyCreateRequest(params);

        String code = req.getParameter(ValidationConstants.CODE_PARAM).trim().toUpperCase();
        String name = req.getParameter(ValidationConstants.NAME_PARAM).trim();
        String sign = req.getParameter(ValidationConstants.SIGN_PARAM).trim();

        name = DataFormatter.capitalizeRequiredLetters(name);

        return Mapper.toCurrencyRequestDto(name, code, sign);
    }

    public static ExchangeRateRequestDto extractExchangeRateRequest(HttpServletRequest req) {
        Map<String, String[]> params = req.getParameterMap();
        Validator.validateExchangeRateCreateRequest(params);

        String baseCurrencyCode = req.getParameter(ValidationConstants.BASE_CURRENCY_CODE).trim().toUpperCase();
        String targetCurrencyCode = req.getParameter(ValidationConstants.TARGET_CURRENCY_CODE).trim().toUpperCase();
        String rate = req.getParameter(ValidationConstants.RATE);

        return Mapper.toExchangeRateCreateDto(baseCurrencyCode, targetCurrencyCode, rate);
    }

    public static CalculationRequestDto extractCalculationRequest(HttpServletRequest req) {
        Map<String, String[]> params = req.getParameterMap();
        Validator.validateCalculationRequest(params);

        String from = params.get(ValidationConstants.FROM)[0].trim().toUpperCase();
        String to = params.get(ValidationConstants.TO)[0].trim().toUpperCase();
        String amount = params.get(ValidationConstants.AMOUNT)[0].trim();

        return Mapper.toCalculationRequestDto(from, to, amount);
    }

    public static String extractPatchData(HttpServletRequest req) {
        Map<String, String[]> params = req.getParameterMap();
        Validator.validatePatchRequest(params);

        String amount = params.get(ValidationConstants.AMOUNT)[0].trim();
        return Mapper.toPatchDto();
    }
}

