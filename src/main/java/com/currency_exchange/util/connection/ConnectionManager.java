package com.currency_exchange.util.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConnectionManager {

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private ConnectionManager() {
    }

    public static Connection open() {
        try {
            return DriverManager.getConnection("jdbc:sqlite:C:\\Users\\Veretennikov\\Desktop\\for_idea\\currency_exchange.sqlite");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
