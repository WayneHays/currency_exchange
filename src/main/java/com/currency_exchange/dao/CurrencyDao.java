package com.currency_exchange.dao;

import com.currency_exchange.entity.Currency;
import com.currency_exchange.exception.dao_exception.CurrencyAlreadyExistsException;
import com.currency_exchange.exception.dao_exception.DaoException;
import com.currency_exchange.exception.dao_exception.DatabaseAccessException;
import com.currency_exchange.util.ConnectionManager;

import java.sql.PreparedStatement;
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
    public Currency save(Currency currency) {
        try (var connection = ConnectionManager.open();
             var preparedStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {

            setSavingParameters(currency, preparedStatement);
            preparedStatement.executeUpdate();
            setGeneratedId(currency, preparedStatement);
            return currency;
        } catch (SQLException e) {
            if (isDuplicateKeyError(e)) {
                throw new CurrencyAlreadyExistsException(currency.getCode(), e);
            } else if (isConnectionError(e)) {
                throw new DatabaseAccessException("Database connection problem", e);
            } else {
                throw new DaoException("Failed to save currency");
            }
        }
    }

    private void setSavingParameters(Currency currency, PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setString(1, currency.getCode());
        preparedStatement.setString(2, currency.getFullName());
        preparedStatement.setString(3, currency.getSign());
    }

    private void setGeneratedId(Currency currency, PreparedStatement preparedStatement) throws SQLException {
        var generatedKeys = preparedStatement.getGeneratedKeys();
        if (generatedKeys.next()) {
            currency.setId(generatedKeys.getLong(1));
        }
    }

    @Override
    public List<Currency> findAll() throws DaoException {
        try (var connection = ConnectionManager.open();
             var prepareStatement = connection.prepareStatement(FIND_ALL_SQL)) {
            var resultSet = prepareStatement.executeQuery();
            return getCurrencies(resultSet);
        } catch (SQLException e) {
            throw new DaoException("Failed to find currencies " + e);
        }
    }

    private List<Currency> getCurrencies(ResultSet resultSet) throws SQLException {
        List<Currency> currencies = new ArrayList<>();
        while (resultSet.next()) {
            currencies.add(buildCurrency(resultSet));
        }
        return currencies;
    }

    public Optional<Currency> findByCode(String code) {
        try (var connection = ConnectionManager.open();
             var prepareStatement = connection.prepareStatement(FIND_BY_CODE_SQL)) {
            prepareStatement.setString(1, code.toUpperCase());
            var resultSet = prepareStatement.executeQuery();
            return getCurrency(resultSet);

        } catch (SQLException e) {
            throw new DaoException("Failed to find currency by code ", e);
        }
    }

    public Optional<Currency> findById(Long id) {
        try (var connection = ConnectionManager.open();
             var prepareStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            prepareStatement.setLong(1, id);
            var resultSet = prepareStatement.executeQuery();
            return getCurrency(resultSet);

        } catch (SQLException e) {
            throw new DaoException("Failed to find currency by id ", e);
        }
    }

    private Optional<Currency> getCurrency(ResultSet resultSet) throws SQLException {
        Currency currency = null;
        if (resultSet.next()) {
            currency = buildCurrency(resultSet);
        }
        return Optional.ofNullable(currency);
    }

    @Override
    public void update(Currency currency) {
        try (var connection = ConnectionManager.open();
             var prepareStatement = connection.prepareStatement(UPDATE_SQL)) {
            setUpdatedParameters(currency, prepareStatement);
            prepareStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Failed to update currency " + e);
        }
    }

    private void setUpdatedParameters(Currency currency, PreparedStatement prepareStatement) throws SQLException {
        prepareStatement.setString(1, currency.getCode());
        prepareStatement.setString(2, currency.getFullName());
        prepareStatement.setString(3, currency.getSign());
        prepareStatement.setLong(4, currency.getId());
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
