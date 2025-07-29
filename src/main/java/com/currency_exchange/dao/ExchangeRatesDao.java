package com.currency_exchange.dao;

import com.currency_exchange.entity.Currency;
import com.currency_exchange.entity.ExchangeRate;
import com.currency_exchange.entity.ExchangeRateData;
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
    public static final String EXCHANGE_RATE_ID = "er_id";
    public static final String BASE_ID = "base_id";
    public static final String BASE_CODE = "base_code";
    public static final String BASE_NAME = "base_name";
    public static final String BASE_SIGN = "base_sign";
    public static final String TARGET_ID = "target_id";
    public static final String TARGET_CODE = "target_code";
    public static final String TARGET_NAME = "target_name";
    public static final String TARGET_SIGN = "target_sign";
    
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
            RETURNING id, base_currency_id, target_currency_id, rate
            """;

    public static final String FIND_WITH_CURRENCY_CODES = """
            SELECT
                er.id as er_id, er.base_currency_id, er.target_currency_id, er.rate,
                bc.id as base_id, bc.code as base_code, bc.full_name as base_name, bc.sign as base_sign,
                tc.id as target_id, tc.code as target_code, tc.full_name as target_name, tc.sign as target_sign
            FROM exchange_rates er
            JOIN currencies bc ON er.base_currency_id = bc.id
            JOIN currencies tc ON er.target_currency_id = tc.id
            WHERE bc.code = ? AND tc.code = ?
            """;

    public static final String FIND_BY_IDS_SQL = FIND_ALL_SQL + " WHERE base_currency_id = ? AND target_currency_id = ?";

    public static final String FAILED_TO_RETRIEVE_GENERATED_ID = "Failed to retrieve generated ID";

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

    public ExchangeRate update(String baseCurrencyCode, String targetCurrencyCode, BigDecimal rate) {
        return executeQuerySingle(UPDATE_SQL, rate, baseCurrencyCode, targetCurrencyCode)
                .orElseThrow(() -> new ExchangeRateNotFoundException(
                        baseCurrencyCode,
                        targetCurrencyCode));
    }

    public ExchangeRateData findExchangeRateWithCurrencyCodes(String baseCurrencyCode, String targetCurrencyCode) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(FIND_WITH_CURRENCY_CODES)) {

            preparedStatement.setString(1, baseCurrencyCode);
            preparedStatement.setString(2, targetCurrencyCode);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.next()) {
                throw new ExchangeRateNotFoundException(baseCurrencyCode, targetCurrencyCode);
            }

            return new ExchangeRateData(
                    new ExchangeRate(
                            resultSet.getLong(EXCHANGE_RATE_ID),
                            resultSet.getLong(BASE_CURRENCY_ID),
                            resultSet.getLong(TARGET_CURRENCY_ID),
                            resultSet.getBigDecimal(RATE)
                    ),
                    new Currency(
                            resultSet.getLong(BASE_ID),
                            resultSet.getString(BASE_CODE),
                            resultSet.getString(BASE_NAME),
                            resultSet.getString(BASE_SIGN)
                    ),
                    new Currency(
                            resultSet.getLong(TARGET_ID),
                            resultSet.getString(TARGET_CODE),
                            resultSet.getString(TARGET_NAME),
                            resultSet.getString(TARGET_SIGN)
                    )
            );
        } catch (SQLException e) {
            throw new DaoException(e.getMessage());
        }
    }

    public ExchangeRate findByCurrencyIds(Long baseId, Long targetId) {
        return executeQuerySingle(FIND_BY_IDS_SQL, baseId, targetId)
                .orElseThrow(() -> new ExchangeRateNotFoundException(baseId, targetId));
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
