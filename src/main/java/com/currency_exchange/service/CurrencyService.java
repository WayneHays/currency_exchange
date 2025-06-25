package com.currency_exchange.service;

import com.currency_exchange.dao.CurrencyDao;
import com.currency_exchange.dto.request.CurrencyDtoRequest;
import com.currency_exchange.dto.response.CurrencyDtoResponse;
import com.currency_exchange.entity.Currency;
import com.currency_exchange.exception.DaoException;
import com.currency_exchange.exception.ServiceException;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class CurrencyService {
    private static final CurrencyService INSTANCE = new CurrencyService();
    private final CurrencyDao currencyDao = CurrencyDao.getInstance();

    private CurrencyService() {
    }

    public static CurrencyService getInstance() {
        return INSTANCE;
    }

    public List<CurrencyDtoResponse> findAll() throws ServiceException {
        try {
            List<Currency> currencies = currencyDao.findAll();

            if (currencies.isEmpty()) {
                return Collections.emptyList();
            }

            return currencies.stream()
                    .map(this::mapToDto
                    ).toList();
        } catch (DaoException e) {
            throw new ServiceException("Database access error", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    public CurrencyDtoResponse findByCode(String code) {
        if (code == null) {
            throw new IllegalArgumentException("Currency code cannot be null");
        }

        try {
            Optional<Currency> maybeCurrency = currencyDao.findByCode(code);

            if (maybeCurrency.isEmpty()) {
                throw new ServiceException("Currency not found", HttpServletResponse.SC_NOT_FOUND);
            }

            Currency currency = maybeCurrency.get();
            return mapToDto(currency);
        } catch (DaoException e) {
            throw new ServiceException("Failed to fetch currency from database " + e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    public CurrencyDtoResponse save(CurrencyDtoRequest dtoRequest) {
        if (dtoRequest == null) {
            throw new IllegalArgumentException("CurrencyDTO cannot be null");
        }

        validateCurrencyDto(dtoRequest);

        try {
            Currency currency = mapToCurrency(dtoRequest);
            Currency savedCurrency = currencyDao.save(currency);
            return mapToDto(savedCurrency);
        } catch (DaoException e) {
            throw new ServiceException("Failed to save currency", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

    }

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

    private void validateCurrencyDto(CurrencyDtoRequest Request) {
        if (Request.getCode() == null || Request.getCode().isBlank()) {
            throw new IllegalArgumentException("Currency code is required");
        }

        if (Request.getFullName() == null || Request.getFullName().isBlank()) {
            throw new IllegalArgumentException("Currency name is required");
        }

        if (Request.getSign() == null || Request.getSign().isBlank()) {
            throw new IllegalArgumentException("Currency sign required");
        }

        if (!Request.getCode().matches("[A-Z]{3}")) {
            throw new IllegalArgumentException("Currency code must be 3 uppercase letters");
        }
    }
}
