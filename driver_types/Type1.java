package connection;

import java.sql.*;

public class Type1 {
    public static void main(String[] args) {
        try {
            // Load JDBC-ODBC Bridge Driver
            Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");

            // Connect to Oracle via ODBC
            Connection con = DriverManager.getConnection("jdbc:odbc:OracleDSN", "hr", "hr");

            // Execute Query
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM vote_details");

            // Print Results
            while (rs.next()) {
                System.out.println(rs.getInt("voter_id") + " " + rs.getString("name"));
            }

            // Close Connections
            rs.close();
            stmt.close();
            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}



//Type 1 (JDBC-ODBC Bridge) is deprecated and not supported in Java 8+.
