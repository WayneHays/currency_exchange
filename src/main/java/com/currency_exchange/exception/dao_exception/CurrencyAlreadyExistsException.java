package com.currency_exchange.exception.dao_exception;

public class CurrencyAlreadyExistsException extends DuplicateException {
    public CurrencyAlreadyExistsException(String code) {
        super("Currency with code [%s] already exists".formatted(code));
    }
}
