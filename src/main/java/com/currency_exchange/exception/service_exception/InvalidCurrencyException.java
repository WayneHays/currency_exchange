package com.currency_exchange.exception.service_exception;

public class InvalidCurrencyException extends ServiceException {

    public InvalidCurrencyException() {
        super("Currency code cannot be empty");
    }
}
