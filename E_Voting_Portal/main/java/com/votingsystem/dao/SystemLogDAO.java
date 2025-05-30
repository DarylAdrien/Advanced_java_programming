// src/main/java/com/votingsystem/dao/SystemLogDAO.java
package com.votingsystem.dao;

import com.votingsystem.db.DBConnection;
import com.votingsystem.model.SystemLog;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for the SystemLog model.
 * Handles all database operations related to the SYSTEM_LOGS table.
 */
public class SystemLogDAO {

    /**
     * Inserts a new system log entry into the database.
     *
     * @param log The SystemLog object to insert.
     * @return The generated log ID if successful, -1 otherwise.
     */
    public int addSystemLog(SystemLog log) {
        String sql = "INSERT INTO SYSTEM_LOGS (LOG_ID, USER_ID, ACTION, DETAILS, IP_ADDRESS) VALUES (SYSTEM_LOGS_SEQ.NEXTVAL, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int generatedId = -1;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql, new String[]{"LOG_ID"});

            if (log.getUserId() != null) {
                pstmt.setInt(1, log.getUserId());
            } else {
                pstmt.setNull(1, java.sql.Types.NUMERIC); // For nullable USER_ID
            }
            pstmt.setString(2, log.getAction());
            pstmt.setString(3, log.getDetails());
            pstmt.setString(4, log.getIpAddress());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    generatedId = rs.getInt(1);
                    System.out.println("System log added successfully with ID: " + generatedId);
                } else {
                    try (Statement stmt2 = conn.createStatement();
                         ResultSet rs2 = stmt2.executeQuery("SELECT SYSTEM_LOGS_SEQ.CURRVAL FROM DUAL")) {
                        if (rs2.next()) {
                            generatedId = rs2.getInt(1);
                            System.out.println("System log added successfully (via CURRVAL) with ID: " + generatedId);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding system log: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeResources(rs, pstmt, conn);
        }
        return generatedId;
    }

    /**
     * Retrieves a system log entry by its ID.
     *
     * @param logId The ID of the system log to retrieve.
     * @return The SystemLog object if found, null otherwise.
     */
    public SystemLog getSystemLogById(int logId) {
        String sql = "SELECT LOG_ID, USER_ID, ACTION, DETAILS, LOG_TIMESTAMP, IP_ADDRESS FROM SYSTEM_LOGS WHERE LOG_ID = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        SystemLog log = null;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, logId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                log = new SystemLog();
                log.setLogId(rs.getInt("LOG_ID"));
                // Check for null before setting userId
                int userId = rs.getInt("USER_ID");
                if (!rs.wasNull()) {
                    log.setUserId(userId);
                }
                log.setAction(rs.getString("ACTION"));
                log.setDetails(rs.getString("DETAILS"));
                log.setLogTimestamp(rs.getTimestamp("LOG_TIMESTAMP"));
                log.setIpAddress(rs.getString("IP_ADDRESS"));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving system log by ID: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeResources(rs, pstmt, conn);
        }
        return log;
    }

    /**
     * Retrieves all system log entries, ordered by timestamp descending.
     *
     * @return A list of all SystemLog objects.
     */
    public List<SystemLog> getAllSystemLogs() {
        String sql = "SELECT LOG_ID, USER_ID, ACTION, DETAILS, LOG_TIMESTAMP, IP_ADDRESS FROM SYSTEM_LOGS ORDER BY LOG_TIMESTAMP DESC";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<SystemLog> logs = new ArrayList<>();

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                SystemLog log = new SystemLog();
                log.setLogId(rs.getInt("LOG_ID"));
                int userId = rs.getInt("USER_ID");
                if (!rs.wasNull()) {
                    log.setUserId(userId);
                }
                log.setAction(rs.getString("ACTION"));
                log.setDetails(rs.getString("DETAILS"));
                log.setLogTimestamp(rs.getTimestamp("LOG_TIMESTAMP"));
                log.setIpAddress(rs.getString("IP_ADDRESS"));
                logs.add(log);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving all system logs: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeResources(rs, pstmt, conn);
        }
        return logs;
    }

    /**
     * Retrieves system log entries for a specific user, ordered by timestamp descending.
     *
     * @param userId The ID of the user whose logs to retrieve.
     * @return A list of SystemLog objects for the specified user.
     */
    public List<SystemLog> getSystemLogsByUserId(int userId) {
        String sql = "SELECT LOG_ID, USER_ID, ACTION, DETAILS, LOG_TIMESTAMP, IP_ADDRESS FROM SYSTEM_LOGS WHERE USER_ID = ? ORDER BY LOG_TIMESTAMP DESC";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<SystemLog> logs = new ArrayList<>();

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                SystemLog log = new SystemLog();
                log.setLogId(rs.getInt("LOG_ID"));
                log.setUserId(rs.getInt("USER_ID")); // Will not be null here due to WHERE clause
                log.setAction(rs.getString("ACTION"));
                log.setDetails(rs.getString("DETAILS"));
                log.setLogTimestamp(rs.getTimestamp("LOG_TIMESTAMP"));
                log.setIpAddress(rs.getString("IP_ADDRESS"));
                logs.add(log);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving system logs by user ID: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeResources(rs, pstmt, conn);
        }
        return logs;
    }

    /**
     * Retrieves system log entries filtered by action, ordered by timestamp descending.
     *
     * @param action The action to filter by (e.g., "LOGIN_SUCCESS", "VOTE_CAST").
     * @return A list of SystemLog objects matching the specified action.
     */
    public List<SystemLog> getSystemLogsByAction(String action) {
        String sql = "SELECT LOG_ID, USER_ID, ACTION, DETAILS, LOG_TIMESTAMP, IP_ADDRESS FROM SYSTEM_LOGS WHERE ACTION = ? ORDER BY LOG_TIMESTAMP DESC";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<SystemLog> logs = new ArrayList<>();

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, action);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                SystemLog log = new SystemLog();
                log.setLogId(rs.getInt("LOG_ID"));
                int userId = rs.getInt("USER_ID");
                if (!rs.wasNull()) {
                    log.setUserId(userId);
                }
                log.setAction(rs.getString("ACTION"));
                log.setDetails(rs.getString("DETAILS"));
                log.setLogTimestamp(rs.getTimestamp("LOG_TIMESTAMP"));
                log.setIpAddress(rs.getString("IP_ADDRESS"));
                logs.add(log);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving system logs by action: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeResources(rs, pstmt, conn);
        }
        return logs;
    }
}
