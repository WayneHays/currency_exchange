package com.currency_exchange.exception.dao_exception;

public class DatabaseAccessException extends DaoException{
    public DatabaseAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
