package mutli_db;

import java.sql.*;

public class Direct_method {
    public static void main(String[] args) {
        // JDBC URLs
        String mysqlUrl = "jdbc:mysql://localhost:3306/mysql_db"; // Replace with your MySQL database
        String oracleUrl = "jdbc:oracle:thin:@localhost:1521:xe"; // Replace with your Oracle database

        // Database credentials
        String mysqlUser = "root";
        String mysqlPassword = "";

        String oracleUser = "daryl";
        String oraclePassword = "daryl";

        Connection mysqlConn = null;
        Connection oracleConn = null;
        Statement mysqlStmt = null;
        Statement oracleStmt = null;
        ResultSet mysqlRs = null;
        ResultSet oracleRs = null;

        try {
            // Load MySQL and Oracle JDBC drivers
            Class.forName("com.mysql.cj.jdbc.Driver");
            Class.forName("oracle.jdbc.driver.OracleDriver");

            // Establish connections
            mysqlConn = DriverManager.getConnection(mysqlUrl, mysqlUser, mysqlPassword);
            oracleConn = DriverManager.getConnection(oracleUrl, oracleUser, oraclePassword);

            // Execute queries on both databases
            mysqlStmt = mysqlConn.createStatement();
            oracleStmt = oracleConn.createStatement();

            mysqlRs = mysqlStmt.executeQuery("SELECT * FROM mysql_table");  // Replace with your MySQL table name
            oracleRs = oracleStmt.executeQuery("SELECT * FROM oracle_table"); // Replace with your Oracle table name

            // Process MySQL results
            System.out.println("MySQL Table Data:");
            while (mysqlRs.next()) {
                System.out.println(mysqlRs.getString(1) + " | " + mysqlRs.getString(2));
            }

            // Process Oracle results
            System.out.println("\nOracle Table Data:");
            while (oracleRs.next()) {
                System.out.println(oracleRs.getString(1) + " | " + oracleRs.getString(2));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Close resources
            try {
                if (mysqlRs != null) mysqlRs.close();
                if (oracleRs != null) oracleRs.close();
                if (mysqlStmt != null) mysqlStmt.close();
                if (oracleStmt != null) oracleStmt.close();
                if (mysqlConn != null) mysqlConn.close();
                if (oracleConn != null) oracleConn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
