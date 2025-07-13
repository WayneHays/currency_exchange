package com.currency_exchange.dao;

import com.currency_exchange.entity.Currency;
import com.currency_exchange.entity.CurrencyPair;
import com.currency_exchange.entity.ExchangeRate;
import com.currency_exchange.exception.dao_exception.DaoException;
import com.currency_exchange.exception.dao_exception.ExchangeRateAlreadyExistsException;
import com.currency_exchange.repository.ExchangeRateQueries;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ExchangeRatesDao extends BaseDao<ExchangeRate> {
    public static final String FAILED_TO_SAVE_MESSAGE = "Failed to save exchangeRate";
    public static final String FAILED_TO_FIND_ALL_MESSAGE = "Failed to find exchangeRates";
    public static final String FAILED_TO_UPDATE_MESSAGE = "Failed to update exchangeRate";
    public static final String FAILED_TO_FIND_BY_IDS = "Failed to find exchange rate with ids %d -> %d";
    public static final String FAILED_TO_FIND_BY_ID = "Failed to find exchange rate with id %d";

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

    @Override
    public ExchangeRate saveAndSetId(ExchangeRate exchangeRate) {
        return executeInsertAndSetId(
                ExchangeRateQueries.SAVE_SQL,
                stmt -> {
                    stmt.setLong(1, exchangeRate.getBaseCurrencyId());
                    stmt.setLong(2, exchangeRate.getTargetCurrencyId());
                    stmt.setBigDecimal(3, exchangeRate.getRate());
                },
                exchangeRate,
                FAILED_TO_SAVE_MESSAGE
        );
    }

    @Override
    public List<ExchangeRate> findAll() {
        return executeQueryAndBuildList(
                ExchangeRateQueries.FIND_ALL_SQL,
                statement -> {
                },
                FAILED_TO_FIND_ALL_MESSAGE
        );
    }

    public Optional<ExchangeRate> update(ExchangeRate exchangeRate, BigDecimal newRate) {
        int rowsAffected = executeUpdate(
                ExchangeRateQueries.UPDATE_SQL,
                statement -> {
                    statement.setBigDecimal(1, newRate);
                    statement.setLong(2, exchangeRate.getId());
                },
                FAILED_TO_UPDATE_MESSAGE
        );

        return rowsAffected > 0 ? findById(exchangeRate.getId()) : Optional.empty();
    }

    public Optional<ExchangeRate> findByCurrencyIds(Long first, Long second) {
        return executeQueryAndBuildSingle(
                ExchangeRateQueries.FIND_BY_PAIR_SQL,
                statement -> {
                    statement.setLong(1, first);
                    statement.setLong(2, second);
                },
                FAILED_TO_FIND_BY_IDS.formatted(first, second)
        );
    }

    public Optional<ExchangeRate> findById(Long id) {
        return executeQueryAndBuildSingle(
                ExchangeRateQueries.FIND_BY_ID_SQL,
                statement -> statement.setLong(1, id),
                FAILED_TO_FIND_BY_ID.formatted(id)
        );
    }

    public Optional<ExchangeRate> findByUsd(Currency currency) {
        return findByCurrencyIds(USD_ID, currency.getId());
    }

    public boolean isExchangeRateExists(CurrencyPair pair) {
        Optional<ExchangeRate> exchangeRate = findByCurrencyIds(pair.base().getId(), pair.target().getId());
        return exchangeRate.isPresent();
    }

    public boolean isReversedExchangeRateExists(CurrencyPair pair) {
        Optional<ExchangeRate> reversed = findByCurrencyIds(pair.target().getId(), pair.base().getId());
        return reversed.isPresent();
    }

    public boolean isCrossCourseExists(CurrencyPair pair) {
        Optional<ExchangeRate> baseToCross = findByCurrencyIds(USD_ID, pair.base().getId());
        Optional<ExchangeRate> targetToCross = findByCurrencyIds(USD_ID, pair.target().getId());

        return baseToCross.isPresent() && targetToCross.isPresent();
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

    @Override
    protected void setEntityId(ExchangeRate entity, Long id) {
        entity.setId(id);
    }

    @Override
    protected DaoException createDuplicateKeyException(ExchangeRate entity, SQLException e) {
        return new ExchangeRateAlreadyExistsException("%s -> %s");
    }
}
