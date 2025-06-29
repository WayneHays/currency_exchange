package com.currency_exchange.service;

import com.currency_exchange.dao.CurrencyDao;
import com.currency_exchange.dto.request.CurrencyDtoRequest;
import com.currency_exchange.dto.response.CurrencyDtoResponse;
import com.currency_exchange.entity.Currency;
import com.currency_exchange.exception.dao_exception.CurrencyAlreadyExistsException;
import com.currency_exchange.exception.dao_exception.DaoException;
import com.currency_exchange.exception.dao_exception.DatabaseAccessException;
import com.currency_exchange.exception.service_exception.*;
import com.currency_exchange.util.Mapper;

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
                    .map(Mapper::mapToCurrencyDtoResponse)
                    .toList();
        } catch (DatabaseAccessException e) {
            throw new ServiceUnavailableException(e.getMessage());
        } catch (DaoException e) {
            throw new ServiceException("Currency service error");
        }
    }

    public CurrencyDtoResponse findByCode(String code) {
        if (code == null || code.isBlank()) {
            throw new InvalidAttributeException(code);
        }
        try {
            return currencyDao.findByCode(code)
                    .map(Mapper::mapToCurrencyDtoResponse)
                    .orElseThrow(() -> new CurrencyNotFoundException(code));
        } catch (DatabaseAccessException e) {
            throw new ServiceUnavailableException(e.getMessage());
        } catch (DaoException e) {
            throw new ServiceException("Currency service error");
        }
    }

    public CurrencyDtoResponse save(CurrencyDtoRequest dtoRequest) {
        try {
            Currency currency = Mapper.mapToCurrency(dtoRequest);
            Currency savedCurrency = currencyDao.save(currency);
            return Mapper.mapToCurrencyDtoResponse(savedCurrency);
        } catch (CurrencyAlreadyExistsException e) {
            throw new CurrencyConflictException(e.getMessage());
        } catch (DatabaseAccessException e) {
            throw new ServiceUnavailableException(e.getMessage());
        } catch (DaoException e) {
            throw new ServiceException("Currency service error");
        }
    }
}
