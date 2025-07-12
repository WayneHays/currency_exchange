package com.currency_exchange.dao.sql;

public final class CurrencyQueries {
    public static final String SAVE_SQL = """
            INSERT INTO currencies (code, full_name, sign) VALUES (?,?,?)
            """;

    public static final String FIND_ALL_SQL = """
            SELECT id,
                code,
                full_name,
                sign
            FROM currencies
            """;

    public static final String FIND_BY_CODE_SQL = FIND_ALL_SQL + """
            WHERE code = ?
            """;

    public static final String FIND_BY_ID_SQL = FIND_ALL_SQL + """
            WHERE id = ?
            """;

    private CurrencyQueries() {
    }
}
