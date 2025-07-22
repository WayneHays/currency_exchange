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

    public static final String FIND_BY_ID_SQL = FIND_ALL_SQL + """
            WHERE id = ?
            """;

    public static final String UPDATE_SQL = """
            UPDATE exchange_rates
            SET rate = ?
            WHERE base_currency_id = ? AND target_currency_id = ?
            """;

    public static final String EXISTS_SQL = """
                SELECT EXISTS(
                SELECT 1 FROM exchange_rates
                WHERE base_currency_id = ? AND target_currency_id = ?
                )
            """;

    public static final String CROSS_COURSE_EXISTS_SQL = """
            SELECT EXISTS(
                SELECT 1 FROM exchange_rates er1
                WHERE er1.base_currency_id = ?
                AND EXISTS (
                    SELECT 1 FROM exchange_rates er2
                    WHERE er2.base_currency_id = er1.target_currency_id
                    AND er2.target_currency_id = ?
                )
            )
            """;


    private ExchangeRateQueries() {
    }

}
