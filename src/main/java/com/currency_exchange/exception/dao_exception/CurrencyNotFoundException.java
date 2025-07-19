package com.currency_exchange.exception.dao_exception;

public class CurrencyNotFoundException extends RuntimeException {

    public CurrencyNotFoundException(String code) {
        super("Currency with code %s not found".formatted(code));
    }
}
