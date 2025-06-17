// src/com/votingsystem/model/Candidate.java
package com.votingsystem.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class Candidate implements Serializable {
    private static final long serialVersionUID = 1L;

    private int candidateId;
    private int userId; // References the USERS table
    private String userName; // To display candidate's name
    private String userFullName; // To display candidate's full name
    private int electionId;
    private String electionName; // To display election name
    private String partyAffiliation;
    private String manifesto; // Can be a longer text
    private String approvalStatus; // PENDING, APPROVED, REJECTED
    private Timestamp registrationDate;
    private int voteCount; // Not in DB, but populated for results

    public Candidate() {
    }

    public Candidate(int candidateId, int userId, int electionId,
                     String partyAffiliation, String manifesto, String approvalStatus,
                     Timestamp registrationDate) {
        this.candidateId = candidateId;
        this.userId = userId;
        this.electionId = electionId;
        this.partyAffiliation = partyAffiliation;
        this.manifesto = manifesto;
        this.approvalStatus = approvalStatus;
        this.registrationDate = registrationDate;
    }

    // Getters and Setters
    public int getCandidateId() { return candidateId; }
    public void setCandidateId(int candidateId) { this.candidateId = candidateId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getUserFullName() { return userFullName; }
    public void setUserFullName(String userFullName) { this.userFullName = userFullName; }

    public int getElectionId() { return electionId; }
    public void setElectionId(int electionId) { this.electionId = electionId; }

    public String getElectionName() { return electionName; }
    public void setElectionName(String electionName) { this.electionName = electionName; }

    public String getPartyAffiliation() { return partyAffiliation; }
    public void setPartyAffiliation(String partyAffiliation) { this.partyAffiliation = partyAffiliation; }

    public String getManifesto() { return manifesto; }
    public void setManifesto(String manifesto) { this.manifesto = manifesto; }

    public String getApprovalStatus() { return approvalStatus; }
    public void setApprovalStatus(String approvalStatus) { this.approvalStatus = approvalStatus; }

    public Timestamp getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(Timestamp registrationDate) { this.registrationDate = registrationDate; }

    public int getVoteCount() { return voteCount; }
    public void setVoteCount(int voteCount) { this.voteCount = voteCount; }

    @Override
    public String toString() {
        return "Candidate{" +
                "candidateId=" + candidateId +
                ", userName='" + userName + '\'' +
                ", electionName='" + electionName + '\'' +
                ", approvalStatus='" + approvalStatus + '\'' +
                '}';
    }
}