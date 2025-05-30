// src/main/java/com/votingsystem/dao/VoteDAO.java
package com.votingsystem.dao;

import com.votingsystem.db.DBConnection;
import com.votingsystem.model.Vote;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for the Vote model.
 * Handles all database operations related to the VOTES table.
 */
public class VoteDAO {

    /**
     * Inserts a new vote into the database.
     *
     * @param vote The Vote object to insert.
     * @return The generated vote ID if successful, -1 otherwise.
     */
    public int addVote(Vote vote) {
        String sql = "INSERT INTO VOTES (VOTE_ID, VOTER_ID, CANDIDATE_ID, ELECTION_ID, IP_ADDRESS) VALUES (VOTES_SEQ.NEXTVAL, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int generatedId = -1;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql, new String[]{"VOTE_ID"});

            pstmt.setInt(1, vote.getVoterId());
            pstmt.setInt(2, vote.getCandidateId());
            pstmt.setInt(3, vote.getElectionId());
            pstmt.setString(4, vote.getIpAddress());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    generatedId = rs.getInt(1);
                    System.out.println("Vote added successfully with ID: " + generatedId);
                } else {
                    try (Statement stmt2 = conn.createStatement();
                         ResultSet rs2 = stmt2.executeQuery("SELECT VOTES_SEQ.CURRVAL FROM DUAL")) {
                        if (rs2.next()) {
                            generatedId = rs2.getInt(1);
                            System.out.println("Vote added successfully (via CURRVAL) with ID: " + generatedId);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding vote: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeResources(rs, pstmt, conn);
        }
        return generatedId;
    }

    /**
     * Retrieves a vote by its ID.
     *
     * @param voteId The ID of the vote to retrieve.
     * @return The Vote object if found, null otherwise.
     */
    public Vote getVoteById(int voteId) {
        String sql = "SELECT VOTE_ID, VOTER_ID, CANDIDATE_ID, ELECTION_ID, VOTE_TIMESTAMP, IP_ADDRESS FROM VOTES WHERE VOTE_ID = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Vote vote = null;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, voteId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                vote = new Vote();
                vote.setVoteId(rs.getInt("VOTE_ID"));
                vote.setVoterId(rs.getInt("VOTER_ID"));
                vote.setCandidateId(rs.getInt("CANDIDATE_ID"));
                vote.setElectionId(rs.getInt("ELECTION_ID"));
                vote.setVoteTimestamp(rs.getTimestamp("VOTE_TIMESTAMP"));
                vote.setIpAddress(rs.getString("IP_ADDRESS"));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving vote by ID: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeResources(rs, pstmt, conn);
        }
        return vote;
    }

    /**
     * Checks if a specific voter has already voted in a given election.
     *
     * @param voterId The ID of the voter.
     * @param electionId The ID of the election.
     * @return true if the voter has already voted in this election, false otherwise.
     */
    public boolean hasVoted(int voterId, int electionId) {
        String sql = "SELECT COUNT(*) FROM VOTES WHERE VOTER_ID = ? AND ELECTION_ID = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean voted = false;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, voterId);
            pstmt.setInt(2, electionId);
            rs = pstmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                voted = true;
            }
        } catch (SQLException e) {
            System.err.println("Error checking if voter has voted: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeResources(rs, pstmt, conn);
        }
        return voted;
    }

    /**
     * Retrieves all votes for a specific election.
     * This is useful for calculating election results.
     *
     * @param electionId The ID of the election.
     * @return A list of Vote objects for the specified election.
     */
    public List<Vote> getVotesByElectionId(int electionId) {
        String sql = "SELECT VOTE_ID, VOTER_ID, CANDIDATE_ID, ELECTION_ID, VOTE_TIMESTAMP, IP_ADDRESS FROM VOTES WHERE ELECTION_ID = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Vote> votes = new ArrayList<>();

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, electionId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Vote vote = new Vote();
                vote.setVoteId(rs.getInt("VOTE_ID"));
                vote.setVoterId(rs.getInt("VOTER_ID"));
                vote.setCandidateId(rs.getInt("CANDIDATE_ID"));
                vote.setElectionId(rs.getInt("ELECTION_ID"));
                vote.setVoteTimestamp(rs.getTimestamp("VOTE_TIMESTAMP"));
                vote.setIpAddress(rs.getString("IP_ADDRESS"));
                votes.add(vote);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving votes by election ID: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeResources(rs, pstmt, conn);
        }
        return votes;
    }

    /**
     * Calculates the vote count for each candidate in a specific election and constituency.
     * Returns a list of objects, where each object contains candidate ID and their total votes.
     *
     * @param electionId The ID of the election.
     * @param constituencyId The ID of the constituency.
     * @return A List of Object arrays, where each array contains [Candidate ID (Integer), Vote Count (Integer)].
     * Returns an empty list if no votes are found or an error occurs.
     */
    public List<Object[]> getElectionResultsByConstituency(int electionId, int constituencyId) {
    	String sql = "SELECT v.CANDIDATE_ID, COUNT(v.VOTE_ID) AS VOTE_COUNT, u.FIRST_NAME, u.LAST_NAME, cand.PARTY " +
                "FROM VOTES v " +
                "JOIN CANDIDATES cand ON v.CANDIDATE_ID = cand.CANDIDATE_ID " +
                "JOIN USERS u ON cand.USER_ID = u.USER_ID " +
                "WHERE v.ELECTION_ID = ? AND cand.CONSTITUENCY_ID = ? " +
                "GROUP BY v.CANDIDATE_ID, u.FIRST_NAME, u.LAST_NAME, cand.PARTY " +
                "ORDER BY VOTE_COUNT DESC";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Object[]> results = new ArrayList<>();

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, electionId);
            pstmt.setInt(2, constituencyId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                results.add(new Object[]{
                    rs.getInt("CANDIDATE_ID"),
                    rs.getInt("VOTE_COUNT"),
                    rs.getString("FIRST_NAME"),
                    rs.getString("LAST_NAME"),
                    rs.getString("PARTY")
                });
            }
        } catch (SQLException e) {
            System.err.println("Error getting election results by constituency: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeResources(rs, pstmt, conn);
        }
        System.out.println(results);
        return results;
    }

    /**
     * Calculates the overall vote count for each candidate in a specific election across all constituencies.
     *
     * @param electionId The ID of the election.
     * @return A List of Object arrays, where each array contains [Candidate ID (Integer), Vote Count (Integer)].
     */
    public List<Object[]> getOverallElectionResults(int electionId) {
    	String sql = "SELECT v.CANDIDATE_ID, COUNT(v.VOTE_ID) AS VOTE_COUNT, u.FIRST_NAME, u.LAST_NAME, cand.PARTY " +
                "FROM VOTES v " +
                "JOIN CANDIDATES cand ON v.CANDIDATE_ID = cand.CANDIDATE_ID " +
                "JOIN USERS u ON cand.USER_ID = u.USER_ID " +
                "WHERE v.ELECTION_ID = ? " +
                "GROUP BY v.CANDIDATE_ID, u.FIRST_NAME, u.LAST_NAME, cand.PARTY " +
                "ORDER BY VOTE_COUNT DESC";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Object[]> results = new ArrayList<>();

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, electionId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                results.add(new Object[]{
                    rs.getInt("CANDIDATE_ID"),
                    rs.getInt("VOTE_COUNT"),
                    rs.getString("FIRST_NAME"),
                    rs.getString("LAST_NAME"),
                    rs.getString("PARTY")
                });
            }
        } catch (SQLException e) {
            System.err.println("Error getting overall election results: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeResources(rs, pstmt, conn);
        }
        return results;
    }
}
