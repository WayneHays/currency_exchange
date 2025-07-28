package com.currency_exchange.dao;

import com.currency_exchange.entity.ExchangeRate;
import com.currency_exchange.exception.DaoException;
import com.currency_exchange.exception.ExchangeRateAlreadyExistsException;
import com.currency_exchange.exception.ExchangeRateNotFoundException;
import com.currency_exchange.util.connection.ConnectionManager;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class ExchangeRatesDao extends BaseDao<ExchangeRate> {
    public static final String BASE_CURRENCY_ID = "base_currency_id";
    public static final String TARGET_CURRENCY_ID = "target_currency_id";
    public static final String RATE = "rate";

    public static final String FAILED_TO_RETRIEVE_GENERATED_ID = "Failed to retrieve generated ID";

    public static final String SAVE_SQL = """
            INSERT INTO exchange_rates (base_currency_id, target_currency_id, rate) VALUES (?,?,?)
            """;

    public static final String FIND_ALL_SQL = """
            SELECT id, base_currency_id, target_currency_id, rate FROM exchange_rates
            """;

    public static final String UPDATE_SQL = """
            UPDATE exchange_rates
            SET rate = ?
            WHERE base_currency_id = (SELECT id FROM currencies WHERE code = ?)
            AND target_currency_id = (SELECT id FROM currencies WHERE code = ?)
            """;

    public static final String FIND_BY_IDS_SQL = FIND_ALL_SQL + " WHERE base_currency_id = ? AND target_currency_id = ?";

    private static final ExchangeRatesDao INSTANCE = new ExchangeRatesDao();

    private ExchangeRatesDao() {
    }

    public static ExchangeRatesDao getInstance() {
        return INSTANCE;
    }

    public ExchangeRate save(ExchangeRate exchangeRate) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(
                     SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setLong(1, exchangeRate.getBaseCurrencyId());
            preparedStatement.setLong(2, exchangeRate.getTargetCurrencyId());
            preparedStatement.setBigDecimal(3, exchangeRate.getRate());
            preparedStatement.executeUpdate();
            var generatedKeys = preparedStatement.getGeneratedKeys();

            if (generatedKeys.next()) {
                exchangeRate.setId(generatedKeys.getLong(1));
                return exchangeRate;
            }
            throw new DaoException(FAILED_TO_RETRIEVE_GENERATED_ID);
        } catch (SQLException e) {
            if (e.getErrorCode() == DUPLICATE_ERROR_CODE) {
                throw new ExchangeRateAlreadyExistsException(
                        exchangeRate.getBaseCurrencyId(),
                        exchangeRate.getTargetCurrencyId());
            }
            throw new DaoException(e.getMessage());
        }
    }

    public List<ExchangeRate> findAll() {
        return executeQuery(FIND_ALL_SQL);
    }

    public boolean update(String baseCurrencyCode, String targetCurrencyCode, BigDecimal rate) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(UPDATE_SQL)) {

            preparedStatement.setBigDecimal(1, rate);
            preparedStatement.setString(2, baseCurrencyCode);
            preparedStatement.setString(3, targetCurrencyCode);

            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new DaoException(e.getMessage());
        }
    }

    public ExchangeRate findByCurrencyIds(Long baseId, Long targetId) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(FIND_BY_IDS_SQL)) {
            preparedStatement.setLong(1, baseId);
            preparedStatement.setLong(2, targetId);
            var resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return buildEntity(resultSet);
            }
            throw new ExchangeRateNotFoundException(baseId, targetId);
        } catch (SQLException e) {
            throw new DaoException(e.getMessage());
        }
    }

    @Override
    protected ExchangeRate buildEntity(ResultSet resultSet) throws SQLException {
        return new ExchangeRate(
                resultSet.getLong(ID),
                resultSet.getLong(BASE_CURRENCY_ID),
                resultSet.getLong(TARGET_CURRENCY_ID),
                resultSet.getBigDecimal(RATE)
        );
    }
}
