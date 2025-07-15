package com.currency_exchange.repository;

public final class ExchangeRateQueries {
    public static final String SAVE_SQL = """
            INSERT INTO exchange_rates (base_currency_id, target_currency_id, rate) VALUES (?,?,?)
            """;

    public static final String FIND_ALL_SQL = """
            SELECT id,
                base_currency_id,
                target_currency_id,
                rate
            FROM exchange_rates
            """;

    public static final String FIND_BY_IDS_SQL = FIND_ALL_SQL + """
            WHERE base_currency_id = ? AND target_currency_id = ?
            """;

    public static final String UPDATE_SQL = """
            UPDATE exchange_rates
            SET rate = ?
            WHERE base_currency_id = ? AND target_currency_id = ?
            RETURNING id, base_currency_id, target_currency_id, rate
            """;
    public static final String FIND_BY_ID_SQL = FIND_ALL_SQL + """
            WHERE id = ?
            """;

    private ExchangeRateQueries() {
    }

}
