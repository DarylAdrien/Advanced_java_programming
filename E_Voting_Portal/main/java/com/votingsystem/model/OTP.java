// src/main/java/com/votingsystem/model/OTP.java
package com.votingsystem.model;

import java.sql.Timestamp;

/**
 * Represents an One-Time Password (OTP) for 2-step verification.
 * Corresponds to the OTPS table in the database.
 */
public class OTP {
    private int otpId;
    private int userId;
    private String otpCode;
    private Timestamp expiryTime;
    private boolean isUsed; // 0 for not used, 1 for used
    private Timestamp createdAt;

    // Default constructor
    public OTP() {
    }

    // Constructor for creating a new OTP (without ID, createdAt)
    public OTP(int userId, String otpCode, Timestamp expiryTime, boolean isUsed) {
        this.userId = userId;
        this.otpCode = otpCode;
        this.expiryTime = expiryTime;
        this.isUsed = isUsed;
    }

    // Full constructor
    public OTP(int otpId, int userId, String otpCode, Timestamp expiryTime, boolean isUsed, Timestamp createdAt) {
        this.otpId = otpId;
        this.userId = userId;
        this.otpCode = otpCode;
        this.expiryTime = expiryTime;
        this.isUsed = isUsed;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getOtpId() {
        return otpId;
    }

    public void setOtpId(int otpId) {
        this.otpId = otpId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getOtpCode() {
        return otpCode;
    }

    public void setOtpCode(String otpCode) {
        this.otpCode = otpCode;
    }

    public Timestamp getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(Timestamp expiryTime) {
        this.expiryTime = expiryTime;
    }

    public boolean isUsed() {
        return isUsed;
    }

    public void setUsed(boolean used) {
        isUsed = used;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "OTP{" +
               "otpId=" + otpId +
               ", userId=" + userId +
               ", otpCode='" + otpCode + '\'' +
               ", expiryTime=" + expiryTime +
               ", isUsed=" + isUsed +
               ", createdAt=" + createdAt +
               '}';
    }
}
