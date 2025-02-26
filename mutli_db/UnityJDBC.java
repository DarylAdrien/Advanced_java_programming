package mutli_db;

import java.sql.*;

public class UnityJDBC {
    public static void main(String[] args) {
        // UnityJDBC connection URL with configuration file

        try {
            // Load UnityJDBC Driver
            Class.forName("unity.jdbc.UnityDriver");
            
//            String unityJdbcUrl = "jdbc:unityjdbc:config=C:\\Users\\eselv\\eclipse-workspace\\AJP\\src\\unityconfig.xml";
            String unityJdbcUrl = "jdbc:unityjdbc:config=classpath:unityconfig.xml";


            // Establish a single connection
            Connection conn = DriverManager.getConnection(unityJdbcUrl, "", "");

            // Create statement
            Statement stmt = conn.createStatement();

            // Query both MySQL (XAMPP) and Oracle
            String query = "SELECT mysql.id, mysql.name, oracle.product " +
                           "FROM mysql_table mysql " +
                           "JOIN oracle_table oracle " +
                           "ON mysql.id = oracle.id";
            
            ResultSet rs = stmt.executeQuery(query);

            // Print results
            while (rs.next()) {
                System.out.println(rs.getInt("id") + " | " +
                                   rs.getString("name") + " | " +
                                   rs.getString("product"));
            }

            // Close resources
            rs.close();
            stmt.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
