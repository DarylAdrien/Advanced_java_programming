// src/com/votingsystem/model/User.java
package com.votingsystem.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class User implements Serializable {
    private static final long serialVersionUID = 1L; // Recommended for Serializable

    private int userId;
    private String username;
    private String passwordHash; // Stored hash, not plaintext password
    private String salt;
    private String email;
    private String phoneNumber;
    private String fullName;
    private java.sql.Date dateOfBirth; // Use java.sql.Date for database DATE type
    private String address;
    private String aadhaarNumber;
    private int roleId;
    private String roleName; // To easily retrieve role name
    private Integer constituencyId; // Use Integer for nullable foreign keys
    private String isActive; // 'Y' or 'N'
    private Timestamp registrationDate;
    private Timestamp lastLoginDate;

    // Constructors
    public User() {
    }

    // Constructor for registration (without userId, hash, salt, timestamps)
    public User(String username, String email, String phoneNumber, String fullName,
                java.sql.Date dateOfBirth, String address, String aadhaarNumber,
                int roleId, Integer constituencyId) {
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.fullName = fullName;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.aadhaarNumber = aadhaarNumber;
        this.roleId = roleId;
        this.constituencyId = constituencyId;
        this.isActive = "Y"; // Default to active upon registration
    }

    // Full constructor (useful when retrieving from DB)
    public User(int userId, String username, String passwordHash, String salt, String email,
                String phoneNumber, String fullName, java.sql.Date dateOfBirth, String address,
                String aadhaarNumber, int roleId, Integer constituencyId, String isActive,
                Timestamp registrationDate, Timestamp lastLoginDate) {
        this.userId = userId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.fullName = fullName;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.aadhaarNumber = aadhaarNumber;
        this.roleId = roleId;
        this.constituencyId = constituencyId;
        this.isActive = isActive;
        this.registrationDate = registrationDate;
        this.lastLoginDate = lastLoginDate;
    }

    // Getters and Setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getSalt() { return salt; }
    public void setSalt(String salt) { this.salt = salt; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public java.sql.Date getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(java.sql.Date dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getAadhaarNumber() { return aadhaarNumber; }
    public void setAadhaarNumber(String aadhaarNumber) { this.aadhaarNumber = aadhaarNumber; }

    public int getRoleId() { return roleId; }
    public void setRoleId(int roleId) { this.roleId = roleId; }

    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }

    public Integer getConstituencyId() { return constituencyId; }
    public void setConstituencyId(Integer constituencyId) { this.constituencyId = constituencyId; }

    public String getIsActive() { return isActive; }
    public void setIsActive(String isActive) { this.isActive = isActive; }

    public Timestamp getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(Timestamp registrationDate) { this.registrationDate = registrationDate; }

    public Timestamp getLastLoginDate() { return lastLoginDate; }
    public void setLastLoginDate(Timestamp lastLoginDate) { this.lastLoginDate = lastLoginDate; }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", fullName='" + fullName + '\'' +
                ", roleId=" + roleId +
                ", roleName='" + roleName + '\'' +
                '}';
    }
}