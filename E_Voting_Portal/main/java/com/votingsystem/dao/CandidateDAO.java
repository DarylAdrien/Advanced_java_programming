// src/main/java/com/votingsystem/dao/CandidateDAO.java
package com.votingsystem.dao;

import com.votingsystem.db.DBConnection;
import com.votingsystem.model.Candidate;
import com.votingsystem.model.User; // To fetch user details for candidate display

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for the Candidate model.
 * Handles all database operations related to the CANDIDATES table.
 */
public class CandidateDAO {

    /**
     * Inserts a new candidate into the database.
     *
     * @param candidate The Candidate object to insert.
     * @return The generated candidate ID if successful, -1 otherwise.
     */
    public int addCandidate(Candidate candidate) {
        String sql = "INSERT INTO CANDIDATES (CANDIDATE_ID, USER_ID, ELECTION_ID, CONSTITUENCY_ID, PARTY, SYMBOL_URL, MANIFESTO, APPROVAL_STATUS) VALUES (CANDIDATES_SEQ.NEXTVAL, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int generatedId = -1;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql, new String[]{"CANDIDATE_ID"});

            pstmt.setInt(1, candidate.getUserId());
            pstmt.setInt(2, candidate.getElectionId());
            pstmt.setInt(3, candidate.getConstituencyId());
            pstmt.setString(4, candidate.getParty());
            pstmt.setString(5, candidate.getSymbolUrl());
            pstmt.setString(6, candidate.getManifesto());
            pstmt.setString(7, candidate.getApprovalStatus());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    generatedId = rs.getInt(1);
                    System.out.println("Candidate added successfully with ID: " + generatedId);
                } else {
                    try (Statement stmt2 = conn.createStatement();
                         ResultSet rs2 = stmt2.executeQuery("SELECT CANDIDATES_SEQ.CURRVAL FROM DUAL")) {
                        if (rs2.next()) {
                            generatedId = rs2.getInt(1);
                            System.out.println("Candidate added successfully (via CURRVAL) with ID: " + generatedId);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding candidate: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeResources(rs, pstmt, conn);
        }
        return generatedId;
    }

    /**
     * Retrieves a candidate by their ID.
     *
     * @param candidateId The ID of the candidate to retrieve.
     * @return The Candidate object if found, null otherwise.
     */
    public Candidate getCandidateById(int candidateId) {
        String sql = "SELECT CANDIDATE_ID, USER_ID, ELECTION_ID, CONSTITUENCY_ID, PARTY, SYMBOL_URL, MANIFESTO, APPROVAL_STATUS, CREATED_AT, UPDATED_AT FROM CANDIDATES WHERE CANDIDATE_ID = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Candidate candidate = null;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, candidateId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                candidate = new Candidate();
                candidate.setCandidateId(rs.getInt("CANDIDATE_ID"));
                candidate.setUserId(rs.getInt("USER_ID"));
                candidate.setElectionId(rs.getInt("ELECTION_ID"));
                candidate.setConstituencyId(rs.getInt("CONSTITUENCY_ID"));
                candidate.setParty(rs.getString("PARTY"));
                candidate.setSymbolUrl(rs.getString("SYMBOL_URL"));
                candidate.setManifesto(rs.getString("MANIFESTO"));
                candidate.setApprovalStatus(rs.getString("APPROVAL_STATUS"));
                candidate.setCreatedAt(rs.getTimestamp("CREATED_AT"));
                candidate.setUpdatedAt(rs.getTimestamp("UPDATED_AT"));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving candidate by ID: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeResources(rs, pstmt, conn);
        }
        return candidate;
    }

    /**
     * Retrieves a candidate by their User ID, Election ID, and Constituency ID.
     * This is useful to check if a specific user is already a candidate in a given election and constituency.
     *
     * @param userId The ID of the user.
     * @param electionId The ID of the election.
     * @param constituencyId The ID of the constituency.
     * @return The Candidate object if found, null otherwise.
     */
    public boolean updateCandidatesConstituencyByUserId(int userId, int newConstituencyId) {
        String sql = "UPDATE candidates SET constituency_id = ? WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, newConstituencyId);
            pstmt.setInt(2, userId);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                success = true;
                System.out.println("Updated constituency for " + rowsAffected + " candidate(s) of user ID: " + userId);
            } else {
                System.out.println("No candidate profiles found for user ID: " + userId + " to update constituency.");
            }
        } catch (SQLException e) {
            System.err.println("Error updating candidate constituency for user ID " + userId + ": " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeResources(null, pstmt, conn);
        }
        return success;
    }
    
    public Candidate getCandidateByUserDetails(int userId, int electionId, int constituencyId) {
        String sql = "SELECT CANDIDATE_ID, USER_ID, ELECTION_ID, CONSTITUENCY_ID, PARTY, SYMBOL_URL, MANIFESTO, APPROVAL_STATUS, CREATED_AT, UPDATED_AT FROM CANDIDATES WHERE USER_ID = ? AND ELECTION_ID = ? AND CONSTITUENCY_ID = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Candidate candidate = null;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.setInt(2, electionId);
            pstmt.setInt(3, constituencyId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                candidate = new Candidate();
                candidate.setCandidateId(rs.getInt("CANDIDATE_ID"));
                candidate.setUserId(rs.getInt("USER_ID"));
                candidate.setElectionId(rs.getInt("ELECTION_ID"));
                candidate.setConstituencyId(rs.getInt("CONSTITUENCY_ID"));
                candidate.setParty(rs.getString("PARTY"));
                candidate.setSymbolUrl(rs.getString("SYMBOL_URL"));
                candidate.setManifesto(rs.getString("MANIFESTO"));
                candidate.setApprovalStatus(rs.getString("APPROVAL_STATUS"));
                candidate.setCreatedAt(rs.getTimestamp("CREATED_AT"));
                candidate.setUpdatedAt(rs.getTimestamp("UPDATED_AT"));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving candidate by user, election, constituency: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeResources(rs, pstmt, conn);
        }
        return candidate;
    }

    /**
     * Updates an existing candidate's information in the database.
     *
     * @param candidate The Candidate object with updated information.
     * @return true if the candidate was updated successfully, false otherwise.
     */
    public boolean updateCandidate(Candidate candidate) {
        String sql = "UPDATE CANDIDATES SET USER_ID = ?, ELECTION_ID = ?, CONSTITUENCY_ID = ?, PARTY = ?, SYMBOL_URL = ?, MANIFESTO = ?, APPROVAL_STATUS = ?, UPDATED_AT = CURRENT_TIMESTAMP WHERE CANDIDATE_ID = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, candidate.getUserId());
            pstmt.setInt(2, candidate.getElectionId());
            pstmt.setInt(3, candidate.getConstituencyId());
            pstmt.setString(4, candidate.getParty());
            pstmt.setString(5, candidate.getSymbolUrl());
            pstmt.setString(6, candidate.getManifesto());
            pstmt.setString(7, candidate.getApprovalStatus());
            pstmt.setInt(8, candidate.getCandidateId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Candidate with ID " + candidate.getCandidateId() + " updated successfully.");
                success = true;
            }
        } catch (SQLException e) {
            System.err.println("Error updating candidate: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeResources(null, pstmt, conn);
        }
        return success;
    }

    /**
     * Deletes a candidate from the database by their ID.
     *
     * @param candidateId The ID of the candidate to delete.
     * @return true if the candidate was deleted successfully, false otherwise.
     */
    public boolean deleteCandidate(int candidateId) {
        String sql = "DELETE FROM CANDIDATES WHERE CANDIDATE_ID = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, candidateId);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Candidate with ID " + candidateId + " deleted successfully.");
                success = true;
            }
        } catch (SQLException e) {
            System.err.println("Error deleting candidate: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeResources(null, pstmt, conn);
        }
        return success;
    }

    /**
     * Retrieves all candidates for a specific election and constituency.
     *
     * @param electionId The ID of the election.
     * @param constituencyId The ID of the constituency.
     * @return A list of Candidate objects.
     */
    public List<Candidate> getCandidatesByElectionAndConstituency(int electionId, int constituencyId) {
        String sql = "SELECT c.CANDIDATE_ID, c.USER_ID, c.ELECTION_ID, c.CONSTITUENCY_ID, c.PARTY, c.SYMBOL_URL, c.MANIFESTO, c.APPROVAL_STATUS, c.CREATED_AT, c.UPDATED_AT, u.FIRST_NAME, u.LAST_NAME, u.USERNAME " +
                     "FROM CANDIDATES c JOIN USERS u ON c.USER_ID = u.USER_ID " +
                     "WHERE c.ELECTION_ID = ? AND c.CONSTITUENCY_ID = ? AND c.APPROVAL_STATUS = 'APPROVED' ORDER BY u.FIRST_NAME, u.LAST_NAME";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Candidate> candidates = new ArrayList<>();

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, electionId);
            pstmt.setInt(2, constituencyId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Candidate candidate = new Candidate();
                candidate.setCandidateId(rs.getInt("CANDIDATE_ID"));
                candidate.setUserId(rs.getInt("USER_ID"));
                candidate.setElectionId(rs.getInt("ELECTION_ID"));
                candidate.setConstituencyId(rs.getInt("CONSTITUENCY_ID"));
                candidate.setParty(rs.getString("PARTY"));
                candidate.setSymbolUrl(rs.getString("SYMBOL_URL"));
                candidate.setManifesto(rs.getString("MANIFESTO"));
                candidate.setApprovalStatus(rs.getString("APPROVAL_STATUS"));
                candidate.setCreatedAt(rs.getTimestamp("CREATED_AT"));
                candidate.setUpdatedAt(rs.getTimestamp("UPDATED_AT"));
                // You might want to extend Candidate model or create a DTO to include User's first/last name
                // For now, we'll just set it if needed, or fetch separately.
                // For display purposes, you can retrieve the User object using UserDAO.getUserById(candidate.getUserId())
                // or join in the SQL query if you need it frequently.
                // For simplicity, we'll assume the JSP will fetch user details if needed.
                candidates.add(candidate);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving candidates by election and constituency: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeResources(rs, pstmt, conn);
        }
        return candidates;
    }

    /**
     * Retrieves all candidates with their associated user details.
     * This is useful for admin dashboards to view all candidates.
     *
     * @return A list of Candidate objects, potentially with User details populated.
     */
    public List<Candidate> getAllCandidatesWithUserDetails() {
        String sql = "SELECT c.CANDIDATE_ID, c.USER_ID, c.ELECTION_ID, c.CONSTITUENCY_ID, c.PARTY, c.SYMBOL_URL, c.MANIFESTO, c.APPROVAL_STATUS, c.CREATED_AT, c.UPDATED_AT, " +
                     "u.USERNAME, u.FIRST_NAME, u.LAST_NAME, u.EMAIL, u.AADHAAR_NUMBER " +
                     "FROM CANDIDATES c JOIN USERS u ON c.USER_ID = u.USER_ID ORDER BY c.CREATED_AT DESC";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Candidate> candidates = new ArrayList<>();
        UserDAO userDAO = new UserDAO(); // To potentially fetch full user object if needed

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Candidate candidate = new Candidate();
                candidate.setCandidateId(rs.getInt("CANDIDATE_ID"));
                candidate.setUserId(rs.getInt("USER_ID"));
                candidate.setElectionId(rs.getInt("ELECTION_ID"));
                candidate.setConstituencyId(rs.getInt("CONSTITUENCY_ID"));
                candidate.setParty(rs.getString("PARTY"));
                candidate.setSymbolUrl(rs.getString("SYMBOL_URL"));
                candidate.setManifesto(rs.getString("MANIFESTO"));
                candidate.setApprovalStatus(rs.getString("APPROVAL_STATUS"));
                candidate.setCreatedAt(rs.getTimestamp("CREATED_AT"));
                candidate.setUpdatedAt(rs.getTimestamp("UPDATED_AT"));

                // If you want to include User object directly in Candidate model, you'd set it here.
                // For simplicity, we'll rely on separate calls or DTOs if full user object is needed on frontend.
                // For now, we're just fetching some user details in the join for display.
                // You could create a CandidateDTO if you want to bundle Candidate and User info.
                // Example of how to get user details:
                // User user = userDAO.getUserById(candidate.getUserId());
                // if (user != null) { /* set user details on candidate object or a DTO */ }

                candidates.add(candidate);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving all candidates with user details: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeResources(rs, pstmt, conn);
        }
        return candidates;
    }

    /**
     * Updates the approval status of a candidate.
     *
     * @param candidateId The ID of the candidate to update.
     * @param status The new approval status ('APPROVED', 'REJECTED', 'PENDING').
     * @return true if the status was updated successfully, false otherwise.
     */
    public boolean updateCandidateApprovalStatus(int candidateId, String status) {
        String sql = "UPDATE CANDIDATES SET APPROVAL_STATUS = ?, UPDATED_AT = CURRENT_TIMESTAMP WHERE CANDIDATE_ID = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, status);
            pstmt.setInt(2, candidateId);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Candidate " + candidateId + " approval status updated to " + status + ".");
                success = true;
            }
        } catch (SQLException e) {
            System.err.println("Error updating candidate approval status: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeResources(null, pstmt, conn);
        }
        return success;
    }
}
