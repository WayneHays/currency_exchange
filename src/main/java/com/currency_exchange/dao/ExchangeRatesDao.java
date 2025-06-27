package com.currency_exchange.dao;

import com.currency_exchange.entity.ExchangeRates;
import com.currency_exchange.exception.dao_exception.DaoException;
import com.currency_exchange.util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExchangeRatesDao implements Dao<Long, ExchangeRates> {
    private static final ExchangeRatesDao INSTANCE = new ExchangeRatesDao();

    private static final String CREATE_SQL = """
            INSERT INTO exchange_rates (base_currency_id, target_currency_id, rate) VALUES (?,?,?)
            """;

    private static final String FIND_ALL_SQL = """
            SELECT id,
                base_currency_id,
                target_currency_id,
                rate
            FROM exchange_rates
            """;

    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + """
            WHERE id = ?
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
    public ExchangeRates save(ExchangeRates exchangeRates) {
        try (var connection = ConnectionManager.open();
             var prepareStatement = connection.prepareStatement(CREATE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            prepareStatement.setLong(1, exchangeRates.getBaseCurrencyId());
            prepareStatement.setLong(2, exchangeRates.getTargetCurrencyId());
            prepareStatement.setBigDecimal(3, exchangeRates.getRate());

            prepareStatement.executeUpdate();
            var generatedKeys = prepareStatement.getGeneratedKeys();

            if (generatedKeys.next()) {
                exchangeRates.setId(generatedKeys.getLong(1));
            }

            return exchangeRates;
        } catch (SQLException e) {
            throw new DaoException("Failed to save exchange rate " + e.getMessage());
        }
    }

    @Override
    public List<ExchangeRates> findAll() {
        try (var connection = ConnectionManager.open();
             var prepareStatement = connection.prepareStatement(FIND_ALL_SQL)) {

            var resultSet = prepareStatement.executeQuery();
            List<ExchangeRates> exchangeRates = new ArrayList<>();

            while (resultSet.next()) {
                exchangeRates.add(buildExchangeRates(resultSet));
            }

            return exchangeRates;
        } catch (SQLException e) {
            throw new DaoException("Failed to get all exchange rates " + e.getMessage());
        }
    }

    @Override
    public Optional<ExchangeRates> findByCode(Long id) {
        try (var connection = ConnectionManager.open();
             var prepareStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            prepareStatement.setLong(1, id);
            var resultSet = prepareStatement.executeQuery();

            ExchangeRates exchangeRates = null;

            if (resultSet.next()) {
                exchangeRates = buildExchangeRates(resultSet);
            }

            return Optional.ofNullable(exchangeRates);
        } catch (SQLException e) {
            throw new DaoException("Failed to get exchange rate " + e.getMessage());
        }
    }

    @Override
    public void update(ExchangeRates exchangeRates) {
        try (var connection = ConnectionManager.open();
             var prepareStatement = connection.prepareStatement(UPDATE_SQL)) {
            prepareStatement.setLong(1, exchangeRates.getBaseCurrencyId());
            prepareStatement.setLong(2, exchangeRates.getTargetCurrencyId());
            prepareStatement.setBigDecimal(3, exchangeRates.getRate());
            prepareStatement.setLong(4, exchangeRates.getId());
            prepareStatement.executeUpdate();

        } catch (SQLException e) {
            throw new DaoException("Failed to update exchange rate " + e.getMessage());
        }
    }

    private ExchangeRates buildExchangeRates(ResultSet resultSet) throws SQLException {
        return new ExchangeRates(
                resultSet.getLong("id"),
                resultSet.getLong("base_currency_id"),
                resultSet.getLong("target_currency_id"),
                resultSet.getBigDecimal("rate")
        );
    }
}
