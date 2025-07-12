package com.currency_exchange.exception.dao_exception;

public class ExchangeRateAlreadyExistsException extends DaoException {
    public ExchangeRateAlreadyExistsException(String codes) {
        super("ExchangeRate of currencies %s already exists".formatted(codes));
    }
}
