package com.currency_exchange.dao;

import com.currency_exchange.exception.dao_exception.DaoException;
import com.currency_exchange.exception.dao_exception.DatabaseAccessException;
import com.currency_exchange.util.ConnectionManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class BaseDao<T> implements Dao<T> {

    protected abstract T buildEntity(ResultSet resultSet) throws SQLException;

    protected abstract void setEntityId(T entity, Long id);

    protected abstract DaoException createDuplicateKeyException(T entity, SQLException e);

    protected List<T> executeQueryAndBuildList(String sql,
                                               PreparedStatementSetter setter,
                                               String errorMessage) {
        try (var connection = ConnectionManager.open();
             var prepareStatement = connection.prepareStatement(sql)) {

            setter.setParameters(prepareStatement);
            var resultSet = prepareStatement.executeQuery();
            return buildEntityList(resultSet);
        } catch (SQLException e) {
            throw createDaoException(errorMessage, e);
        }
    }

    protected Optional<T> executeQueryAndBuildSingle(String sql,
                                                     PreparedStatementSetter setter,
                                                     String errorMessage) {
        try (var connection = ConnectionManager.open();
             var prepareStatement = connection.prepareStatement(sql)) {

            setter.setParameters(prepareStatement);
            var resultSet = prepareStatement.executeQuery();
            return getEntity(resultSet);
        } catch (SQLException e) {
            throw createDaoException(errorMessage, e);
        }
    }

    protected T executeInsertAndSetId(String sql,
                                      PreparedStatementSetter setter,
                                      T entity,
                                      String errorMessage) {
        try (var connection = ConnectionManager.open();
             var preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            setter.setParameters(preparedStatement);
            preparedStatement.executeUpdate();

            var generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                setEntityId(entity, generatedKeys.getLong(1));
            }

            return entity;
        } catch (SQLException e) {
            if (isDuplicateKeyError(e)) {
                throw createDuplicateKeyException(entity, e);
            }
            throw createDaoException(errorMessage, e);
        }
    }

    protected int executeUpdate(String sql,
                                PreparedStatementSetter setter,
                                String errorMessage) {
        try (var connection = ConnectionManager.open();
             var prepareStatement = connection.prepareStatement(sql)) {

            setter.setParameters(prepareStatement);
            return prepareStatement.executeUpdate();
        } catch (SQLException e) {
            throw createDaoException(errorMessage, e);
        }
    }

    protected Optional<T> getEntity(ResultSet resultSet) throws SQLException {
        T entity = null;
        if (resultSet.next()) {
            entity = buildEntity(resultSet);
        }
        return Optional.ofNullable(entity);
    }

    protected List<T> buildEntityList(ResultSet resultSet) throws SQLException {
        List<T> entities = new ArrayList<>();
        while (resultSet.next()) {
            entities.add(buildEntity(resultSet));
        }
        return entities;
    }

    protected DaoException createDaoException(String message, SQLException e) {
        if (isConnectionError(e)) {
            return new DatabaseAccessException(e);
        } else {
            return new DaoException(message, e);
        }
    }

    protected boolean isDuplicateKeyError(SQLException e) {
        return e.getErrorCode() == 19 && e.getMessage().contains("UNIQUE constraint failed");
    }

    protected boolean isConnectionError(SQLException e) {
        int errorCode = e.getErrorCode();
        return errorCode == 14 || errorCode == 10 || errorCode == 8 || errorCode == 7;
    }
}
