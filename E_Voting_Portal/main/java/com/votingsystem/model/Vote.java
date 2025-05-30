// src/main/java/com/votingsystem/model/Vote.java
package com.votingsystem.model;

import java.sql.Timestamp;

/**
 * Represents a Vote cast in the voting system.
 * Corresponds to the VOTES table in the database.
 */
public class Vote {
    private int voteId;
    private int voterId;
    private int candidateId;
    private int electionId;
    private Timestamp voteTimestamp;
    private String ipAddress;

    // Default constructor
    public Vote() {
    }

    // Constructor for creating a new vote (without ID, timestamp)
    public Vote(int voterId, int candidateId, int electionId, String ipAddress) {
        this.voterId = voterId;
        this.candidateId = candidateId;
        this.electionId = electionId;
        this.ipAddress = ipAddress;
    }

    // Full constructor
    public Vote(int voteId, int voterId, int candidateId, int electionId, Timestamp voteTimestamp, String ipAddress) {
        this.voteId = voteId;
        this.voterId = voterId;
        this.candidateId = candidateId;
        this.electionId = electionId;
        this.voteTimestamp = voteTimestamp;
        this.ipAddress = ipAddress;
    }

    // Getters and Setters
    public int getVoteId() {
        return voteId;
    }

    public void setVoteId(int voteId) {
        this.voteId = voteId;
    }

    public int getVoterId() {
        return voterId;
    }

    public void setVoterId(int voterId) {
        this.voterId = voterId;
    }

    public int getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(int candidateId) {
        this.candidateId = candidateId;
    }

    public int getElectionId() {
        return electionId;
    }

    public void setElectionId(int electionId) {
        this.electionId = electionId;
    }

    public Timestamp getVoteTimestamp() {
        return voteTimestamp;
    }

    public void setVoteTimestamp(Timestamp voteTimestamp) {
        this.voteTimestamp = voteTimestamp;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    @Override
    public String toString() {
        return "Vote{" +
               "voteId=" + voteId +
               ", voterId=" + voterId +
               ", candidateId=" + candidateId +
               ", electionId=" + electionId +
               ", voteTimestamp=" + voteTimestamp +
               ", ipAddress='" + ipAddress + '\'' +
               '}';
    }
}
