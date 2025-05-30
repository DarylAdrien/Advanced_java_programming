// src/main/java/com/votingsystem/model/User.java
package com.votingsystem.model;

import java.sql.Timestamp;

/**
 * Represents a User in the voting system.
 * Corresponds to the USERS table in the database.
 * Roles can be 'VOTER', 'CANDIDATE', 'ADMIN'.
 */
public class User {
    private int userId;
    private String username;
    private String passwordHash; // Hashed password
    private String role;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String aadhaarNumber;
    private int constituencyId;
    private int isRegistered; // 0 for pending, 1 for approved
    private int isOtpEnabled; // 0 for disabled, 1 for enabled 2FA
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Default constructor
    public User() {
    }

    // Constructor for creating a new user (without userId, createdAt, updatedAt)
    public User(String username, String passwordHash, String role, String firstName, String lastName, String email,
                String phoneNumber, String aadhaarNumber, int constituencyId, int isRegistered, int isOtpEnabled) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.aadhaarNumber = aadhaarNumber;
        this.constituencyId = constituencyId;
        this.isRegistered = isRegistered;
        this.isOtpEnabled = isOtpEnabled;
    }

    // Full constructor
    public User(int userId, String username, String passwordHash, String role, String firstName, String lastName,
                String email, String phoneNumber, String aadhaarNumber, int constituencyId, int isRegistered,
                int isOtpEnabled, Timestamp createdAt, Timestamp updatedAt) {
        this.userId = userId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.aadhaarNumber = aadhaarNumber;
        this.constituencyId = constituencyId;
        this.isRegistered = isRegistered;
        this.isOtpEnabled = isOtpEnabled;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAadhaarNumber() {
        return aadhaarNumber;
    }

    public void setAadhaarNumber(String aadhaarNumber) {
        this.aadhaarNumber = aadhaarNumber;
    }

    public int getConstituencyId() {
        return constituencyId;
    }

    public void setConstituencyId(int constituencyId) {
        this.constituencyId = constituencyId;
    }

    public int getRegistered() {
        return isRegistered;
    }

    public void setRegistered(int registered) {
        isRegistered = registered;
    }

    public int getOtpEnabled() {
        return isOtpEnabled;
    }

    public void setOtpEnabled(int otpEnabled) {
        isOtpEnabled = otpEnabled;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "User{" +
               "userId=" + userId +
               ", username='" + username + '\'' +
               ", role='" + role + '\'' +
               ", firstName='" + firstName + '\'' +
               ", lastName='" + lastName + '\'' +
               ", email='" + email + '\'' +
               ", phoneNumber='" + phoneNumber + '\'' +
               ", aadhaarNumber='" + aadhaarNumber + '\'' +
               ", constituencyId=" + constituencyId +
               ", isRegistered=" + isRegistered +
               ", isOtpEnabled=" + isOtpEnabled +
               ", createdAt=" + createdAt +
               ", updatedAt=" + updatedAt +
               '}';
    }
}
