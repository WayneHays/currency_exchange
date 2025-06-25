package com.currency_exchange;

import java.sql.DriverManager;
import java.sql.SQLException;

public class DBTest {
    public static void main(String[] args) {
        try {
            java.sql.Driver driver = new org.sqlite.JDBC();
            DriverManager.registerDriver(driver);
            System.out.println("SQLite driver registered successfully");
        } catch (SQLException e) {
            System.out.println("Failed to register SQLite driver: " + e.getMessage());
        }
    }
}
