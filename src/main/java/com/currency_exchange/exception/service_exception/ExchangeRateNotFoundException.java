package com.currency_exchange.exception.service_exception;

public class ExchangeRateNotFoundException extends ServiceException {

    public ExchangeRateNotFoundException(String pair) {
        super("ExchangeRate not found: %s - %s".formatted(pair.substring(0, 3), pair.substring(3, 6)));
    }
}
