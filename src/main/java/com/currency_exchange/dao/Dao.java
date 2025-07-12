package com.currency_exchange.dao;

import java.util.List;

public interface Dao<T> {

    List<T> findAll();

    T saveAndSetId(T entity);
}
