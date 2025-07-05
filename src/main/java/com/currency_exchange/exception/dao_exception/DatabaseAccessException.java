package com.currency_exchange.exception.dao_exception;

public class DatabaseAccessException extends DaoException {
    public DatabaseAccessException(Throwable cause) {
        super("Database connection problem", cause);
    }
}
