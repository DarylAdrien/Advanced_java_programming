package connection;

import java.sql.*;

import java.util.*;

public class Type4 {
    public static void main(String[] args) {
        // Database credentials
        String url = "jdbc:oracle:thin:@localhost:1521:xe"; // Change with your DB URL
        String username = "daryl";  // Replace with your username
        String password = "daryl";  // Replace with your password

        
        String createTableQuery = "CREATE TABLE VOTE_DETAILS ("
                + "VOTER_ID NUMBER PRIMARY KEY, "
                + "NAME VARCHAR2(50), "
                + "VOTED_TO VARCHAR2(10) )";
        
        Scanner scanner = new Scanner(System.in);
        // Establish connection
        try {
            // Load and register Oracle JDBC driver (Optional in newer versions of JDBC)
        	Class.forName("oracle.jdbc.driver.OracleDriver");

            // Get connection to the database
            Connection connection = DriverManager.getConnection(url, username, password);
            
            Statement statement = connection.createStatement();
         // Check if the table exists
            if (!isTableExists(connection, "VOTE_DETAILS")) {
                statement.executeUpdate(createTableQuery);
                System.out.println("Table 'VOTER_DETAILS' created successfully!");
            } else {
                System.out.println("Table 'VOTE_DETAILS' already exists. Skipping creation.");
            }
            
            while (true) {
                System.out.println("\nCRUD Operations:");
                System.out.println("1. Insert Record");
                System.out.println("2. Read Records");
                System.out.println("3. Update Record");
                System.out.println("4. Delete Record");
                System.out.println("5. Exit");
                System.out.print("Enter your choice: ");
                int choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        insertRecord(connection, scanner);
                        break;
                    case 2:
                        readRecords(connection);
                        break;
                    case 3:
                        updateRecord(connection, scanner);
                        break;
                    case 4:
                        deleteRecord(connection, scanner);
                        break;
                    case 5:
                        System.out.println("Exiting program...");
                        connection.close();
                        scanner.close();
                        return;
                    default:
                        System.out.println("Invalid choice! Please enter a valid option.");
                }
            }
        } 
        catch (SQLException | ClassNotFoundException e) 
        {
            e.printStackTrace();
        }
    }
    
    private static boolean isTableExists(Connection connection, String tableName) throws SQLException {
        String checkTableQuery = "SELECT table_name FROM user_tables WHERE table_name = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(checkTableQuery);
        preparedStatement.setString(1, tableName.toUpperCase());  // Table names are stored in uppercase
        ResultSet resultSet = preparedStatement.executeQuery();

        boolean exists = resultSet.next(); // If there is a result, the table exists
        resultSet.close();
        preparedStatement.close();
        return exists;
    }
    
    // CREATE: Insert a new record into VOTE_DETAILS
    private static void insertRecord(Connection connection, Scanner scanner) throws SQLException {
        System.out.print("Enter Voter ID: ");
        int voterId = scanner.nextInt();
        scanner.nextLine();  // Consume newline
        System.out.print("Enter Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Voted To: ");
        String votedTo = scanner.nextLine();

        String insertSQL = "INSERT INTO VOTE_DETAILS (VOTER_ID, NAME, VOTED_TO) VALUES (?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(insertSQL);
        preparedStatement.setInt(1, voterId);
        preparedStatement.setString(2, name);
        preparedStatement.setString(3, votedTo);

        int rowsInserted = preparedStatement.executeUpdate();
        if (rowsInserted > 0) {
            System.out.println("Record inserted successfully!");
        }
        preparedStatement.close();
    }

    // READ: Retrieve all records from VOTE_DETAILS
    private static void readRecords(Connection connection) throws SQLException {
        String selectSQL = "SELECT * FROM VOTE_DETAILS";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(selectSQL);

        boolean hasRecords = false;
        
        System.out.println("\nVOTER DETAILS:");
        while (resultSet.next()) {
            hasRecords = true; // At least one record exists
            int id = resultSet.getInt("VOTER_ID");
            String name = resultSet.getString("NAME");
            String votedTo = resultSet.getString("VOTED_TO");
            System.out.println("ID: " + id + ", Name: " + name + ", Voted To: " + votedTo);
        }

        if (!hasRecords) {
            System.out.println("No records found in VOTE_DETAILS.");
        }

        resultSet.close();
        statement.close();
    }

    // UPDATE: Modify an existing record in VOTE_DETAILS
    private static void updateRecord(Connection connection, Scanner scanner) throws SQLException {
        System.out.print("Enter Voter ID to update: ");
        int voterId = scanner.nextInt();
        scanner.nextLine();  // Consume newline
        System.out.print("Enter new Voted To: ");
        String newVotedTo = scanner.nextLine();

        String updateSQL = "UPDATE VOTE_DETAILS SET VOTED_TO = ? WHERE VOTER_ID = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(updateSQL);
        preparedStatement.setString(1, newVotedTo);
        preparedStatement.setInt(2, voterId);

        int rowsUpdated = preparedStatement.executeUpdate();
        if (rowsUpdated > 0) {
            System.out.println("Record updated successfully!");
        } else {
            System.out.println("No record found with Voter ID: " + voterId);
        }
        preparedStatement.close();
    }

    // DELETE: Remove a record from VOTE_DETAILS
    private static void deleteRecord(Connection connection, Scanner scanner) throws SQLException {
        System.out.print("Enter Voter ID to delete: ");
        int voterId = scanner.nextInt();

        String deleteSQL = "DELETE FROM VOTE_DETAILS WHERE VOTER_ID = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(deleteSQL);
        preparedStatement.setInt(1, voterId);

        int rowsDeleted = preparedStatement.executeUpdate();
        if (rowsDeleted > 0) {
            System.out.println("Record deleted successfully!");
        } else {
            System.out.println("No record found with Voter ID: " + voterId);
        }
        preparedStatement.close();
    }
}