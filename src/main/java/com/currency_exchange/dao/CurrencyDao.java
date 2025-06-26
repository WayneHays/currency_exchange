package com.currency_exchange.dao;

import com.currency_exchange.entity.Currency;
import com.currency_exchange.exception.DaoException;
import com.currency_exchange.util.ConnectionManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CurrencyDao implements Dao<String, Currency> {

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
            throw new DaoException("Unable to save currency " + e);
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
            throw new DaoException("Unable to find currencies " + e);
        }
    }

    @Override
    public Optional<Currency> findByCode(String code) {
        try (var connection = ConnectionManager.open();
             var prepareStatement = connection.prepareStatement(FIND_BY_CODE_SQL)) {
            prepareStatement.setString(1, code.toUpperCase());

            var resultSet = prepareStatement.executeQuery();
            Currency currency = null;

            if (resultSet.next()) {
                currency = buildCurrency(resultSet);
            }
            return Optional.ofNullable(currency);

        } catch (SQLException e) {
            throw new DaoException("Unable to find currency by code ", e);
        }
    }

    @Override
    public void update(Currency currency) {
        try (var connection = ConnectionManager.open();
             var prepareStatement = connection.prepareStatement(UPDATE_SQL)) {
            prepareStatement.setString(1, currency.getCode());
            prepareStatement.setString(2, currency.getFullName());
            prepareStatement.setString(3, currency.getSign());
            prepareStatement.setLong(4, currency.getId());

            prepareStatement.executeUpdate();

        } catch (SQLException e) {
            throw new DaoException("Unable to update currency " + e);
        }
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
