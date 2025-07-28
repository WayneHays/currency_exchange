package com.currency_exchange.util.database;

public final class DatabaseSchema {

    public static final String DATABASE_EXISTS_CHECK = "SELECT 1 FROM currencies LIMIT 1";

    public static final String CREATE_CURRENCIES_TABLE = """
            CREATE TABLE currencies
             (
                 id        INTEGER NOT NULL PRIMARY KEY,
                 code      VARCHAR NOT NULL UNIQUE,
                 full_name VARCHAR NOT NULL,
                 sign      VARCHAR NOT NULL
             )
            """;

    public static final String CREATE_EXCHANGE_RATES_TABLE = """
            CREATE TABLE exchange_rates
             (
                 id                 INTEGER    NOT NULL PRIMARY KEY,
                 base_currency_id   INTEGER    NOT NULL REFERENCES currencies (id),
                 target_currency_id INTEGER    NOT NULL REFERENCES currencies (id),
                 rate               DECIMAL(6) NOT NULL,
                 UNIQUE (base_currency_id, target_currency_id)
             )
            """;

    public static final String INSERT_DATA_INTO_CURRENCIES = """
            INSERT INTO currencies(code, full_name, sign)
            VALUES ('RUB', 'Russian Ruble', '₽'),
                   ('USD', 'US Dollar', '$'),
                   ('EUR', 'Euro', '€');
            """;

    public static final String INSERT_DATA_INTO_EXCHANGE_RATES = """
            INSERT INTO exchange_rates(base_currency_id, target_currency_id, rate)
            VALUES (1,2,0.0127),
                   (1,3,0.0109),
                   (2,3,0.8613);
            """;

    private DatabaseSchema() {
    }
}
