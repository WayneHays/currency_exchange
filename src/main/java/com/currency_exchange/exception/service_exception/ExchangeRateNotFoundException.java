package com.currency_exchange.exception.service_exception;

public class ExchangeRateNotFoundException extends ServiceException {

    public ExchangeRateNotFoundException(String base, String target) {
        super("ExchangeRate not found: %s - %s".formatted(base, target));
    }
}
