package com.currency_exchange.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseDao<T> {
    protected static final int DUPLICATE_ERROR_CODE = 19;

    protected abstract T buildEntity(ResultSet resultSet) throws SQLException;

    protected List<T> buildEntityList(ResultSet resultSet) throws SQLException {
        List<T> entities = new ArrayList<>();
        while (resultSet.next()) {
            entities.add(buildEntity(resultSet));
        }
        return entities;
    }
}
