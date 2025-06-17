// src/com/votingsystem/service/AuthService.java
package com.votingsystem.service;

import com.votingsystem.model.User;
import com.votingsystem.util.DBConnection;
import com.votingsystem.util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Random;

public class AuthService {

    /**
     * Registers a new user.
     * @param user The User object containing registration details.
     * @param plainPassword The user's plaintext password.
     * @return The registered User object with generated ID, or null if registration fails.
     */
    public User registerUser(User user, String plainPassword) {
        String salt = PasswordUtil.generateSalt();
        String hashedPassword = PasswordUtil.hashPassword(plainPassword, salt);

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        User registeredUser = null;

        String sql = "INSERT INTO USERS2 (USER_ID, USERNAME, PASSWORD_HASH, SALT, EMAIL, PHONE_NUMBER, FULL_NAME, DATE_OF_BIRTH, ADDRESS, AADHAAR_NUMBER, ROLE_ID, CONSTITUENCY_ID, IS_ACTIVE, REGISTRATION_DATE) " +
                     "VALUES (USER_SEQ.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, SYSTIMESTAMP)";

        try {
            conn = DBConnection.getConnection();
            // Use RETURN_GENERATED_KEYS to get the new USER_ID (if your driver supports it, otherwise a separate select)
            // Oracle JDBC typically requires specifying the column name for generated keys, or a separate query.
            // For simplicity with USER_SEQ.NEXTVAL, we'll fetch the current sequence value after insertion.
            pstmt = conn.prepareStatement(sql, new String[]{"USER_ID"}); // This might need adjustment for Oracle 11g to return actual value

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, hashedPassword);
            pstmt.setString(3, salt);
            pstmt.setString(4, user.getEmail());
            pstmt.setString(5, user.getPhoneNumber());
            pstmt.setString(6, user.getFullName());
            pstmt.setDate(7, user.getDateOfBirth());
            pstmt.setString(8, user.getAddress());
            pstmt.setString(9, user.getAadhaarNumber());
            pstmt.setInt(10, user.getRoleId());
            // Handle nullable constituency_id
            if (user.getConstituencyId() != null) {
                pstmt.setInt(11, user.getConstituencyId());
            } else {
                pstmt.setNull(11, java.sql.Types.NUMERIC);
            }
            pstmt.setString(12, user.getIsActive());
//            pstmt.setTimestamp(12, new Timestamp(System.currentTimeMillis())); 
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                // Fetch the newly generated USER_ID
                // For Oracle, a common way is to query the sequence's CURRVAL after insertion
                Statement stmt = null;
                try {
                	rs = pstmt.getGeneratedKeys();
//                    stmt = conn.createStatement();
//                    rs = stmt.executeQuery("SELECT USER_SEQ2.CURRVAL FROM DUAL");
                    if (rs.next()) {
                    	int generatedUserId = rs.getInt(1); // Get the first (and usually only) generated key
                        user.setUserId(generatedUserId);
//                        user.setUserId(rs.getInt(1));
                        user.setPasswordHash(hashedPassword); // Set hash and salt to the returned user
                        user.setSalt(salt);
                        user.setRegistrationDate(new Timestamp(System.currentTimeMillis())); // Set current timestamp
                        registeredUser = user;
                        // Log registration
                        addLog(conn, registeredUser.getUserId(), "User Registration", "User " + registeredUser.getUsername() + " registered successfully.");
                    }
                } finally {
                    DBConnection.closeResultSet(rs);
                    DBConnection.closeStatement(stmt);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error registering user: " + e.getMessage());
            e.printStackTrace();
            // Log the error
            addLog(conn, null, "User Registration Failed", "Attempt to register user " + user.getUsername() + " failed: " + e.getMessage());
        } finally {
            DBConnection.closeResultSet(rs);
            DBConnection.closeStatement(pstmt);
            DBConnection.closeConnection(conn);
        }
        return registeredUser;
    }

    /**
     * Authenticates a user.
     * @param username The username provided by the user.
     * @param plainPassword The plaintext password provided by the user.
     * @return The authenticated User object (with role name), or null if authentication fails.
     */
    public User authenticateUser(String username, String plainPassword) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        User user = null;

        // Fetch user data including role name from ROLES table
        String sql = "SELECT u.USER_ID, u.USERNAME, u.PASSWORD_HASH, u.SALT, u.EMAIL, u.PHONE_NUMBER, u.FULL_NAME, " +
                     "u.DATE_OF_BIRTH, u.ADDRESS, u.AADHAAR_NUMBER, u.ROLE_ID, r.ROLE_NAME, u.CONSTITUENCY_ID, u.IS_ACTIVE, " +
                     "u.REGISTRATION_DATE, u.LAST_LOGIN_DATE " +
                     "FROM USERS2 u JOIN ROLES2 r ON u.ROLE_ID = r.ROLE_ID " +
                     "WHERE u.USERNAME = ? AND u.IS_ACTIVE = 'Y'";

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("PASSWORD_HASH");
                String storedSalt = rs.getString("SALT");

                if (PasswordUtil.verifyPassword(plainPassword, storedHash, storedSalt)) {
                    user = new User();
                    user.setUserId(rs.getInt("USER_ID"));
                    user.setUsername(rs.getString("USERNAME"));
                    user.setPasswordHash(storedHash); // Not strictly needed for return, but good for completeness
                    user.setSalt(storedSalt);
                    user.setEmail(rs.getString("EMAIL"));
                    user.setPhoneNumber(rs.getString("PHONE_NUMBER"));
                    user.setFullName(rs.getString("FULL_NAME"));
                    user.setDateOfBirth(rs.getDate("DATE_OF_BIRTH"));
                    user.setAddress(rs.getString("ADDRESS"));
                    user.setAadhaarNumber(rs.getString("AADHAAR_NUMBER"));
                    user.setRoleId(rs.getInt("ROLE_ID"));
                    user.setRoleName(rs.getString("ROLE_NAME"));
                    // Check if constituency_id is null before setting
                    int constituencyId = rs.getInt("CONSTITUENCY_ID");
                    if (!rs.wasNull()) { // Check if the last retrieved column was SQL NULL
                        user.setConstituencyId(constituencyId);
                    } else {
                        user.setConstituencyId(null);
                    }
                    user.setIsActive(rs.getString("IS_ACTIVE"));
                    user.setRegistrationDate(rs.getTimestamp("REGISTRATION_DATE"));
                    user.setLastLoginDate(rs.getTimestamp("LAST_LOGIN_DATE"));

                    // Update last login date
                    updateLastLogin(conn, user.getUserId());
                    user.setLastLoginDate(new Timestamp(System.currentTimeMillis())); // Update in object too
                    // Log successful login
                    addLog(conn, user.getUserId(), "User Login Success", "User " + username + " logged in.");
                } else {
                    // Password mismatch
                    System.out.println("Authentication failed for user: " + username + " (Invalid password)");
                    addLog(conn, null, "User Login Failed", "Invalid password for user " + username + ".");
                }
            } else {
                // Username not found or account inactive
                System.out.println("Authentication failed: User " + username + " not found or inactive.");
                addLog(conn, null, "User Login Failed", "Username " + username + " not found or inactive.");
            }
        } catch (SQLException e) {
            System.err.println("Error authenticating user: " + e.getMessage());
            e.printStackTrace();
            addLog(conn, null, "User Login Error", "Database error during login for user " + username + ": " + e.getMessage());
        } finally {
            DBConnection.closeResultSet(rs);
            DBConnection.closeStatement(pstmt);
            DBConnection.closeConnection(conn);
        }
        return user;
    }

    /**
     * Checks if a username or email already exists.
     * @param username The username to check.
     * @param email The email to check.
     * @param aadhaarNumber The Aadhaar number to check.
     * @return true if any exists, false otherwise.
     */
    public boolean checkIfUserExists(String username, String email, String aadhaarNumber) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean exists = false;

        String sql = "SELECT COUNT(*) FROM USERS2 WHERE USERNAME = ? OR EMAIL = ? OR AADHAAR_NUMBER = ?";

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
            System.err.println("Error checking user existence: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeResultSet(rs);
            DBConnection.closeStatement(pstmt);
            DBConnection.closeConnection(conn);
        }
        return exists;
    }

    /**
     * Updates the last login timestamp for a user.
     * @param conn An existing database connection (to allow for transactional logging)
     * @param userId The ID of the user.
     */
    private void updateLastLogin(Connection conn, int userId) {
        PreparedStatement pstmt = null;
        String sql = "UPDATE USERS2 SET LAST_LOGIN_DATE = SYSTIMESTAMP WHERE USER_ID = ?";
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating last login date for user " + userId + ": " + e.getMessage());
            e.printStackTrace();
            // This error shouldn't prevent login, but should be logged.
        } finally {
            DBConnection.closeStatement(pstmt);
            // Do NOT close the connection here, it's passed from authenticateUser
        }
    }

    
    public String generateAndStoreOTP(int userId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        String otpCode = generateOTPCode();
        String storedOtp = null;

        // Set OTP expiry time (e.g., 5 minutes from now)
        Calendar calendar = Calendar.getInstance();
        Timestamp generatedTime = new Timestamp(calendar.getTimeInMillis());
        calendar.add(Calendar.MINUTE, 5); // OTP valid for 5 minutes
        Timestamp expiryTime = new Timestamp(calendar.getTimeInMillis());

        // Mark any existing unexpired OTPs for this user as used/invalidated first (optional, but good for security)
        invalidateOldOTPs(userId);

        String sql = "INSERT INTO OTP_VERIFICATION2 (OTP_ID, USER_ID, OTP_CODE, GENERATED_TIME, EXPIRY_TIME, IS_USED) " +
                     "VALUES (OTP_SEQ2.NEXTVAL, ?, ?, ?, ?, 'N')";

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.setString(2, otpCode);
            pstmt.setTimestamp(3, generatedTime);
            pstmt.setTimestamp(4, expiryTime);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                storedOtp = otpCode;
                addLog(conn, userId, "OTP Generation Success", "OTP generated for user " + userId + ".");
            } else {
                System.err.println("Failed to insert OTP for user: " + userId);
                addLog(conn, userId, "OTP Generation Failed", "Failed to insert OTP for user " + userId + ".");
            }
        } catch (SQLException e) {
            System.err.println("Error generating and storing OTP for user " + userId + ": " + e.getMessage());
            e.printStackTrace();
            addLog(conn, userId, "OTP Generation Error", "Database error generating OTP for user " + userId + ": " + e.getMessage());
        } finally {
            DBConnection.closeStatement(pstmt);
            DBConnection.closeConnection(conn);
        }
        return storedOtp;
    }

    /**
     * Validates an OTP provided by the user.
     * @param userId The ID of the user.
     * @param otpCode The OTP entered by the user.
     * @return true if the OTP is valid and not used, false otherwise.
     */
    public boolean validateOTP(int userId, String otpCode) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean isValid = false;

        String sql = "SELECT OTP_ID, EXPIRY_TIME, IS_USED FROM OTP_VERIFICATION2 " +
                     "WHERE USER_ID = ? AND OTP_CODE = ? AND IS_USED = 'N' AND EXPIRY_TIME > SYSTIMESTAMP " +
                     "ORDER BY GENERATED_TIME DESC"; // Get the latest valid OTP

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.setString(2, otpCode);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                // OTP found, not used, and not expired
                int otpId = rs.getInt("OTP_ID");
                markOTPAsUsed(conn, otpId); // Mark it as used immediately after successful validation
                isValid = true;
                addLog(conn, userId, "OTP Validation Success", "User " + userId + " successfully validated OTP.");
            } else {
                addLog(conn, userId, "OTP Validation Failed", "User " + userId + " provided invalid or expired OTP.");
            }
        } catch (SQLException e) {
            System.err.println("Error validating OTP for user " + userId + ": " + e.getMessage());
            e.printStackTrace();
            addLog(conn, userId, "OTP Validation Error", "Database error validating OTP for user " + userId + ": " + e.getMessage());
        } finally {
            DBConnection.closeResultSet(rs);
            DBConnection.closeStatement(pstmt);
            DBConnection.closeConnection(conn);
        }
        return isValid;
    }

    /**
     * Marks an OTP as used in the database.
     * @param conn An existing database connection.
     * @param otpId The ID of the OTP to mark.
     */
    private void markOTPAsUsed(Connection conn, int otpId) {
        PreparedStatement pstmt = null;
        String sql = "UPDATE OTP_VERIFICATION2 SET IS_USED = 'Y' WHERE OTP_ID = ?";
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, otpId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error marking OTP " + otpId + " as used: " + e.getMessage());
            e.printStackTrace();
            // Log this, but don't stop the flow if the OTP was valid.
        } finally {
            DBConnection.closeStatement(pstmt);
            // Do NOT close the connection here, it's passed from validateOTP
        }
    }

    /**
     * Invalidates all unexpired OTPs for a given user.
     * This is useful before generating a new OTP to ensure only the latest is valid.
     * @param userId The ID of the user.
     */
    private void invalidateOldOTPs(int userId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        String sql = "UPDATE OTP_VERIFICATION2 SET IS_USED = 'Y' WHERE USER_ID = ? AND IS_USED = 'N' AND EXPIRY_TIME > SYSTIMESTAMP";
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error invalidating old OTPs for user " + userId + ": " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeStatement(pstmt);
            DBConnection.closeConnection(conn);
        }
    }

    /**
     * Generates a random 6-digit OTP code.
     * @return A string representation of the OTP code.
     */
    private String generateOTPCode() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000); // Generates a number between 100000 and 999999
        return String.valueOf(otp);
    }

    /**
     * Adds an entry to the APPLICATION_LOGS table.
     * This method is designed to be called internally within AuthService, potentially using an existing connection
     * or getting a new one if an external log request.
     * @param conn An existing connection, or null if a new connection should be opened.
     * @param userId The ID of the user performing the action (can be null for system logs).
     * @param action A brief description of the action (e.g., "Login Success").
     * @param details More detailed information about the action.
     */
    public void addLog(Connection conn, Integer userId, String action, String details) {
        Connection currentConn = conn;
        PreparedStatement pstmt = null;
        boolean closeConn = false;

        if (currentConn == null) {
            currentConn = DBConnection.getConnection();
            closeConn = true;
        }

        if (currentConn == null) {
            System.err.println("Failed to get DB connection for logging.");
            return;
        }

        String sql = "INSERT INTO APPLICATION_LOGS2 (LOG_ID, LOG_TIMESTAMP, USER_ID, ACTION, DETAILS, IP_ADDRESS) " +
                     "VALUES (LOG_SEQ.NEXTVAL, SYSTIMESTAMP, ?, ?, ?, ?)";
        try {
            pstmt = currentConn.prepareStatement(sql);
            if (userId != null) {
                pstmt.setInt(1, userId);
            } else {
                pstmt.setNull(1, java.sql.Types.NUMERIC);
            }
            pstmt.setString(2, action);
            pstmt.setString(3, details);
            pstmt.setString(4, "N/A"); // IP address will be captured in servlets; for now, N/A
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error adding log entry: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeStatement(pstmt);
            if (closeConn) {
                DBConnection.closeConnection(currentConn);
            }
        }
    }
}