package com.currency_exchange.repository;

public final class CurrencyQueries {
    public static final String SAVE_SQL = """
            INSERT INTO currencies (param, full_name, sign) VALUES (?,?,?)
            """;

    public static final String FIND_ALL_SQL = """
            SELECT id,
                param,
                full_name,
                sign
            FROM currencies
            """;

    public static final String FIND_BY_CODE_SQL = FIND_ALL_SQL + """
            WHERE param = ?
            """;

    public static final String FIND_BY_ID_SQL = FIND_ALL_SQL + """
            WHERE id = ?
            """;

    public static final String FIND_BY_IDS_SQL = FIND_ALL_SQL + """
            WHERE id IN (%s)
            """;

    private CurrencyQueries() {
    }
}
