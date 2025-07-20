package com.currency_exchange.dao;

import com.currency_exchange.entity.Currency;
import com.currency_exchange.entity.ExchangeRate;
import com.currency_exchange.exception.DaoException;
import com.currency_exchange.exception.ExchangeRateAlreadyExistsException;
import com.currency_exchange.exception.ExchangeRateNotFoundException;
import com.currency_exchange.repository.ExchangeRateQueries;
import com.currency_exchange.service.RateType;
import com.currency_exchange.util.connection.ConnectionManager;

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
            if (e.getErrorCode() == 19) {
                throw new ExchangeRateAlreadyExistsException(exchangeRate.getBaseCurrencyId(), exchangeRate.getTargetCurrencyId());
            }
            throw new DaoException(e.getMessage());
        }
    }

    public ExchangeRate update(ExchangeRate exchangeRate) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(ExchangeRateQueries.UPDATE_SQL, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setBigDecimal(1, exchangeRate.getRate());
            preparedStatement.setLong(2, exchangeRate.getBaseCurrencyId());
            preparedStatement.setLong(3, exchangeRate.getTargetCurrencyId());

            if (preparedStatement.executeUpdate() == 0) {
                throw new ExchangeRateNotFoundException(exchangeRate.getBaseCurrencyId(), exchangeRate.getTargetCurrencyId());
            }

            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                return buildEntity(resultSet);
            }
            throw new DaoException("Failed to retrieve updated exchange rate");

        } catch (SQLException e) {
            throw new DaoException(e.getMessage());
        }
    }

    public List<ExchangeRate> findAll() {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(ExchangeRateQueries.FIND_ALL_SQL)) {
            var resultSet = preparedStatement.executeQuery();
            return buildEntityList(resultSet);
        } catch (SQLException e) {
            throw new DaoException(e.getMessage());
        }
    }

    public ExchangeRate findByCurrencyIds(Long baseId, Long targetId) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(ExchangeRateQueries.FIND_BY_IDS_SQL)) {
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

    public ExchangeRate findById(Long id) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(ExchangeRateQueries.FIND_BY_ID_SQL)) {
            preparedStatement.setLong(1, id);
            var resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return buildEntity(resultSet);
            }
            throw new ExchangeRateNotFoundException(id);
        } catch (SQLException e) {
            throw new DaoException(e.getMessage());
        }
    }

    public boolean isRateExists(Currency base, Currency target, RateType type) {
        String sql = switch (type) {
            case DIRECT, REVERSE -> ExchangeRateQueries.EXISTS_SQL;
            case CROSS -> ExchangeRateQueries.CROSS_COURSE_EXISTS_SQL;
        };

        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(sql)) {

            if (type == RateType.REVERSE) {
                preparedStatement.setLong(1, target.getId());
                preparedStatement.setLong(2, base.getId());
            } else {
                preparedStatement.setLong(1, base.getId());
                preparedStatement.setLong(2, target.getId());
            }

            var resultSet = preparedStatement.executeQuery();
            return resultSet.next() && resultSet.getBoolean(1);
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
