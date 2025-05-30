// src/main/java/com/votingsystem/dao/UserDAO.java
package com.votingsystem.dao;

import com.votingsystem.db.DBConnection;

import com.votingsystem.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for the User model.
 * Handles all database operations related to the USERS table.
 */
public class UserDAO {

    /**
     * Inserts a new user into the database.
     *
     * @param user The User object to insert.
     * @return The generated user ID if successful, -1 otherwise.
     */
    public int addUser(User user) {
        String sql = "INSERT INTO USERS (USER_ID, USERNAME, PASSWORD_HASH, ROLE, FIRST_NAME, LAST_NAME, EMAIL, PHONE_NUMBER, AADHAAR_NUMBER, CONSTITUENCY_ID, IS_REGISTERED, IS_OTP_ENABLED) " +
                     "VALUES (USERS_SEQ.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int generatedId = -1;

        try {
            conn = DBConnection.getConnection();
            // Use Statement.RETURN_GENERATED_KEYS for auto-generated IDs if using other databases.
            // For Oracle with sequences, we fetch the current sequence value.
            pstmt = conn.prepareStatement(sql, new String[]{"USER_ID"}); // Specify the column name for generated keys

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPasswordHash());
            pstmt.setString(3, user.getRole());
            pstmt.setString(4, user.getFirstName());
            pstmt.setString(5, user.getLastName());
            pstmt.setString(6, user.getEmail());
            pstmt.setString(7, user.getPhoneNumber());
            pstmt.setString(8, user.getAadhaarNumber());
            // Handle nullable constituencyId
            if (user.getConstituencyId() > 0) {
                pstmt.setInt(9, user.getConstituencyId());
            } else {
                pstmt.setNull(9, java.sql.Types.NUMERIC);
            }
            pstmt.setInt(10, user.getRegistered() );
            pstmt.setInt(11, user.getOtpEnabled() );

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                // For Oracle, after inserting with NEXTVAL, you can get the current value of the sequence
                // or retrieve the generated key if the driver supports it for the specific setup.
                // The `new String[]{"USER_ID"}` in prepareStatement is the standard way,
                // but sometimes for Oracle, a separate query is needed for sequences.
                // Let's try getting generated keys first.
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    generatedId = rs.getInt(1);
                    System.out.println("User added successfully with ID: " + generatedId);
                } else {
                    // Fallback for Oracle if getGeneratedKeys doesn't work as expected for sequences
                    // This is less reliable as it might return a value incremented by another session
                    // if not handled carefully with transactions.
                    // A more robust way for Oracle is to use `SELECT USERS_SEQ.CURRVAL FROM DUAL;`
                    // immediately after the insert within the same transaction.
                    try (Statement stmt2 = conn.createStatement();
                         ResultSet rs2 = stmt2.executeQuery("SELECT USERS_SEQ.CURRVAL FROM DUAL")) {
                        if (rs2.next()) {
                            generatedId = rs2.getInt(1);
                            System.out.println("User added successfully (via CURRVAL) with ID: " + generatedId);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding user: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeResources(rs, pstmt, conn);
        }
        return generatedId;
    }

    /**
     * Retrieves a user by their username.
     *
     * @param username The username of the user to retrieve.
     * @return The User object if found, null otherwise.
     */
    public User getUserByUsername(String username) {
        String sql = "SELECT USER_ID, USERNAME, PASSWORD_HASH, ROLE, FIRST_NAME, LAST_NAME, EMAIL, PHONE_NUMBER, AADHAAR_NUMBER, CONSTITUENCY_ID, IS_REGISTERED, IS_OTP_ENABLED, CREATED_AT, UPDATED_AT FROM USERS WHERE USERNAME = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        User user = null;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                user = new User();
                user.setUserId(rs.getInt("USER_ID"));
                user.setUsername(rs.getString("USERNAME"));
                user.setPasswordHash(rs.getString("PASSWORD_HASH"));
                user.setRole(rs.getString("ROLE"));
                user.setFirstName(rs.getString("FIRST_NAME"));
                user.setLastName(rs.getString("LAST_NAME"));
                user.setEmail(rs.getString("EMAIL"));
                user.setPhoneNumber(rs.getString("PHONE_NUMBER"));
                user.setAadhaarNumber(rs.getString("AADHAAR_NUMBER"));
                user.setConstituencyId(rs.getInt("CONSTITUENCY_ID"));
                user.setRegistered(rs.getInt("IS_REGISTERED"));
                user.setOtpEnabled(rs.getInt("IS_OTP_ENABLED") );
                user.setCreatedAt(rs.getTimestamp("CREATED_AT"));
                user.setUpdatedAt(rs.getTimestamp("UPDATED_AT"));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving user by username: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeResources(rs, pstmt, conn);
        }
        return user;
    }

    /**
     * Retrieves a user by their user ID.
     *
     * @param userId The ID of the user to retrieve.
     * @return The User object if found, null otherwise.
     */
    public User getUserById(int userId) {
        String sql = "SELECT USER_ID, USERNAME, PASSWORD_HASH, ROLE, FIRST_NAME, LAST_NAME, EMAIL, PHONE_NUMBER, AADHAAR_NUMBER, CONSTITUENCY_ID, IS_REGISTERED, IS_OTP_ENABLED, CREATED_AT, UPDATED_AT FROM USERS WHERE USER_ID = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        User user = null;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                user = new User();
                user.setUserId(rs.getInt("USER_ID"));
                user.setUsername(rs.getString("USERNAME"));
                user.setPasswordHash(rs.getString("PASSWORD_HASH"));
                user.setRole(rs.getString("ROLE"));
                user.setFirstName(rs.getString("FIRST_NAME"));
                user.setLastName(rs.getString("LAST_NAME"));
                user.setEmail(rs.getString("EMAIL"));
                user.setPhoneNumber(rs.getString("PHONE_NUMBER"));
                user.setAadhaarNumber(rs.getString("AADHAAR_NUMBER"));
                user.setConstituencyId(rs.getInt("CONSTITUENCY_ID"));
                user.setRegistered(rs.getInt("IS_REGISTERED") );
                user.setOtpEnabled(rs.getInt("IS_OTP_ENABLED") );
                user.setCreatedAt(rs.getTimestamp("CREATED_AT"));
                user.setUpdatedAt(rs.getTimestamp("UPDATED_AT"));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving user by ID: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeResources(rs, pstmt, conn);
        }
        return user;
    }

    /**
     * Updates an existing user's information in the database.
     *
     * @param user The User object with updated information.
     * @return true if the user was updated successfully, false otherwise.
     */
    public boolean updateUser(User user) {
        String sql = "UPDATE USERS SET USERNAME = ?, PASSWORD_HASH = ?, ROLE = ?, FIRST_NAME = ?, LAST_NAME = ?, EMAIL = ?, PHONE_NUMBER = ?, AADHAAR_NUMBER = ?, CONSTITUENCY_ID = ?, IS_REGISTERED = ?, IS_OTP_ENABLED = ?, UPDATED_AT = CURRENT_TIMESTAMP WHERE USER_ID = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPasswordHash());
            pstmt.setString(3, user.getRole());
            pstmt.setString(4, user.getFirstName());
            pstmt.setString(5, user.getLastName());
            pstmt.setString(6, user.getEmail());
            pstmt.setString(7, user.getPhoneNumber());
            pstmt.setString(8, user.getAadhaarNumber());
            if (user.getConstituencyId() > 0) {
                pstmt.setInt(9, user.getConstituencyId());
            } else {
                pstmt.setNull(9, java.sql.Types.NUMERIC);
            }
            pstmt.setInt(10, user.getRegistered() );
            pstmt.setInt(11, user.getOtpEnabled() );
            pstmt.setInt(12, user.getUserId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("User with ID " + user.getUserId() + " updated successfully.");
                success = true;
            }
        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeResources(null, pstmt, conn);
        }
        return success;
    }

    /**
     * Deletes a user from the database by their user ID.
     *
     * @param userId The ID of the user to delete.
     * @return true if the user was deleted successfully, false otherwise.
     */
    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM USERS WHERE USER_ID = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("User with ID " + userId + " deleted successfully.");
                success = true;
            }
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeResources(null, pstmt, conn);
        }
        return success;
    }

    /**
     * Authenticates a user by username and password.
     * NOTE: This method performs a direct password comparison.
     * In a real application, you should hash the password before storing it
     * and compare the hashed password here using a secure hashing algorithm (e.g., BCrypt).
     *
     * @param username The username of the user.
     * @param password The plain text password.
     * @return The User object if authentication is successful, null otherwise.
     */
    public User authenticateUser(String username, String password) {
        // In a real application, 'password' here should be hashed and compared with passwordHash
        // For this example, we're doing a direct comparison for simplicity.
        // You should implement a secure password hashing mechanism (e.g., BCrypt).
        String sql = "SELECT USER_ID, USERNAME, PASSWORD_HASH, ROLE, FIRST_NAME, LAST_NAME, EMAIL, PHONE_NUMBER, AADHAAR_NUMBER, CONSTITUENCY_ID, IS_REGISTERED, IS_OTP_ENABLED, CREATED_AT, UPDATED_AT FROM USERS WHERE USERNAME = ? AND PASSWORD_HASH = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        User user = null;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password); // Compare with plain text password for now
            rs = pstmt.executeQuery();

            if (rs.next()) {
                user = new User();
                user.setUserId(rs.getInt("USER_ID"));
                user.setUsername(rs.getString("USERNAME"));
                user.setPasswordHash(rs.getString("PASSWORD_HASH"));
                user.setRole(rs.getString("ROLE"));
                user.setFirstName(rs.getString("FIRST_NAME"));
                user.setLastName(rs.getString("LAST_NAME"));
                user.setEmail(rs.getString("EMAIL"));
                user.setPhoneNumber(rs.getString("PHONE_NUMBER"));
                user.setAadhaarNumber(rs.getString("AADHAAR_NUMBER"));
                user.setConstituencyId(rs.getInt("CONSTITUENCY_ID"));
                user.setRegistered(rs.getInt("IS_REGISTERED") );
                user.setOtpEnabled(rs.getInt("IS_OTP_ENABLED") );
                user.setCreatedAt(rs.getTimestamp("CREATED_AT"));
                user.setUpdatedAt(rs.getTimestamp("UPDATED_AT"));
            }
        } catch (SQLException e) {
            System.err.println("Error authenticating user: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeResources(rs, pstmt, conn);
        }
        return user;
    }

    /**
     * Checks if a username or email already exists in the database.
     *
     * @param username The username to check.
     * @param email The email to check.
     * @return true if either username or email exists, false otherwise.
     */
    public boolean checkUserExists(String username, String email, String aadhaarNumber) {
        String sql = "SELECT COUNT(*) FROM USERS WHERE USERNAME = ? OR EMAIL = ? OR AADHAAR_NUMBER = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean exists = false;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, email);
            pstmt.setString(3, aadhaarNumber);
            rs = pstmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                exists = true;
            }
        } catch (SQLException e) {
            System.err.println("Error checking if user exists: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeResources(rs, pstmt, conn);
        }
        return exists;
    }

    /**
     * Retrieves all users from the database.
     *
     * @return A list of all User objects.
     */
    public List<User> getAllUsers() {
        String sql = "SELECT USER_ID, USERNAME, PASSWORD_HASH, ROLE, FIRST_NAME, LAST_NAME, EMAIL, PHONE_NUMBER, AADHAAR_NUMBER, CONSTITUENCY_ID, IS_REGISTERED, IS_OTP_ENABLED, CREATED_AT, UPDATED_AT FROM USERS";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<User> users = new ArrayList<>();

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("USER_ID"));
                user.setUsername(rs.getString("USERNAME"));
                user.setPasswordHash(rs.getString("PASSWORD_HASH")); // Still returning hash, but for display/admin purposes only
                user.setRole(rs.getString("ROLE"));
                user.setFirstName(rs.getString("FIRST_NAME"));
                user.setLastName(rs.getString("LAST_NAME"));
                user.setEmail(rs.getString("EMAIL"));
                user.setPhoneNumber(rs.getString("PHONE_NUMBER"));
                user.setAadhaarNumber(rs.getString("AADHAAR_NUMBER"));
                user.setConstituencyId(rs.getInt("CONSTITUENCY_ID"));
                user.setRegistered(rs.getInt("IS_REGISTERED") );
                user.setOtpEnabled(rs.getInt("IS_OTP_ENABLED") );
                user.setCreatedAt(rs.getTimestamp("CREATED_AT"));
                user.setUpdatedAt(rs.getTimestamp("UPDATED_AT"));
                users.add(user);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving all users: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeResources(rs, pstmt, conn);
        }
        return users;
    }

    /**
     * Retrieves users by their role.
     *
     * @param role The role to filter by (e.g., "VOTER", "CANDIDATE", "ADMIN").
     * @return A list of User objects matching the specified role.
     */
    public List<User> getUsersByRole(String role) {
        String sql = "SELECT USER_ID, USERNAME, PASSWORD_HASH, ROLE, FIRST_NAME, LAST_NAME, EMAIL, PHONE_NUMBER, AADHAAR_NUMBER, CONSTITUENCY_ID, IS_REGISTERED, IS_OTP_ENABLED, CREATED_AT, UPDATED_AT FROM USERS WHERE ROLE = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<User> users = new ArrayList<>();

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, role);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("USER_ID"));
                user.setUsername(rs.getString("USERNAME"));
                user.setPasswordHash(rs.getString("PASSWORD_HASH"));
                user.setRole(rs.getString("ROLE"));
                user.setFirstName(rs.getString("FIRST_NAME"));
                user.setLastName(rs.getString("LAST_NAME"));
                user.setEmail(rs.getString("EMAIL"));
                user.setPhoneNumber(rs.getString("PHONE_NUMBER"));
                user.setAadhaarNumber(rs.getString("AADHAAR_NUMBER"));
                user.setConstituencyId(rs.getInt("CONSTITUENCY_ID"));
                user.setRegistered(rs.getInt("IS_REGISTERED") );
                user.setOtpEnabled(rs.getInt("IS_OTP_ENABLED") );
                user.setCreatedAt(rs.getTimestamp("CREATED_AT"));
                user.setUpdatedAt(rs.getTimestamp("UPDATED_AT"));
                users.add(user);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving users by role: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeResources(rs, pstmt, conn);
        }
        return users;
    }
    public List<User> getUnregisteredUsers() {
        String sql = "SELECT * FROM USERS WHERE IS_REGISTERED = 0";
        // Same logic as other methods
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<User> users = new ArrayList<>();

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            
            rs = pstmt.executeQuery();

            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("USER_ID"));
                user.setUsername(rs.getString("USERNAME"));
                user.setPasswordHash(rs.getString("PASSWORD_HASH"));
                user.setRole(rs.getString("ROLE"));
                user.setFirstName(rs.getString("FIRST_NAME"));
                user.setLastName(rs.getString("LAST_NAME"));
                user.setEmail(rs.getString("EMAIL"));
                user.setPhoneNumber(rs.getString("PHONE_NUMBER"));
                user.setAadhaarNumber(rs.getString("AADHAAR_NUMBER"));
                user.setConstituencyId(rs.getInt("CONSTITUENCY_ID"));
                user.setRegistered(rs.getInt("IS_REGISTERED") );
                user.setOtpEnabled(rs.getInt("IS_OTP_ENABLED") );
                user.setCreatedAt(rs.getTimestamp("CREATED_AT"));
                user.setUpdatedAt(rs.getTimestamp("UPDATED_AT"));
                users.add(user);
            }
            if (users.isEmpty()) {
                System.out.println("No unregistered users found.");
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving users by role: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeResources(rs, pstmt, conn);
        }
        return users;
    }
}
