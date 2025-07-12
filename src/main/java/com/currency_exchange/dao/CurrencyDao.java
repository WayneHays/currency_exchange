package com.currency_exchange.dao;

import com.currency_exchange.entity.Currency;
import com.currency_exchange.exception.dao_exception.CurrencyAlreadyExistsException;
import com.currency_exchange.exception.dao_exception.DaoException;
import com.currency_exchange.repository.CurrencyQueries;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class CurrencyDao extends BaseDao<Currency> {
    public static final String FAILED_TO_SAVE_MESSAGE = "Failed to save currency";
    public static final String FAILED_TO_FIND_ALL_MESSAGE = "Failed to find currencies";
    public static final String FAILED_TO_FIND_BY_CODE_MESSAGE = "Failed to find currency with code %s";
    public static final String FAILED_TO_FIND_BY_ID = "Failed to find currency with id %d";
    public static final String ID = "id";
    public static final String CODE = "code";
    public static final String FULL_NAME = "full_name";
    public static final String SIGN = "sign";

    private static final CurrencyDao INSTANCE = new CurrencyDao();

    private CurrencyDao() {
    }

    public static CurrencyDao getInstance() {
        return INSTANCE;
    }

    @Override
    public Currency saveAndSetId(Currency currency) {
        return executeInsertAndSetId(
                CurrencyQueries.SAVE_SQL,
                statement -> {
                    statement.setString(1, currency.getCode());
                    statement.setString(2, currency.getFullName());
                    statement.setString(3, currency.getSign());
                },
                currency,
                FAILED_TO_SAVE_MESSAGE
        );
    }

    @Override
    public List<Currency> findAll() throws DaoException {
        return executeQueryAndBuildList(
                CurrencyQueries.FIND_ALL_SQL,
                stmt -> {
                },
                FAILED_TO_FIND_ALL_MESSAGE
        );
    }

    public Optional<Currency> findByCode(String code) {
        return executeQueryAndBuildSingle(
                CurrencyQueries.FIND_BY_CODE_SQL,
                stmt -> stmt.setString(1, code.toUpperCase()),
                FAILED_TO_FIND_BY_CODE_MESSAGE.formatted(code)
        );
    }

    public Optional<Currency> findById(Long id) {
        return executeQueryAndBuildSingle(
                CurrencyQueries.FIND_BY_ID_SQL,
                stmt -> stmt.setLong(1, id),
                FAILED_TO_FIND_BY_ID.formatted(id)
        );
    }


    @Override
    protected Currency buildEntity(ResultSet resultSet) throws SQLException {
        return new Currency(
                resultSet.getLong(ID),
                resultSet.getString(CODE),
                resultSet.getString(FULL_NAME),
                resultSet.getString(SIGN)
        );
    }

    @Override
    protected void setEntityId(Currency entity, Long id) {
        entity.setId(id);
    }

    @Override
    protected DaoException createDuplicateKeyException(Currency entity, SQLException e) {
        return new CurrencyAlreadyExistsException(entity.getCode(), e);
    }
}
