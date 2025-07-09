package com.currency_exchange.service;

import com.currency_exchange.dao.CurrencyDao;
import com.currency_exchange.dto.currency.CurrencyCreateRequest;
import com.currency_exchange.dto.currency.CurrencyResponse;
import com.currency_exchange.entity.Currency;
import com.currency_exchange.exception.dao_exception.CurrencyAlreadyExistsException;
import com.currency_exchange.exception.dao_exception.DaoException;
import com.currency_exchange.exception.dao_exception.DatabaseAccessException;
import com.currency_exchange.exception.service_exception.CurrencyNotFoundException;
import com.currency_exchange.exception.service_exception.ServiceException;
import com.currency_exchange.util.Mapper;

import java.util.ArrayList;
import java.util.List;

public class CurrencyService {
    public static final String CURRENCY_SERVICE_ERROR = "Service temporarily unavailable";
    private static final CurrencyService INSTANCE = new CurrencyService();
    private final CurrencyDao currencyDao = CurrencyDao.getInstance();

    private CurrencyService() {
    }

    public static CurrencyService getInstance() {
        return INSTANCE;
    }

    public CurrencyResponse save(CurrencyCreateRequest dto) {
        try {
            Currency currency = Mapper.mapToCurrency(dto);
            Currency savedCurrency = currencyDao.saveAndSetId(currency);
            return Mapper.mapToCurrencyResponse(savedCurrency);
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
            List<CurrencyResponse> result = new ArrayList<>();
            List<Currency> currencies = currencyDao.findAll();

            for (Currency currency : currencies) {
                result.add(Mapper.mapToCurrencyResponse(currency));
            }
            return result;
        } catch (DatabaseAccessException e) {
            throw new ServiceException(CURRENCY_SERVICE_ERROR, e);
        } catch (DaoException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    public CurrencyResponse prepareCurrencyResponseByCode(String code) {
        Currency currency = findCurrencyEntityByCode(code);
        return Mapper.mapToCurrencyResponse(currency);
    }

    public Currency findCurrencyEntityByCode(String code) {
        try {
            return currencyDao.findByCode(code)
                    .orElseThrow(() -> new CurrencyNotFoundException(code));
        } catch (DatabaseAccessException e) {
            throw new ServiceException(CURRENCY_SERVICE_ERROR, e);
        } catch (DaoException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    public Currency findCurrencyEntityById(Long id) {
        try {
            return currencyDao.findById(id)
                    .orElseThrow(() -> new CurrencyNotFoundException(String.valueOf(id)));
        } catch (DatabaseAccessException e) {
            throw new ServiceException(CURRENCY_SERVICE_ERROR, e);
        } catch (DaoException e) {
            throw new ServiceException(e.getMessage());
        }
    }
}
