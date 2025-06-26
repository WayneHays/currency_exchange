package com.currency_exchange.service;

import com.currency_exchange.dao.CurrencyDao;
import com.currency_exchange.dto.request.CurrencyDtoRequest;
import com.currency_exchange.dto.response.CurrencyDtoResponse;
import com.currency_exchange.entity.Currency;
import com.currency_exchange.exception.DaoException;
import com.currency_exchange.exception.service_exception.CurrencyNotFoundException;
import com.currency_exchange.exception.service_exception.InvalidCurrencyException;
import com.currency_exchange.exception.service_exception.ServiceException;

import java.util.List;

public class CurrencyService {
    private static final CurrencyService INSTANCE = new CurrencyService();
    private final CurrencyDao currencyDao = CurrencyDao.getInstance();

    private CurrencyService() {
    }

    public static CurrencyService getInstance() {
        return INSTANCE;
    }

    public List<CurrencyDtoResponse> findAll() {
        try {
            List<Currency> currencies = currencyDao.findAll();
            return currencies.stream()
                    .map(this::mapToDto)
                    .toList();
        } catch (DaoException e) {
            throw new ServiceException("Currencies list unavailable");
        }
    }

    public CurrencyDtoResponse findByCode(String code) {
        if (code == null || code.isBlank()) {
            throw new InvalidCurrencyException();
        }
        try {
            return currencyDao.findByCode(code)
                    .map(this::mapToDto)
                    .orElseThrow(() -> new CurrencyNotFoundException(code));
        } catch (DaoException e) {
            throw new ServiceException("Currency service unavailable", e);
        }
    }

//    public CurrencyDtoResponse save(CurrencyDtoRequest dtoRequest) {
//        if (dtoRequest == null) {
//            throw new ServiceException();
//        }
//
//        validateCurrencyDto(dtoRequest);
//
//        try {
//            Currency currency = mapToCurrency(dtoRequest);
//            Currency savedCurrency = currencyDao.save(currency);
//            return mapToDto(savedCurrency);
//        } catch (DatabaseException e) {
//            throw new ServiceException("Failed to save currency");
//        }
//
//    }

    private CurrencyDtoResponse mapToDto(Currency currency) {
        return new CurrencyDtoResponse(currency.getId(),
                currency.getCode(),
                currency.getFullName(),
                currency.getSign());
    }

    private Currency mapToCurrency(CurrencyDtoRequest dtoRequest) {
        Currency currency = new Currency();
        currency.setCode(dtoRequest.getCode());
        currency.setSign(dtoRequest.getSign());
        currency.setFullName(dtoRequest.getFullName());
        return currency;
    }

    private void validateCurrencyDto(CurrencyDtoRequest request) {
        if (request.getCode() == null || request.getCode().isBlank()) {
            throw new IllegalArgumentException("Currency code is required");
        }

        if (request.getFullName() == null || request.getFullName().isBlank()) {
            throw new IllegalArgumentException("Currency name is required");
        }

        if (request.getSign() == null || request.getSign().isBlank()) {
            throw new IllegalArgumentException("Currency sign required");
        }

        if (!request.getCode().matches("[A-Z]{3}")) {
            throw new IllegalArgumentException("Currency code must be 3 uppercase letters");
        }
    }
}
