// src/main/java/com/votingsystem/model/Candidate.java
package com.votingsystem.model;

import java.sql.Timestamp;

/**
 * Represents a Candidate participating in an Election.
 * Corresponds to the CANDIDATES table in the database.
 * Approval status can be 'PENDING', 'APPROVED', 'REJECTED'.
 */
public class Candidate {
    private int candidateId;
    private int userId; // Links to the User who is a candidate
    private int electionId;
    private int constituencyId;
    private String party;
    private String symbolUrl;
    private String manifesto;
    private String approvalStatus; // 'PENDING', 'APPROVED', 'REJECTED'
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Default constructor
    public Candidate() {
    }

    // Constructor for creating a new candidate (without ID, timestamps)
    public Candidate(int userId, int electionId, int constituencyId, String party, String symbolUrl, String manifesto, String approvalStatus) {
        this.userId = userId;
        this.electionId = electionId;
        this.constituencyId = constituencyId;
        this.party = party;
        this.symbolUrl = symbolUrl;
        this.manifesto = manifesto;
        this.approvalStatus = approvalStatus;
    }

    // Full constructor
    public Candidate(int candidateId, int userId, int electionId, int constituencyId, String party,
                     String symbolUrl, String manifesto, String approvalStatus, Timestamp createdAt, Timestamp updatedAt) {
        this.candidateId = candidateId;
        this.userId = userId;
        this.electionId = electionId;
        this.constituencyId = constituencyId;
        this.party = party;
        this.symbolUrl = symbolUrl;
        this.manifesto = manifesto;
        this.approvalStatus = approvalStatus;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public int getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(int candidateId) {
        this.candidateId = candidateId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getElectionId() {
        return electionId;
    }

    public void setElectionId(int electionId) {
        this.electionId = electionId;
    }

    public int getConstituencyId() {
        return constituencyId;
    }

    public void setConstituencyId(int constituencyId) {
        this.constituencyId = constituencyId;
    }

    public String getParty() {
        return party;
    }

    public void setParty(String party) {
        this.party = party;
    }

    public String getSymbolUrl() {
        return symbolUrl;
    }

    public void setSymbolUrl(String symbolUrl) {
        this.symbolUrl = symbolUrl;
    }

    public String getManifesto() {
        return manifesto;
    }

    public void setManifesto(String manifesto) {
        this.manifesto = manifesto;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
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
        return "Candidate{" +
               "candidateId=" + candidateId +
               ", userId=" + userId +
               ", electionId=" + electionId +
               ", constituencyId=" + constituencyId +
               ", party='" + party + '\'' +
               ", symbolUrl='" + symbolUrl + '\'' +
               ", manifesto='" + manifesto + '\'' +
               ", approvalStatus='" + approvalStatus + '\'' +
               ", createdAt=" + createdAt +
               ", updatedAt=" + updatedAt +
               '}';
    }
}
