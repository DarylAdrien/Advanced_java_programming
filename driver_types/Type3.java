package connection;

import java.sql.Connection;


import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Type3 {

    public static void main(String[] args) {

        // JDBC URL format for Type 3 driver (example)
        // Replace this with the actual JDBC URL format for your Type 3 middleware driver
//        String url = "jdbc:middlewareprotocol://middlewarehost:port/database";
        String url = "jdbc:oracle:thin:@localhost:1521:xe";
        String user = "daryl";    // Your database username
        String password = "daryl"; // Your database password

        // Loading the Type 3 driver (Replace with the correct driver class name)
        try {
            // Example for DataDirect JDBC Type 3 driver, change this to your actual driver class name
        	Class.forName("oracle.jdbc.driver.OracleDriver"); // Replace with the actual driver class
        } catch (ClassNotFoundException e) {
            System.out.println("Driver not found: " + e);
            return;
        }

        // Establishing connection
        try (Connection connection = DriverManager.getConnection(url, user, password)) {

            // Create a statement object to send SQL queries
            Statement statement = connection.createStatement();

            // Execute a SQL query
            ResultSet resultSet = statement.executeQuery("SELECT * FROM TESTJAVA");

            // Processing the result set
            while (resultSet.next()) {
                System.out.println("Column1: " + resultSet.getString(1));
//                System.out.println("Column2: " + resultSet.getString(2));
                // You can add more columns as needed
            }

        } catch (SQLException e) {
            System.out.println("Error while connecting to the database: " + e.getMessage());
        }
    }
}
