package com.currency_exchange.dao;

import java.sql.SQLException;
import java.util.List;

public interface Dao<T> {

    List<T> findAll();

    T saveAndSetId(T entity);

    default boolean isDuplicateKeyError(SQLException e) {
        return e.getErrorCode() == 19 && e.getMessage().contains("UNIQUE constraint failed");
    }

    default boolean isConnectionError(SQLException e) {
        int errorCode = e.getErrorCode();
        return errorCode == 14 || errorCode == 10 || errorCode == 8 || errorCode == 7;
    }
}
