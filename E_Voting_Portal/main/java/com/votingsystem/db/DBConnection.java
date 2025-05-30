// src/main/java/com/votingsystem/db/DBConnection.java
package com.votingsystem.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utility class for managing database connections to Oracle.
 * This class provides a static method to get a database connection.
 */
public class DBConnection {

    // Database connection parameters
    // IMPORTANT: Replace these with your actual Oracle database details
    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521:XE"; // Example: @hostname:port:SID
    private static final String DB_USER = "daryl"; // Your Oracle username
    private static final String DB_PASSWORD = "daryl"; // Your Oracle password

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private DBConnection() {
        // Utility class
    }

    /**
     * Establishes and returns a connection to the Oracle database.
     *
     * @return A Connection object to the database.
     * @throws SQLException If a database access error occurs or the URL is null.
     */
    public static Connection getConnection() throws SQLException {
        Connection connection = null;
        try {
            // Register the Oracle JDBC driver (not strictly necessary for modern JDBC, but good practice)
            Class.forName("oracle.jdbc.driver.OracleDriver");
            // Get the connection
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("Database connection established successfully.");
        } catch (ClassNotFoundException e) {
            System.err.println("Oracle JDBC Driver not found. Make sure ojdbcX.jar is in your classpath.");
            throw new SQLException("Oracle JDBC Driver not found.", e);
        } catch (SQLException e) {
            System.err.println("Failed to establish database connection: " + e.getMessage());
            throw e; // Re-throw the exception for calling methods to handle
        }
        return connection;
    }

    /**
     * Closes the given database connection, statement, and result set.
     * This method handles null checks to prevent NullPointerExceptions.
     *
     * @param rs The ResultSet to close (can be null).
     * @param stmt The Statement to close (can be null).
     * @param conn The Connection to close (can be null).
     */
    public static void closeResources(java.sql.ResultSet rs, java.sql.Statement stmt, Connection conn) {
        try {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing database resources: " + e.getMessage());
            // Log this error, but don't re-throw as it's a cleanup method
        }
    }
}
