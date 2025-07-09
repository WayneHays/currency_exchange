package com.currency_exchange.service;

import com.currency_exchange.exception.dao_exception.DaoException;
import com.currency_exchange.exception.dao_exception.DatabaseAccessException;
import com.currency_exchange.exception.service_exception.ServiceException;

import java.util.function.Supplier;

public class BaseService {
    public static final String SERVICE_ERROR = "Service temporarily unavailable: %s";

    protected <T> T executeDaoOperation(Supplier<T> operation) {
        try {
            return operation.get();
        } catch (DatabaseAccessException e) {
            throw new ServiceException(SERVICE_ERROR.formatted(e.getMessage()));
        } catch (DaoException e) {
            throw new ServiceException(e.getMessage());
        }
    }
}
