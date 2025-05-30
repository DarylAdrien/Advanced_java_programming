// src/main/java/com/votingsystem/model/Election.java
package com.votingsystem.model;

import java.sql.Timestamp;

/**
 * Represents an Election in the voting system.
 * Corresponds to the ELECTIONS table in the database.
 * Status can be 'UPCOMING', 'ACTIVE', 'COMPLETED', 'CANCELLED'.
 */
public class Election {
    private int electionId;
    private String name;
    private String description;
    private Timestamp startDate;
    private Timestamp endDate;
    private String status; // 'UPCOMING', 'ACTIVE', 'COMPLETED', 'CANCELLED'
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Default constructor
    public Election() {
    }

    // Constructor for creating a new election (without ID, timestamps)
    public Election(String name, String description, Timestamp startDate, Timestamp endDate, String status) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    // Full constructor
    public Election(int electionId, String name, String description, Timestamp startDate, Timestamp endDate,
                    String status, Timestamp createdAt, Timestamp updatedAt) {
        this.electionId = electionId;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public int getElectionId() {
        return electionId;
    }

    public void setElectionId(int electionId) {
        this.electionId = electionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp getStartDate() {
        return startDate;
    }

    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }

    public Timestamp getEndDate() {
        return endDate;
    }

    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
        return "Election{" +
               "electionId=" + electionId +
               ", name='" + name + '\'' +
               ", description='" + description + '\'' +
               ", startDate=" + startDate +
               ", endDate=" + endDate +
               ", status='" + status + '\'' +
               ", createdAt=" + createdAt +
               ", updatedAt=" + updatedAt +
               '}';
    }
}
