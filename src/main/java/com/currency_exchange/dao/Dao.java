package com.currency_exchange.dao;

import java.util.List;
import java.util.Optional;

public interface Dao<K, T> {

    List<T> findAll();

    Optional<T> findByCode(K id);

    void update(T entity);

    T save(T entity);
}
