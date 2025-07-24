package com.currency_exchange.dao;

import com.currency_exchange.entity.Currency;
import com.currency_exchange.exception.CurrencyAlreadyExistsException;
import com.currency_exchange.exception.CurrencyNotFoundException;
import com.currency_exchange.exception.DaoException;
import com.currency_exchange.util.connection.ConnectionManager;

import java.sql.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CurrencyDao extends BaseDao<Currency> {
    public static final String ID = "id";
    public static final String CODE = "code";
    public static final String FULL_NAME = "full_name";
    public static final String SIGN = "sign";

    public static final String SAVE_SQL = "INSERT INTO currencies (code, full_name, sign) VALUES (?,?,?)";
    public static final String FIND_ALL_SQL = "SELECT id, code, full_name, sign FROM currencies";
    public static final String FIND_BY_IDS_SQL = FIND_ALL_SQL + " WHERE id IN (";
    public static final String FIND_BY_CODE_SQL = FIND_ALL_SQL + " WHERE CODE = ?";

    private static final CurrencyDao INSTANCE = new CurrencyDao();

    private CurrencyDao() {
    }

    public static CurrencyDao getInstance() {
        return INSTANCE;
    }

    public Currency save(Currency currency) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(
                     SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, currency.getCode());
            preparedStatement.setString(2, currency.getFullName());
            preparedStatement.setString(3, currency.getSign());
            preparedStatement.executeUpdate();

            var generatedKeys = preparedStatement.getGeneratedKeys();

            if (generatedKeys.next()) {
                currency.setId(generatedKeys.getLong(1));
                return currency;
            }
            throw new DaoException("Creating currency failed");
        } catch (SQLException e) {
            if (e.getErrorCode() == DUPLICATE_ERROR_CODE) {
                throw new CurrencyAlreadyExistsException(currency.getCode());
            }
            throw new DaoException(e.getMessage());
        }
    }

    public Currency findByCode(String code) {
        try (var connection = ConnectionManager.get();
             var prepareStatement = connection.prepareStatement(FIND_BY_CODE_SQL)) {
            prepareStatement.setString(1, code);
            var resultSet = prepareStatement.executeQuery();
            if (resultSet.next()) {
                return buildEntity(resultSet);
            }
            throw new CurrencyNotFoundException(code);
        } catch (SQLException e) {
            throw new DaoException(e.getMessage());
        }
    }

    public List<Currency> findAll() {
        try (var connection = ConnectionManager.get();
             var prepareStatement = connection.prepareStatement(FIND_ALL_SQL)) {
            var resultSet = prepareStatement.executeQuery();
            return buildEntityList(resultSet);
        } catch (SQLException e) {
            throw new DaoException(e.getMessage());
        }
    }

    public Map<Long, Currency> findByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new HashMap<>();
        }
        String placeholders = String.join(",", Collections.nCopies(ids.size(), "?"));
        String sql = "%s%s)".formatted(FIND_BY_IDS_SQL, placeholders);

        try (Connection connection = ConnectionManager.get();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            int index = 1;
            for (Long id : ids) {
                statement.setLong(index++, id);
            }

            Map<Long, Currency> result = new HashMap<>();
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Currency currency = buildEntity(resultSet);
                    result.put(currency.getId(), currency);
                }
            }
            return result;
        } catch (SQLException e) {
            throw new DaoException(e.getMessage());
        }
    }

    @Override
    protected Currency buildEntity(ResultSet resultSet) throws SQLException {
        return new Currency(
                resultSet.getLong(ID),
                resultSet.getString(CODE),
                resultSet.getString(FULL_NAME),
                resultSet.getString(SIGN)
        );
    }
}
