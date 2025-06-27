package com.currency_exchange.exception.dao_exception;

public class CurrencyAlreadyExistsException extends DaoException {
    public CurrencyAlreadyExistsException(String code, Throwable cause) {
        super("Currency with code %s already exists".formatted(code), cause);
    }
}
