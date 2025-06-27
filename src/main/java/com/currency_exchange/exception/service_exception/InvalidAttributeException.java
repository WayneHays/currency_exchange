package com.currency_exchange.exception.service_exception;

public class InvalidAttributeException extends ServiceException{

    public InvalidAttributeException(String wrongAttribute) {
        super("Invalid attribute %s".formatted(wrongAttribute));
    }
}
