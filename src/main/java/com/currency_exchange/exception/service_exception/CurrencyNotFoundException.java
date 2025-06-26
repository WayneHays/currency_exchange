package com.currency_exchange.exception.service_exception;

public class CurrencyNotFoundException extends ServiceException{
    private final String code;

    public CurrencyNotFoundException(String code) {
        super("Currency with code %s not found".formatted(code));
        this.code = code;
    }
}
