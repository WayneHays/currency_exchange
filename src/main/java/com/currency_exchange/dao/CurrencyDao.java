package com.currency_exchange.dao;

import com.currency_exchange.entity.Currency;
import com.currency_exchange.exception.dao_exception.CurrencyAlreadyExistsException;
import com.currency_exchange.exception.dao_exception.DaoException;
import com.currency_exchange.exception.dao_exception.DatabaseAccessException;
import com.currency_exchange.util.ConnectionManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CurrencyDao implements Dao<Currency> {

    private static final CurrencyDao INSTANCE = new CurrencyDao();

    private static final String SAVE_SQL = """
            INSERT INTO currencies (code, full_name, sign) VALUES (?,?,?)
            """;

    private static final String FIND_ALL_SQL = """
            SELECT id,
                code,
                full_name,
                sign
            FROM currencies
            """;

    private static final String FIND_BY_CODE_SQL = FIND_ALL_SQL + """
            WHERE code = ?
            """;

    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + """
            WHERE id = ?
            """;

    private static final String UPDATE_SQL = """
            UPDATE currencies
            SET code = ?,
            full_name = ?,
            sign = ?
            WHERE id = ?
            """;

    private CurrencyDao() {
    }

    public static CurrencyDao getInstance() {
        return INSTANCE;
    }

    @Override
    public Currency saveAndSetId(Currency currency) {
        try (var connection = ConnectionManager.open();
             var preparedStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, currency.getCode());
            preparedStatement.setString(2, currency.getFullName());
            preparedStatement.setString(3, currency.getSign());
            preparedStatement.executeUpdate();

            var generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                currency.setId(generatedKeys.getLong(1));
            }

            return currency;
        } catch (SQLException e) {
            if (isDuplicateKeyError(e)) {
                throw new CurrencyAlreadyExistsException(currency.getCode(), e);
            } else if (isConnectionError(e)) {
                throw new DatabaseAccessException(e);
            } else {
                throw new DaoException("Failed to save currency");
            }
        }
    }

    @Override
    public List<Currency> findAll() throws DaoException {
        try (var connection = ConnectionManager.open();
             var prepareStatement = connection.prepareStatement(FIND_ALL_SQL)) {

            var resultSet = prepareStatement.executeQuery();
            List<Currency> currencies = new ArrayList<>();
            while (resultSet.next()) {
                currencies.add(buildCurrency(resultSet));
            }
            return currencies;
        } catch (SQLException e) {
            if (isConnectionError(e)) {
                throw new DatabaseAccessException(e);
            } else {
                throw new DaoException("Failed to find all currencies " + e);
            }
        }
    }

    public Optional<Currency> findByCode(String code) {
        try (var connection = ConnectionManager.open();
             var prepareStatement = connection.prepareStatement(FIND_BY_CODE_SQL)) {
            prepareStatement.setString(1, code.toUpperCase());
            var resultSet = prepareStatement.executeQuery();
            return getCurrency(resultSet);
        } catch (SQLException e) {
            if (isConnectionError(e)) {
                throw new DatabaseAccessException(e);
            } else {
                throw new DaoException("Failed to find currency with code %s".formatted(code), e);
            }
        }
    }

    public Optional<Currency> findById(Long id) {
        try (var connection = ConnectionManager.open();
             var prepareStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            prepareStatement.setLong(1, id);
            var resultSet = prepareStatement.executeQuery();
            return getCurrency(resultSet);
        } catch (SQLException e) {
            if (isConnectionError(e)) {
                throw new DatabaseAccessException(e);
            } else {
                throw new DaoException("Failed to find currency with id %d".formatted(id), e);
            }
        }
    }

    private Optional<Currency> getCurrency(ResultSet resultSet) throws SQLException {
        Currency currency = null;
        if (resultSet.next()) {
            currency = buildCurrency(resultSet);
        }
        return Optional.ofNullable(currency);
    }

    private Currency buildCurrency(ResultSet resultSet) throws SQLException {
        return new Currency(
                resultSet.getLong("id"),
                resultSet.getString("code"),
                resultSet.getString("full_name"),
                resultSet.getString("sign")
        );
    }
}
