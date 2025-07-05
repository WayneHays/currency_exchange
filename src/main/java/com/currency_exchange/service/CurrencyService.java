package com.currency_exchange.service;

import com.currency_exchange.dao.CurrencyDao;
import com.currency_exchange.dto.request.CurrencyRequest;
import com.currency_exchange.dto.response.CurrencyResponse;
import com.currency_exchange.entity.Currency;
import com.currency_exchange.exception.dao_exception.CurrencyAlreadyExistsException;
import com.currency_exchange.exception.dao_exception.DaoException;
import com.currency_exchange.exception.dao_exception.DatabaseAccessException;
import com.currency_exchange.exception.service_exception.CurrencyNotFoundException;
import com.currency_exchange.exception.service_exception.ServiceException;
import com.currency_exchange.util.Mapper;

import java.util.List;

public class CurrencyService {
    private static final CurrencyService INSTANCE = new CurrencyService();
    public static final String CURRENCY_SERVICE_ERROR = "Service temporarily unavailable";
    private final CurrencyDao currencyDao = CurrencyDao.getInstance();

    private CurrencyService() {
    }

    public static CurrencyService getInstance() {
        return INSTANCE;
    }

    public CurrencyResponse save(CurrencyRequest dtoRequest) {
        try {
            Currency currency = Mapper.mapToCurrency(dtoRequest);
            Currency savedCurrency = currencyDao.saveAndSetId(currency);
            return Mapper.mapToCurrencyDtoResponse(savedCurrency);
        } catch (CurrencyAlreadyExistsException e) {
            throw e;
        } catch (DatabaseAccessException e) {
            throw new ServiceException(CURRENCY_SERVICE_ERROR, e);
        } catch (DaoException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    public List<CurrencyResponse> findAll() {
        try {
            return currencyDao.findAll()
                    .stream()
                    .map(Mapper::mapToCurrencyDtoResponse)
                    .toList();
        } catch (DatabaseAccessException e) {
            throw new ServiceException(CURRENCY_SERVICE_ERROR, e);
        } catch (DaoException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    public CurrencyResponse findByCode(String code) {
        try {
            return currencyDao.findByCode(code)
                    .map(Mapper::mapToCurrencyDtoResponse)
                    .orElseThrow(() -> new CurrencyNotFoundException(code));
        } catch (DatabaseAccessException e) {
            throw new ServiceException(CURRENCY_SERVICE_ERROR, e);
        } catch (DaoException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    public CurrencyResponse findById(Long id) {
        try {
            return currencyDao.findById(id)
                    .map(Mapper::mapToCurrencyDtoResponse)
                    .orElseThrow(() -> new CurrencyNotFoundException(String.valueOf(id)));
        } catch (DatabaseAccessException e) {
            throw new ServiceException(CURRENCY_SERVICE_ERROR, e);
        } catch (DaoException e) {
            throw new ServiceException(e.getMessage());
        }
    }
}
