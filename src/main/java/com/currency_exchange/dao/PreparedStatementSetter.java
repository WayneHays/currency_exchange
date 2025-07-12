package com.currency_exchange.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@FunctionalInterface
public interface PreparedStatementSetter {
    void setParameters(PreparedStatement statement) throws SQLException;
}
