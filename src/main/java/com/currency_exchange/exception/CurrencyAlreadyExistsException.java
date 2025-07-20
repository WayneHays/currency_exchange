package com.currency_exchange.exception;

public class CurrencyAlreadyExistsException extends RuntimeException {

    public CurrencyAlreadyExistsException(String param) {
        super("Currency with code [%s] already exists".formatted(param.toUpperCase()));
    }
}
