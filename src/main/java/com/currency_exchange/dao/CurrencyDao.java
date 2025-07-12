package com.currency_exchange.dao;

import com.currency_exchange.dao.sql.CurrencyQueries;
import com.currency_exchange.entity.Currency;
import com.currency_exchange.exception.dao_exception.CurrencyAlreadyExistsException;
import com.currency_exchange.exception.dao_exception.DaoException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class CurrencyDao extends BaseDao<Currency> {

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
                "Failed to save currency"
        );
    }

    @Override
    public List<Currency> findAll() throws DaoException {
        return executeQueryAndBuildList(
                CurrencyQueries.FIND_ALL_SQL,
                stmt -> {
                },
                "Failed to find currencies"
        );
    }

    public Optional<Currency> findByCode(String code) {
        return executeQueryAndBuildSingle(
                CurrencyQueries.FIND_BY_CODE_SQL,
                stmt -> stmt.setString(1, code.toUpperCase()),
                "Failed to find currency with code %s".formatted(code)
        );
    }

    public Optional<Currency> findById(Long id) {
        return executeQueryAndBuildSingle(
                CurrencyQueries.FIND_BY_ID_SQL,
                stmt -> stmt.setLong(1, id),
                "Failed to find currency with id %d".formatted(id)
        );
    }


    @Override
    protected Currency buildEntity(ResultSet resultSet) throws SQLException {
        return new Currency(
                resultSet.getLong("id"),
                resultSet.getString("code"),
                resultSet.getString("full_name"),
                resultSet.getString("sign")
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
