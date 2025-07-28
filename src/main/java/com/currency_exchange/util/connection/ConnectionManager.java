package com.currency_exchange.util.connection;

import com.currency_exchange.util.PropertiesUtil;
import com.currency_exchange.util.database.DatabaseSchema;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public final class ConnectionManager {
    private static final String URL_KEY = "db.url";
    private static final String POOL_SIZE_KEY = "db.pool.size";
    private static final Integer DEFAULT_POOL_SIZE = 5;
    private static final String DRIVER_CLASS_PATH = "org.sqlite.JDBC";
    private static final String DATABASE_INIT_ERROR_MESSAGE = "Database initialization failed";

    private static BlockingQueue<Connection> pool;
    private static boolean databaseInitialized = false;

    public static Connection get() {
        try {
            return pool.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    static {
        loadDriver();
        initDatabase();
        initConnectionPool();
    }

    private ConnectionManager() {
    }

    private static void loadDriver() {
        try {
            Class.forName(DRIVER_CLASS_PATH);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static void initDatabase() {
        if (!databaseInitialized) {
            try {
                if (!databaseExists()) {
                    createTablesAndData();
                }
                databaseInitialized = true;
            } catch (SQLException e) {
                throw new RuntimeException(DATABASE_INIT_ERROR_MESSAGE, e);
            }
        }
    }

    private static boolean databaseExists() {
        try (Connection connection = open();
             var statement = connection.createStatement()) {
            statement.executeQuery(DatabaseSchema.DATABASE_EXISTS_CHECK);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    private static void createTablesAndData() throws SQLException {
        try (Connection connection = open();
             var statement = connection.createStatement()) {
            createTables(statement);
            insertInitialData(statement);
        }
    }

    private static void createTables(Statement statement) throws SQLException {
        statement.execute(DatabaseSchema.CREATE_CURRENCIES_TABLE);
        statement.execute(DatabaseSchema.CREATE_EXCHANGE_RATES_TABLE);
    }

    private static void insertInitialData(Statement statement) throws SQLException {
        statement.execute(DatabaseSchema.INSERT_DATA_INTO_CURRENCIES);
        statement.execute(DatabaseSchema.INSERT_DATA_INTO_EXCHANGE_RATES);
    }

    private static void initConnectionPool() {
        String poolSize = PropertiesUtil.get(POOL_SIZE_KEY);
        int size = poolSize == null ? DEFAULT_POOL_SIZE : Integer.parseInt(poolSize);
        pool = new ArrayBlockingQueue<>(size);

        for (int i = 0; i < size; i++) {
            Connection connection = open();
            Connection proxyConnection = wrapConnection(connection);
            pool.add(proxyConnection);
        }
    }

    private static Connection open() {
        try {
            return DriverManager.getConnection(PropertiesUtil.get(URL_KEY));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static Connection wrapConnection(Connection connection) {
        return (Connection) Proxy.newProxyInstance(
                Connection.class.getClassLoader(),
                new Class[]{Connection.class},
                (proxy, method, args) -> "close".equals(method.getName())
                        ? pool.add((Connection) proxy)
                        : method.invoke(connection, args)
        );
    }
}
