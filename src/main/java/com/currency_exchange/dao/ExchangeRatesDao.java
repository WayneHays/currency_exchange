package com.currency_exchange.dao;

import com.currency_exchange.entity.ExchangeRate;
import com.currency_exchange.exception.dao_exception.DaoException;
import com.currency_exchange.exception.dao_exception.DatabaseAccessException;
import com.currency_exchange.exception.dao_exception.ExchangeRateAlreadyExistsException;
import com.currency_exchange.util.ConnectionManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExchangeRatesDao implements Dao<ExchangeRate> {
    private static final ExchangeRatesDao INSTANCE = new ExchangeRatesDao();

    private static final String SAVE_SQL = """
            INSERT INTO exchange_rates (base_currency_id, target_currency_id, rate) VALUES (?,?,?)
            """;

    private static final String FIND_ALL_SQL = """
            SELECT id,
                base_currency_id,
                target_currency_id,
                rate
            FROM exchange_rates
            """;

    private static final String FIND_BY_PAIR_SQL = FIND_ALL_SQL + """
            WHERE base_currency_id = ? AND target_currency_id = ?
            """;

    private static final String UPDATE_SQL = """
            UPDATE exchange_rates
            SET base_currency_id = ?,
            target_currency_id = ?,
            rate = ?
            WHERE id = ?
            """;


    private ExchangeRatesDao() {
    }

    public static ExchangeRatesDao getInstance() {
        return INSTANCE;
    }

    @Override
    public ExchangeRate saveAndSetId(ExchangeRate exchangeRate) {
        try (var connection = ConnectionManager.open();
             var prepareStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {

            prepareStatement.setLong(1, exchangeRate.getBaseCurrencyId());
            prepareStatement.setLong(2, exchangeRate.getTargetCurrencyId());
            prepareStatement.setBigDecimal(3, exchangeRate.getRate());
            prepareStatement.executeUpdate();

            return getExchangeRate(exchangeRate, prepareStatement);
        } catch (SQLException e) {
            if (isDuplicateKeyError(e)) {
                throw new ExchangeRateAlreadyExistsException("%s -> %s", e);
            } else if (isConnectionError(e)) {
                throw new DatabaseAccessException(e);
            } else {
                throw new DaoException("Failed to save exchangeRate");
            }
        }
    }

    @Override
    public List<ExchangeRate> findAll() {
        try (var connection = ConnectionManager.open();
             var prepareStatement = connection.prepareStatement(FIND_ALL_SQL)) {

            var resultSet = prepareStatement.executeQuery();
            return getExchangeRates(resultSet);
        } catch (SQLException e) {
            if (isConnectionError(e)) {
                throw new DatabaseAccessException(e);
            } else {
                throw new DaoException("Failed to find all exchange rates " + e);
            }
        }
    }

    @Override
    public void update(ExchangeRate exchangeRate) {
        try (var connection = ConnectionManager.open();
             var prepareStatement = connection.prepareStatement(UPDATE_SQL)) {

            prepareStatement.setLong(1, exchangeRate.getBaseCurrencyId());
            prepareStatement.setLong(2, exchangeRate.getTargetCurrencyId());
            prepareStatement.setBigDecimal(3, exchangeRate.getRate());
            prepareStatement.setLong(4, exchangeRate.getId());

            prepareStatement.executeUpdate();
        } catch (SQLException e) {
            if (isConnectionError(e)) {
                throw new DatabaseAccessException(e);
            } else {
                throw new DaoException("Failed to update exchange rate " + e);
            }
        }
    }

    public Optional<ExchangeRate> findByCurrencyIds(Long first, Long second) {
        try (var connection = ConnectionManager.open();
             var prepareStatement = connection.prepareStatement(FIND_BY_PAIR_SQL)) {

            prepareStatement.setLong(1, first);
            prepareStatement.setLong(2, second);

            var resultSet = prepareStatement.executeQuery();

            return getExchangeRate(resultSet);
        } catch (SQLException e) {
            if (isConnectionError(e)) {
                throw new DatabaseAccessException(e);
            } else {
                throw new DaoException("Failed to find exchange rate with ids %d -> %d".formatted(first, second), e);
            }
        }
    }

    private ExchangeRate getExchangeRate(ExchangeRate exchangeRate, PreparedStatement prepareStatement) throws SQLException {
        var generatedKeys = prepareStatement.getGeneratedKeys();
        if (generatedKeys.next()) {
            exchangeRate.setId(generatedKeys.getLong(1));
        }
        return exchangeRate;
    }

    private List<ExchangeRate> getExchangeRates(ResultSet resultSet) throws SQLException {
        List<ExchangeRate> exchangeRates = new ArrayList<>();
        while (resultSet.next()) {
            exchangeRates.add(buildExchangeRate(resultSet));
        }
        return exchangeRates;
    }

    private Optional<ExchangeRate> getExchangeRate(ResultSet resultSet) throws SQLException {
        ExchangeRate exchangeRate = null;
        if (resultSet.next()) {
            exchangeRate = buildExchangeRate(resultSet);
        }
        return Optional.ofNullable(exchangeRate);
    }

    private ExchangeRate buildExchangeRate(ResultSet resultSet) throws SQLException {
        return new ExchangeRate(
                resultSet.getLong("id"),
                resultSet.getLong("base_currency_id"),
                resultSet.getLong("target_currency_id"),
                resultSet.getBigDecimal("rate")
        );
    }
}
