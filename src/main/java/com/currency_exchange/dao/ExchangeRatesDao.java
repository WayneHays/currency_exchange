package com.currency_exchange.dao;

import com.currency_exchange.entity.ExchangeRate;
import com.currency_exchange.exception.dao_exception.DaoException;
import com.currency_exchange.exception.dao_exception.DatabaseAccessException;
import com.currency_exchange.exception.dao_exception.ExchangeRateAlreadyExistsException;
import com.currency_exchange.util.ConnectionManager;

import java.math.BigDecimal;
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
            SET rate = ?
            WHERE id = ?
            """;
    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + """
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

            var generatedKeys = prepareStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                exchangeRate.setId(generatedKeys.getLong(1));
            }
            return exchangeRate;
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

    public Optional<ExchangeRate> update(ExchangeRate exchangeRate, BigDecimal newRate) {
        try (var connection = ConnectionManager.open();
             var prepareStatement = connection.prepareStatement(UPDATE_SQL)) {

            prepareStatement.setBigDecimal(1, newRate);
            prepareStatement.setLong(2, exchangeRate.getId());

            int rowsAffected = prepareStatement.executeUpdate();

            if (rowsAffected == 0) {
                return Optional.empty();
            }

            try (var selectStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {
                selectStatement.setLong(1, exchangeRate.getId());
                ResultSet resultSet = selectStatement.executeQuery();
                return getExchangeRate(resultSet);
            }
        } catch (SQLException e) {
            if (isConnectionError(e)) {
                throw new DatabaseAccessException(e);
            } else {
                throw new DaoException("Failed to update exchange rate " + e);
            }
        }
    }

    @Override
    public List<ExchangeRate> findAll() {
        try (var connection = ConnectionManager.open();
             var prepareStatement = connection.prepareStatement(FIND_ALL_SQL)) {

            var resultSet = prepareStatement.executeQuery();
            List<ExchangeRate> exchangeRates = new ArrayList<>();

            while (resultSet.next()) {
                exchangeRates.add(buildExchangeRate(resultSet));
            }
            return exchangeRates;
        } catch (SQLException e) {
            if (isConnectionError(e)) {
                throw new DatabaseAccessException(e);
            } else {
                throw new DaoException("Failed to find all exchange rates " + e);
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
