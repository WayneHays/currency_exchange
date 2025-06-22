package com.currency_exchange.exception;

public class DaoException extends RuntimeException{

    public DaoException(Throwable throwable) {
        super(throwable);
    }
}
