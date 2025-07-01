package com.currency_exchange.service;

import com.currency_exchange.dao.CurrencyDao;
import com.currency_exchange.dto.request.CurrencyDtoRequest;
import com.currency_exchange.dto.response.CurrencyDtoResponse;
import com.currency_exchange.entity.Currency;
import com.currency_exchange.exception.dao_exception.CurrencyAlreadyExistsException;
import com.currency_exchange.exception.dao_exception.DaoException;
import com.currency_exchange.exception.dao_exception.DatabaseAccessException;
import com.currency_exchange.exception.service_exception.CurrencyConflictException;
import com.currency_exchange.exception.service_exception.CurrencyNotFoundException;
import com.currency_exchange.exception.service_exception.ServiceException;
import com.currency_exchange.exception.service_exception.ServiceUnavailableException;
import com.currency_exchange.util.Mapper;

import java.util.List;

public class CurrencyService {
    private static final CurrencyService INSTANCE = new CurrencyService();
    public static final String CURRENCY_SERVICE_ERROR = "Currency service error";
    private final CurrencyDao currencyDao = CurrencyDao.getInstance();

    private CurrencyService() {
    }

    public static CurrencyService getInstance() {
        return INSTANCE;
    }

    public List<CurrencyDtoResponse> findAll() {
        try {
            return currencyDao.findAll()
                    .stream()
                    .map(Mapper::mapToCurrencyDtoResponse)
                    .toList();
        } catch (DatabaseAccessException e) {
            throw new ServiceUnavailableException(e.getMessage());
        } catch (DaoException e) {
            throw new ServiceException(CURRENCY_SERVICE_ERROR);
        }
    }

    public CurrencyDtoResponse findByCode(String code) {
        try {
            return currencyDao.findByCode(code)
                    .map(Mapper::mapToCurrencyDtoResponse)
                    .orElseThrow(() -> new CurrencyNotFoundException(code));
        } catch (DatabaseAccessException e) {
            throw new ServiceUnavailableException(e.getMessage());
        } catch (DaoException e) {
            throw new ServiceException(CURRENCY_SERVICE_ERROR);
        }
    }

    public CurrencyDtoResponse findById(Long id) {
        try {
            return currencyDao.findById(id)
                    .map(Mapper::mapToCurrencyDtoResponse)
                    .orElseThrow(() -> new CurrencyNotFoundException(String.valueOf(id)));
        } catch (DatabaseAccessException e) {
            throw new ServiceUnavailableException(e.getMessage());
        } catch (DaoException e) {
            throw new ServiceException(CURRENCY_SERVICE_ERROR);
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
            throw new ServiceException(CURRENCY_SERVICE_ERROR);
        }
    }
}
