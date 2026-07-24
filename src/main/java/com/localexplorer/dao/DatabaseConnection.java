package com.localexplorer.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton Pattern: only one connection object is ever created and reused
 * across the whole app.
 *
 * >>> EDIT THESE THREE VALUES to match your local MySQL setup <<<
 */
public class DatabaseConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/local_explorer_ai?useSSL=false&serverTimezone=UTC";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "Shiva@2008";

    private static DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("Failed to connect to database. "
                    + "Check MySQL is running and credentials in DatabaseConnection.java are correct.", e);
        }
    }

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            }
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("Lost database connection.", e);
        }
        return connection;
    }
}
