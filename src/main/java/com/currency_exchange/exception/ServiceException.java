package com.currency_exchange.exception;

public class ServiceException extends BusinessException{

    public ServiceException(String message, int httpStatus) {
        super(message, httpStatus);
    }
}
