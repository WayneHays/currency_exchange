package com.currency_exchange.exception;

public class ServiceException extends RuntimeException {

    public ServiceException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
