package com.currency_exchange.dao;

import com.currency_exchange.exception.DaoException;
import com.currency_exchange.util.connection.ConnectionManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseDao<T> {
    public static final String ID = "id";
    protected static final int DUPLICATE_ERROR_CODE = 19;

    protected List<T> executeQuery(String sql) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(sql)) {
            var resultSet = preparedStatement.executeQuery();
            return buildEntityList(resultSet);
        } catch (SQLException e) {
            throw new DaoException(e.getMessage());
        }
    }

    protected List<T> buildEntityList(ResultSet resultSet) throws SQLException {
        List<T> entities = new ArrayList<>();
        while (resultSet.next()) {
            entities.add(buildEntity(resultSet));
        }
        return entities;
    }

    protected abstract T buildEntity(ResultSet resultSet) throws SQLException;
}
