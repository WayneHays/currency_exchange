package com.currency_exchange.exception.service_exception;

public class ExchangeRateNotFoundException extends ServiceException {

    public ExchangeRateNotFoundException(String baseCode, String targetCode) {
        super("ExchangeRate not found: %s - %s".formatted(baseCode, targetCode));
    }
}
