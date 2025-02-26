package mutli_db;

import org.apache.calcite.jdbc.CalciteConnection;
import org.apache.calcite.tools.Frameworks;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.prepare.Prepare;

import java.sql.*;
import java.util.Properties;

public class apache_calcite {

    public static void main(String[] args) throws SQLException, Exception {
        // Set up connection properties for Calcite
        Properties info = new Properties();
//        info.setProperty("model", "file:///path/to/your/model.json");  // Path to your model file
        info.setProperty("model", "file:///C:/Users/eselv/eclipse-workspace/AJP/src/model.json");


        // Load JDBC driver for Calcite
        Class.forName("org.apache.calcite.jdbc.Driver");

        // Connect to Calcite using JDBC
        Connection calciteConnection = DriverManager.getConnection("jdbc:calcite:", info);

        // Create a statement for executing queries
        Statement statement = calciteConnection.createStatement();

        // Query to retrieve data from the MySQL schema
        String mysqlQuery = "SELECT * FROM MY_SCHEMA.mysql_table";  // Replace with your actual MySQL table
        ResultSet mysqlResultSet = statement.executeQuery(mysqlQuery);
        System.out.println("MySQL Data:");
        while (mysqlResultSet.next()) {
            System.out.println("ID: " + mysqlResultSet.getInt("id") + ", Name: " + mysqlResultSet.getString("name"));
        }

        // Query to retrieve data from the Oracle schema
        String oracleQuery = "SELECT * FROM MY_ORACLE_SCHEMA.oracle_table";  // Replace with your actual Oracle table
        ResultSet oracleResultSet = statement.executeQuery(oracleQuery);
        System.out.println("Oracle Data:");
        while (oracleResultSet.next()) {
            System.out.println("ID: " + oracleResultSet.getInt("id") + ", product: " + oracleResultSet.getString("product"));
        }

        // Close connections
        mysqlResultSet.close();
        oracleResultSet.close();
        statement.close();
        calciteConnection.close();
    }
}
