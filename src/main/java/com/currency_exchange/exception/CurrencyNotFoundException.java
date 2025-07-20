package com.currency_exchange.exception;

public class CurrencyNotFoundException extends RuntimeException {

    public CurrencyNotFoundException(String code) {
        super("Currency with code [%s] not found".formatted(code.toUpperCase()));
    }
}
