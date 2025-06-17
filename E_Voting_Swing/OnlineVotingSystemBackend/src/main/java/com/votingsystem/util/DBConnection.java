// src/com/votingsystem/util/DBConnection.java
package com.votingsystem.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    // Oracle 11g connection details
    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521:XE"; // Replace XE with your SID
    private static final String DB_USER = "daryl"; // e.g., SYSTEM, HR, or a dedicated user
    private static final String DB_PASSWORD = "daryl"; // Password for your database user

    static {
        try {
            // Register the Oracle JDBC driver
            Class.forName("oracle.jdbc.driver.OracleDriver");
            System.out.println("Oracle JDBC Driver Registered!");
        } catch (ClassNotFoundException e) {
            System.err.println("Error: Oracle JDBC Driver not found!");
            e.printStackTrace();
        }
    }

    /**
     * Establishes a new database connection.
     * @return A valid Connection object, or null if an error occurs.
     */
    public static Connection getConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            // System.out.println("Database connection established!"); // Uncomment for debugging
        } catch (SQLException e) {
            System.err.println("Error establishing database connection:");
            e.printStackTrace();
            // Handle specific SQL exceptions as needed
        }
        return connection;
    }

    /**
     * Closes the database connection.
     * @param connection The Connection object to close.
     */
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
                // System.out.println("Database connection closed."); // Uncomment for debugging
            } catch (SQLException e) {
                System.err.println("Error closing database connection:");
                e.printStackTrace();
            }
        }
    }

    /**
     * Closes a Statement object.
     * @param statement The Statement object to close.
     */
    public static void closeStatement(java.sql.Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                System.err.println("Error closing statement:");
                e.printStackTrace();
            }
        }
    }

    /**
     * Closes a ResultSet object.
     * @param resultSet The ResultSet object to close.
     */
    public static void closeResultSet(java.sql.ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                System.err.println("Error closing result set:");
                e.printStackTrace();
            }
        }
    }

    // You can add a main method for quick testing of connection
    public static void main(String[] args) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            if (conn != null) {
                System.out.println("Successfully connected to the database!");
            } else {
                System.out.println("Failed to connect to the database.");
            }
        } finally {
            DBConnection.closeConnection(conn);
        }
    }
}