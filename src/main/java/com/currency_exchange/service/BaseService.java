package com.currency_exchange.service;

import com.currency_exchange.exception.dao_exception.CurrencyAlreadyExistsException;
import com.currency_exchange.exception.dao_exception.DaoException;
import com.currency_exchange.exception.dao_exception.DatabaseAccessException;
import com.currency_exchange.exception.dao_exception.ExchangeRateAlreadyExistsException;
import com.currency_exchange.exception.service_exception.ServiceException;

import java.util.function.Supplier;

public class BaseService {
    protected static final String SERVICE_ERROR = "Service temporarily unavailable: %s";

    protected <T> T executeDaoOperation(Supplier<T> operation) {
        try {
            return operation.get();
        } catch (DatabaseAccessException e) {
            throw new ServiceException(SERVICE_ERROR.formatted(e.getMessage()));
        } catch (CurrencyAlreadyExistsException | ExchangeRateAlreadyExistsException e) {
            throw e;
        } catch (DaoException e) {
            throw new ServiceException(e.getMessage());
        }
    }
}
