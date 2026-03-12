package com.github.andrewdev.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    private static final String url = System.getenv("MYSQL_URL");
    private static final String user = "root";
    private static final String password = System.getenv("MYSQL_ROOT_PASSWORD");

    private DatabaseManager() {}

    public static Connection getConnection() throws SQLException {
        int maxRetries = 20;
        int delayMs = 1500;

        SQLException lastEx = null;

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                System.out.println("Attempt " + attempt + " to connect to MySQL...");
                Connection conn = DriverManager.getConnection(url, user, password);
                System.out.println("Connected successfully on attempt " + attempt);
                return conn;
            } catch (SQLException e) {
                lastEx = e;
                System.err.println("Connection failed (attempt " + attempt + "): " + e.getMessage());
                if (attempt < maxRetries) {
                    try { 
                        Thread.sleep(delayMs);
                    } catch (InterruptedException ie) { }
                }
            }
        }

        throw new SQLException("Failed to connect to MySQL after " + maxRetries + " attempts", lastEx);
    }
}
