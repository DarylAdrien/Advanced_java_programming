// src/main/java/com/votingsystem/dao/OTPDAO.java
package com.votingsystem.dao;

import com.votingsystem.db.DBConnection;
import com.votingsystem.model.OTP;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Random;

/**
 * Data Access Object (DAO) for the OTP model.
 * Handles all database operations related to the OTPS table, including OTP generation and validation.
 */
public class OTPDAO {

    /**
     * Generates a new OTP for a given user and stores it in the database.
     * Invalidates any previous unexpired OTPs for the same user.
     *
     * @param userId The ID of the user for whom to generate the OTP.
     * @return The generated OTP code as a String, or null if an error occurs.
     */
    public String generateAndStoreOTP(int userId) {
        String otpCode = generateRandomOTP();
        // OTP valid for 5 minutes
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 5);
        Timestamp expiryTime = new Timestamp(calendar.getTimeInMillis());

        Connection conn = null;
        PreparedStatement pstmtUpdate = null;
        PreparedStatement pstmtInsert = null;
        ResultSet rs = null;
        int generatedId = -1;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // 1. Mark any existing unexpired OTPs for this user as used (or delete them)
            String updateSql = "UPDATE OTPS SET IS_USED = 1, EXPIRY_TIME = CURRENT_TIMESTAMP WHERE USER_ID = ? AND IS_USED = 0 AND EXPIRY_TIME > CURRENT_TIMESTAMP";
            pstmtUpdate = conn.prepareStatement(updateSql);
            pstmtUpdate.setInt(1, userId);
            pstmtUpdate.executeUpdate();

            // 2. Insert the new OTP
            String insertSql = "INSERT INTO OTPS (OTP_ID, USER_ID, OTP_CODE, EXPIRY_TIME, IS_USED) VALUES (OTPS_SEQ.NEXTVAL, ?, ?, ?, 0)";
            pstmtInsert = conn.prepareStatement(insertSql, new String[]{"OTP_ID"});
            pstmtInsert.setInt(1, userId);
            pstmtInsert.setString(2, otpCode);
            pstmtInsert.setTimestamp(3, expiryTime);

            int affectedRows = pstmtInsert.executeUpdate();

            if (affectedRows > 0) {
                rs = pstmtInsert.getGeneratedKeys();
                if (rs.next()) {
                    generatedId = rs.getInt(1);
                    System.out.println("New OTP generated and stored for user " + userId + " with ID: " + generatedId);
                } else {
                    try (Statement stmt2 = conn.createStatement();
                         ResultSet rs2 = stmt2.executeQuery("SELECT OTPS_SEQ.CURRVAL FROM DUAL")) {
                        if (rs2.next()) {
                            generatedId = rs2.getInt(1);
                            System.out.println("New OTP generated and stored for user " + userId + " (via CURRVAL) with ID: " + generatedId);
                        }
                    }
                }
                conn.commit(); // Commit transaction
                return otpCode;
            } else {
                conn.rollback(); // Rollback if insert fails
                return null;
            }
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback(); // Rollback on error
            } catch (SQLException ex) {
                System.err.println("Error during OTP transaction rollback: " + ex.getMessage());
            }
            System.err.println("Error generating and storing OTP: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true); // Reset auto-commit
            } catch (SQLException e) {
                System.err.println("Error resetting auto-commit: " + e.getMessage());
            }
            DBConnection.closeResources(rs, pstmtInsert, null);
            DBConnection.closeResources(null, pstmtUpdate, conn); // Close conn last
        }
    }

    /**
     * Validates an OTP provided by the user.
     * Marks the OTP as used upon successful validation.
     *
     * @param userId The ID of the user.
     * @param otpCode The OTP code to validate.
     * @return true if the OTP is valid and not expired/used, false otherwise.
     */
    public boolean validateOTP(int userId, String otpCode) {
        String sql = "SELECT OTP_ID, EXPIRY_TIME, IS_USED FROM OTPS WHERE USER_ID = ? AND OTP_CODE = ? AND IS_USED = 0 AND EXPIRY_TIME > CURRENT_TIMESTAMP";
        Connection conn = null;
        PreparedStatement pstmtSelect = null;
        PreparedStatement pstmtUpdate = null;
        ResultSet rs = null;
        boolean isValid = false;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction

            pstmtSelect = conn.prepareStatement(sql);
            pstmtSelect.setInt(1, userId);
            pstmtSelect.setString(2, otpCode);
            rs = pstmtSelect.executeQuery();

            if (rs.next()) {
                int otpId = rs.getInt("OTP_ID");
                Timestamp expiryTime = rs.getTimestamp("EXPIRY_TIME");
                boolean isUsed = (rs.getInt("IS_USED") == 1);

                // Check if OTP is still valid and not used
                if (expiryTime.after(new Timestamp(System.currentTimeMillis())) && !isUsed) {
                    // Mark OTP as used
                    String updateSql = "UPDATE OTPS SET IS_USED = 1 WHERE OTP_ID = ?";
                    pstmtUpdate = conn.prepareStatement(updateSql);
                    pstmtUpdate.setInt(1, otpId);
                    int affectedRows = pstmtUpdate.executeUpdate();

                    if (affectedRows > 0) {
                        isValid = true;
                        System.out.println("OTP " + otpCode + " validated successfully for user " + userId);
                        conn.commit(); // Commit transaction
                    } else {
                        System.out.println("Failed to mark OTP as used for user " + userId + ".");
                        conn.rollback(); // Rollback if update fails
                    }
                } else {
                    System.out.println("OTP for user " + userId + " is expired or already used.");
                    conn.rollback(); // Rollback if OTP is invalid
                }
            } else {
                System.out.println("OTP " + otpCode + " not found or invalid for user " + userId + ".");
                conn.rollback(); // Rollback if OTP not found
            }
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback(); // Rollback on error
            } catch (SQLException ex) {
                System.err.println("Error during OTP validation transaction rollback: " + ex.getMessage());
            }
            System.err.println("Error validating OTP: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true); // Reset auto-commit
            } catch (SQLException e) {
                System.err.println("Error resetting auto-commit: " + e.getMessage());
            }
            DBConnection.closeResources(rs, pstmtSelect, null);
            DBConnection.closeResources(null, pstmtUpdate, conn); // Close conn last
        }
        return isValid;
    }

    /**
     * Generates a random 6-digit OTP code.
     *
     * @return A 6-digit OTP code as a String.
     */
    private String generateRandomOTP() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000); // Generates a 6-digit number
        return String.valueOf(otp);
    }
}
