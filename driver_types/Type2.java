package connection;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Type2 {
    public static void main(String[] args) {
        // Oracle database credentials
        String url = "jdbc:oracle:oci:@localhost:1521:xe";  // Using OCI (Type 2)
        String username = "daryl";
        String password = "daryl";

        try {
            // Load the Oracle OCI Driver (Type 2)
            Class.forName("oracle.jdbc.driver.OracleDriver");

            // Establish connection
            Connection connection = DriverManager.getConnection(url, username, password);
            System.out.println("Connected to Oracle Database using Type 2 (OCI Driver)!");

            // Close connection
            connection.close();

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
}
