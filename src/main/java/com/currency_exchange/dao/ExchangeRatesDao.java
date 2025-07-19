package com.currency_exchange.dao;

import com.currency_exchange.entity.Currency;
import com.currency_exchange.entity.CurrencyPair;
import com.currency_exchange.entity.ExchangeRate;
import com.currency_exchange.exception.dao_exception.DaoException;
import com.currency_exchange.exception.dao_exception.ExchangeRateAlreadyExistsException;
import com.currency_exchange.exception.dao_exception.ExchangeRateNotFoundException;
import com.currency_exchange.repository.ExchangeRateQueries;
import com.currency_exchange.util.connection.ConnectionManager;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class ExchangeRatesDao extends BaseDao<ExchangeRate> {
    public static final String ID = "id";
    public static final String BASE_CURRENCY_ID = "base_currency_id";
    public static final String TARGET_CURRENCY_ID = "target_currency_id";
    public static final String RATE = "rate";
    public static final Long USD_ID = 2L;

    private static final ExchangeRatesDao INSTANCE = new ExchangeRatesDao();

    private ExchangeRatesDao() {
    }

    public static ExchangeRatesDao getInstance() {
        return INSTANCE;
    }

    public ExchangeRate save(ExchangeRate exchangeRate) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(ExchangeRateQueries.SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setLong(1, exchangeRate.getBaseCurrencyId());
            preparedStatement.setLong(2, exchangeRate.getTargetCurrencyId());
            preparedStatement.setBigDecimal(3, exchangeRate.getRate());
            preparedStatement.executeUpdate();

            var generatedKeys = preparedStatement.getGeneratedKeys();

            if (generatedKeys.next()) {
                exchangeRate.setId(generatedKeys.getLong(1));
                return exchangeRate;
            }
            throw new ExchangeRateAlreadyExistsException(exchangeRate.getBaseCurrencyId(), exchangeRate.getTargetCurrencyId());
        } catch (SQLException e) {
            throw new DaoException(e.getMessage());
        }
    }

    /* возвращает пустой список при отсутствии сущностей */
    public ExchangeRate update(CurrencyPair pair, BigDecimal rate) {
        try (var connection = ConnectionManager.get();
             var prepareStatement = connection.prepareStatement(ExchangeRateQueries.UPDATE_SQL)) {
            prepareStatement.setLong(1, pair.base().getId());
            prepareStatement.setLong(2, pair.target().getId());
            prepareStatement.setBigDecimal(3, rate);

            try (var resultSet = prepareStatement.executeQuery()) {
                if (resultSet.next()) {
                    return buildEntity(resultSet);
                }
                throw new ExchangeRateNotFoundException(pair.base().getCode(), pair.target().getCode());
            }
        } catch (SQLException e) {
            throw new DaoException(e.getMessage());
        }
    }

    public List<ExchangeRate> findAll() {
        try (var connection = ConnectionManager.get();
             var prepareStatement = connection.prepareStatement(ExchangeRateQueries.FIND_ALL_SQL)) {
            var resultSet = prepareStatement.executeQuery();
            return buildEntityList(resultSet);
        } catch (SQLException e) {
            throw new DaoException(e.getMessage());
        }
    }

    public ExchangeRate findByCurrencyIds(Long baseId, Long targetId) {
        try (var connection = ConnectionManager.get();
             var prepareStatement = connection.prepareStatement(ExchangeRateQueries.FIND_BY_IDS_SQL)) {
            prepareStatement.setLong(1, baseId);
            prepareStatement.setLong(2, targetId);
            var resultSet = prepareStatement.executeQuery();
            if (resultSet.next()) {
                return buildEntity(resultSet);
            }
            throw new ExchangeRateNotFoundException(baseId, targetId);
        } catch (SQLException e) {
            throw new DaoException(e.getMessage());
        }
    }

    public ExchangeRate findByUsd(Currency currency) {
        return findByCurrencyIds(USD_ID, currency.getId());
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
