// src/main/java/com/votingsystem/dao/ConstituencyDAO.java
package com.votingsystem.dao;

import com.votingsystem.db.DBConnection;
import com.votingsystem.model.Constituency;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for the Constituency model.
 * Handles all database operations related to the CONSTITUENCIES table.
 */
public class ConstituencyDAO {

    /**
     * Inserts a new constituency into the database.
     *
     * @param constituency The Constituency object to insert.
     * @return The generated constituency ID if successful, -1 otherwise.
     */
    public int addConstituency(Constituency constituency) {
        String sql = "INSERT INTO CONSTITUENCIES (CONSTITUENCY_ID, NAME, DESCRIPTION) VALUES (CONSTITUENCIES_SEQ.NEXTVAL, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int generatedId = -1;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql, new String[]{"CONSTITUENCY_ID"});

            pstmt.setString(1, constituency.getName());
            pstmt.setString(2, constituency.getDescription());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    generatedId = rs.getInt(1);
                    System.out.println("Constituency added successfully with ID: " + generatedId);
                } else {
                    // Fallback for Oracle sequence if getGeneratedKeys doesn't return it
                    try (Statement stmt2 = conn.createStatement();
                         ResultSet rs2 = stmt2.executeQuery("SELECT CONSTITUENCIES_SEQ.CURRVAL FROM DUAL")) {
                        if (rs2.next()) {
                            generatedId = rs2.getInt(1);
                            System.out.println("Constituency added successfully (via CURRVAL) with ID: " + generatedId);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding constituency: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeResources(rs, pstmt, conn);
        }
        return generatedId;
    }

    /**
     * Retrieves a constituency by its ID.
     *
     * @param constituencyId The ID of the constituency to retrieve.
     * @return The Constituency object if found, null otherwise.
     */
    public Constituency getConstituencyById(int constituencyId) {
        String sql = "SELECT CONSTITUENCY_ID, NAME, DESCRIPTION, CREATED_AT, UPDATED_AT FROM CONSTITUENCIES WHERE CONSTITUENCY_ID = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Constituency constituency = null;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, constituencyId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                constituency = new Constituency();
                constituency.setConstituencyId(rs.getInt("CONSTITUENCY_ID"));
                constituency.setName(rs.getString("NAME"));
                constituency.setDescription(rs.getString("DESCRIPTION"));
                constituency.setCreatedAt(rs.getTimestamp("CREATED_AT"));
                constituency.setUpdatedAt(rs.getTimestamp("UPDATED_AT"));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving constituency by ID: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeResources(rs, pstmt, conn);
        }
        return constituency;
    }

    /**
     * Retrieves a constituency by its name.
     *
     * @param name The name of the constituency to retrieve.
     * @return The Constituency object if found, null otherwise.
     */
    public Constituency getConstituencyByName(String name) {
        String sql = "SELECT CONSTITUENCY_ID, NAME, DESCRIPTION, CREATED_AT, UPDATED_AT FROM CONSTITUENCIES WHERE NAME = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Constituency constituency = null;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                constituency = new Constituency();
                constituency.setConstituencyId(rs.getInt("CONSTITUENCY_ID"));
                constituency.setName(rs.getString("NAME"));
                constituency.setDescription(rs.getString("DESCRIPTION"));
                constituency.setCreatedAt(rs.getTimestamp("CREATED_AT"));
                constituency.setUpdatedAt(rs.getTimestamp("UPDATED_AT"));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving constituency by name: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeResources(rs, pstmt, conn);
        }
        return constituency;
    }

    /**
     * Updates an existing constituency's information in the database.
     *
     * @param constituency The Constituency object with updated information.
     * @return true if the constituency was updated successfully, false otherwise.
     */
    public boolean updateConstituency(Constituency constituency) {
        String sql = "UPDATE CONSTITUENCIES SET NAME = ?, DESCRIPTION = ?, UPDATED_AT = CURRENT_TIMESTAMP WHERE CONSTITUENCY_ID = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, constituency.getName());
            pstmt.setString(2, constituency.getDescription());
            pstmt.setInt(3, constituency.getConstituencyId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Constituency with ID " + constituency.getConstituencyId() + " updated successfully.");
                success = true;
            }
        } catch (SQLException e) {
            System.err.println("Error updating constituency: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeResources(null, pstmt, conn);
        }
        return success;
    }

    /**
     * Deletes a constituency from the database by its ID.
     *
     * @param constituencyId The ID of the constituency to delete.
     * @return true if the constituency was deleted successfully, false otherwise.
     */
    public boolean deleteConstituency(int constituencyId) {
        String sql = "DELETE FROM CONSTITUENCIES WHERE CONSTITUENCY_ID = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, constituencyId);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Constituency with ID " + constituencyId + " deleted successfully.");
                success = true;
            }
        } catch (SQLException e) {
            System.err.println("Error deleting constituency: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeResources(null, pstmt, conn);
        }
        return success;
    }

    /**
     * Retrieves all constituencies from the database.
     *
     * @return A list of all Constituency objects.
     */
    public List<Constituency> getAllConstituencies() {
        String sql = "SELECT CONSTITUENCY_ID, NAME, DESCRIPTION, CREATED_AT, UPDATED_AT FROM CONSTITUENCIES ORDER BY NAME";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Constituency> constituencies = new ArrayList<>();

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Constituency constituency = new Constituency();
                constituency.setConstituencyId(rs.getInt("CONSTITUENCY_ID"));
                constituency.setName(rs.getString("NAME"));
                constituency.setDescription(rs.getString("DESCRIPTION"));
                constituency.setCreatedAt(rs.getTimestamp("CREATED_AT"));
                constituency.setUpdatedAt(rs.getTimestamp("UPDATED_AT"));
                constituencies.add(constituency);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving all constituencies: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeResources(rs, pstmt, conn);
        }
        return constituencies;
    }
}
