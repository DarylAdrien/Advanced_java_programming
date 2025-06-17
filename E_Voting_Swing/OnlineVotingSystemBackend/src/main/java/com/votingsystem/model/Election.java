// src/com/votingsystem/model/Election.java
package com.votingsystem.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class Election implements Serializable {
    private static final long serialVersionUID = 1L;

    private int electionId;
    private String electionName;
    private String description;
    private Timestamp startDateTime;
    private Timestamp endDateTime;
    private String status; // SCHEDULED, ACTIVE, COMPLETED, CANCELLED
    private int createdBy; // User ID of the admin who created it
    private String createdByName; // Optional: To display admin's name
    private Timestamp createdDate;

    public Election() {
    }

    public Election(int electionId, String electionName, String description,
                    Timestamp startDateTime, Timestamp endDateTime, String status,
                    int createdBy, Timestamp createdDate) {
        this.electionId = electionId;
        this.electionName = electionName;
        this.description = description;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.status = status;
        this.createdBy = createdBy;
        this.createdDate = createdDate;
    }

    // Getters and Setters
    public int getElectionId() { return electionId; }
    public void setElectionId(int electionId) { this.electionId = electionId; }

    public String getElectionName() { return electionName; }
    public void setElectionName(String electionName) { this.electionName = electionName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Timestamp getStartDateTime() { return startDateTime; }
    public void setStartDateTime(Timestamp startDateTime) { this.startDateTime = startDateTime; }

    public Timestamp getEndDateTime() { return endDateTime; }
    public void setEndDateTime(Timestamp endDateTime) { this.endDateTime = endDateTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getCreatedBy() { return createdBy; }
    public void setCreatedBy(int createdBy) { this.createdBy = createdBy; }

    public String getCreatedByName() { return createdByName; }
    public void setCreatedByName(String createdByName) { this.createdByName = createdByName; }

    public Timestamp getCreatedDate() { return createdDate; }
    public void setCreatedDate(Timestamp createdDate) { this.createdDate = createdDate; }

    @Override
    public String toString() {
        return "Election{" +
                "electionId=" + electionId +
                ", electionName='" + electionName + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}