package com.currency_exchange.exception.dao_exception;

public class CurrencyAlreadyExistsException extends DaoException {
    private final String code;

    public CurrencyAlreadyExistsException(String code, Throwable cause) {
        super("Currency with code %s already exists".formatted(code), cause);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
