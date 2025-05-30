// src/main/java/com/votingsystem/model/SystemLog.java
package com.votingsystem.model;

import java.sql.Timestamp;

/**
 * Represents a System Log entry for auditing and monitoring.
 * Corresponds to the SYSTEM_LOGS table in the database.
 */
public class SystemLog {
    private int logId;
    private Integer userId; // Nullable, as some logs might not be tied to a specific user
    private String action;
    private String details;
    private Timestamp logTimestamp;
    private String ipAddress;

    // Default constructor
    public SystemLog() {
    }

    // Constructor for creating a new log (without ID, timestamp)
    public SystemLog(Integer userId, String action, String details, String ipAddress) {
        this.userId = userId;
        this.action = action;
        this.details = details;
        this.ipAddress = ipAddress;
    }

    // Full constructor
    public SystemLog(int logId, Integer userId, String action, String details, Timestamp logTimestamp, String ipAddress) {
        this.logId = logId;
        this.userId = userId;
        this.action = action;
        this.details = details;
        this.logTimestamp = logTimestamp;
        this.ipAddress = ipAddress;
    }

    // Getters and Setters
    public int getLogId() {
        return logId;
    }

    public void setLogId(int logId) {
        this.logId = logId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Timestamp getLogTimestamp() {
        return logTimestamp;
    }

    public void setLogTimestamp(Timestamp logTimestamp) {
        this.logTimestamp = logTimestamp;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    @Override
    public String toString() {
        return "SystemLog{" +
               "logId=" + logId +
               ", userId=" + userId +
               ", action='" + action + '\'' +
               ", details='" + details + '\'' +
               ", logTimestamp=" + logTimestamp +
               ", ipAddress='" + ipAddress + '\'' +
               '}';
    }
}
