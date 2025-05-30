// src/main/java/com/votingsystem/dao/ElectionDAO.java
package com.votingsystem.dao;

import com.votingsystem.db.DBConnection;
import com.votingsystem.model.Election;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for the Election model.
 * Handles all database operations related to the ELECTIONS table.
 */
public class ElectionDAO {

    /**
     * Inserts a new election into the database.
     *
     * @param election The Election object to insert.
     * @return The generated election ID if successful, -1 otherwise.
     */
    public int addElection(Election election) {
        String sql = "INSERT INTO ELECTIONS (ELECTION_ID, NAME, DESCRIPTION, START_DATE, END_DATE, STATUS) VALUES (ELECTIONS_SEQ.NEXTVAL, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int generatedId = -1;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql, new String[]{"ELECTION_ID"});

            pstmt.setString(1, election.getName());
            pstmt.setString(2, election.getDescription());
            pstmt.setTimestamp(3, election.getStartDate());
            pstmt.setTimestamp(4, election.getEndDate());
            pstmt.setString(5, election.getStatus());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    generatedId = rs.getInt(1);
                    System.out.println("Election added successfully with ID: " + generatedId);
                } else {
                    try (Statement stmt2 = conn.createStatement();
                         ResultSet rs2 = stmt2.executeQuery("SELECT ELECTIONS_SEQ.CURRVAL FROM DUAL")) {
                        if (rs2.next()) {
                            generatedId = rs2.getInt(1);
                            System.out.println("Election added successfully (via CURRVAL) with ID: " + generatedId);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding election: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeResources(rs, pstmt, conn);
        }
        return generatedId;
    }

    /**
     * Retrieves an election by its ID.
     *
     * @param electionId The ID of the election to retrieve.
     * @return The Election object if found, null otherwise.
     */
    public Election getElectionById(int electionId) {
        String sql = "SELECT ELECTION_ID, NAME, DESCRIPTION, START_DATE, END_DATE, STATUS, CREATED_AT, UPDATED_AT FROM ELECTIONS WHERE ELECTION_ID = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Election election = null;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, electionId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                election = new Election();
                election.setElectionId(rs.getInt("ELECTION_ID"));
                election.setName(rs.getString("NAME"));
                election.setDescription(rs.getString("DESCRIPTION"));
                election.setStartDate(rs.getTimestamp("START_DATE"));
                election.setEndDate(rs.getTimestamp("END_DATE"));
                election.setStatus(rs.getString("STATUS"));
                election.setCreatedAt(rs.getTimestamp("CREATED_AT"));
                election.setUpdatedAt(rs.getTimestamp("UPDATED_AT"));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving election by ID: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeResources(rs, pstmt, conn);
        }
        return election;
    }

    /**
     * Retrieves an election by its name.
     *
     * @param name The name of the election to retrieve.
     * @return The Election object if found, null otherwise.
     */
    public Election getElectionByName(String name) {
        String sql = "SELECT ELECTION_ID, NAME, DESCRIPTION, START_DATE, END_DATE, STATUS, CREATED_AT, UPDATED_AT FROM ELECTIONS WHERE NAME = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Election election = null;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                election = new Election();
                election.setElectionId(rs.getInt("ELECTION_ID"));
                election.setName(rs.getString("NAME"));
                election.setDescription(rs.getString("DESCRIPTION"));
                election.setStartDate(rs.getTimestamp("START_DATE"));
                election.setEndDate(rs.getTimestamp("END_DATE"));
                election.setStatus(rs.getString("STATUS"));
                election.setCreatedAt(rs.getTimestamp("CREATED_AT"));
                election.setUpdatedAt(rs.getTimestamp("UPDATED_AT"));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving election by name: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeResources(rs, pstmt, conn);
        }
        return election;
    }

    /**
     * Updates an existing election's information in the database.
     *
     * @param election The Election object with updated information.
     * @return true if the election was updated successfully, false otherwise.
     */
    public boolean updateElection(Election election) {
        String sql = "UPDATE ELECTIONS SET NAME = ?, DESCRIPTION = ?, START_DATE = ?, END_DATE = ?, STATUS = ?, UPDATED_AT = CURRENT_TIMESTAMP WHERE ELECTION_ID = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, election.getName());
            pstmt.setString(2, election.getDescription());
            pstmt.setTimestamp(3, election.getStartDate());
            pstmt.setTimestamp(4, election.getEndDate());
            pstmt.setString(5, election.getStatus());
            pstmt.setInt(6, election.getElectionId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Election with ID " + election.getElectionId() + " updated successfully.");
                success = true;
            }
        } catch (SQLException e) {
            System.err.println("Error updating election: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeResources(null, pstmt, conn);
        }
        return success;
    }

    /**
     * Deletes an election from the database by its ID.
     *
     * @param electionId The ID of the election to delete.
     * @return true if the election was deleted successfully, false otherwise.
     */
    public boolean deleteElection(int electionId) {
        String sql = "DELETE FROM ELECTIONS WHERE ELECTION_ID = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, electionId);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Election with ID " + electionId + " deleted successfully.");
                success = true;
            }
        } catch (SQLException e) {
            System.err.println("Error deleting election: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeResources(null, pstmt, conn);
        }
        return success;
    }

    /**
     * Retrieves all elections from the database.
     *
     * @return A list of all Election objects.
     */
    public List<Election> getAllElections() {
        String sql = "SELECT ELECTION_ID, NAME, DESCRIPTION, START_DATE, END_DATE, STATUS, CREATED_AT, UPDATED_AT FROM ELECTIONS ORDER BY START_DATE DESC";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Election> elections = new ArrayList<>();

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Election election = new Election();
                election.setElectionId(rs.getInt("ELECTION_ID"));
                election.setName(rs.getString("NAME"));
                election.setDescription(rs.getString("DESCRIPTION"));
                election.setStartDate(rs.getTimestamp("START_DATE"));
                election.setEndDate(rs.getTimestamp("END_DATE"));
                election.setStatus(rs.getString("STATUS"));
                election.setCreatedAt(rs.getTimestamp("CREATED_AT"));
                election.setUpdatedAt(rs.getTimestamp("UPDATED_AT"));
                elections.add(election);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving all elections: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeResources(rs, pstmt, conn);
        }
        return elections;
    }

    /**
     * Retrieves elections by their status.
     *
     * @param status The status to filter by (e.g., "UPCOMING", "ACTIVE", "COMPLETED").
     * @return A list of Election objects matching the specified status.
     */
    public List<Election> getElectionsByStatus(String status) {
        String sql = "SELECT ELECTION_ID, NAME, DESCRIPTION, START_DATE, END_DATE, STATUS, CREATED_AT, UPDATED_AT FROM ELECTIONS WHERE STATUS = ? ORDER BY START_DATE DESC";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Election> elections = new ArrayList<>();

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, status);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Election election = new Election();
                election.setElectionId(rs.getInt("ELECTION_ID"));
                election.setName(rs.getString("NAME"));
                election.setDescription(rs.getString("DESCRIPTION"));
                election.setStartDate(rs.getTimestamp("START_DATE"));
                election.setEndDate(rs.getTimestamp("END_DATE"));
                election.setStatus(rs.getString("STATUS"));
                election.setCreatedAt(rs.getTimestamp("CREATED_AT"));
                election.setUpdatedAt(rs.getTimestamp("UPDATED_AT"));
                elections.add(election);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving elections by status: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeResources(rs, pstmt, conn);
        }
        return elections;
    }
}
