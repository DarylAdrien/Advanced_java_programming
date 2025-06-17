// src/com/votingsystem/service/ElectionService.java
package com.votingsystem.service;

import com.votingsystem.model.Candidate;
import com.votingsystem.model.Constituency;
import com.votingsystem.model.Election;
import com.votingsystem.model.User;
import com.votingsystem.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ElectionService {

    private AuthService authService = new AuthService(); // For logging

    // --- Constituency Management ---

    /**
     * Creates a new constituency.
     * @param constituency The Constituency object to create.
     * @param adminUserId The ID of the admin performing the action.
     * @return The created Constituency with its ID, or null if failed.
     */
    public Constituency createConstituency(Constituency constituency, int adminUserId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Constituency createdConstituency = null;

        String sql = "INSERT INTO CONSTITUENCIES2 (CONSTITUENCY_ID, NAME, DESCRIPTION) VALUES (CONSTITUENCY_SEQ2.NEXTVAL, ?, ?)";

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql, new String[]{"CONSTITUENCY_ID"}); // Request generated key
            pstmt.setString(1, constituency.getName());
            pstmt.setString(2, constituency.getDescription());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                // For Oracle, we might need to use CURRVAL if executeUpdate doesn't directly return it easily
                Statement stmt = null;
                try {
                    stmt = conn.createStatement();
//                    rs = stmt.executeQuery("SELECT CONSTITUENCY_SEQ2.CURRVAL FROM DUAL");
                    rs = pstmt.getGeneratedKeys();
                    if (rs.next()) {
                    	int generatedUserId = rs.getInt(1); // Get the first (and usually only) generated key
                    	constituency.setConstituencyId(generatedUserId);
//                        constituency.setConstituencyId(rs.getInt(1));
                        createdConstituency = constituency;
                        authService.addLog(conn, adminUserId, "Constituency Creation", "Constituency '" + constituency.getName() + "' created.");
                    }
                } finally {
                    DBConnection.closeResultSet(rs);
                    DBConnection.closeStatement(stmt);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error creating constituency: " + e.getMessage());
            e.printStackTrace();
            authService.addLog(conn, adminUserId, "Constituency Creation Failed", "Failed to create constituency '" + constituency.getName() + "': " + e.getMessage());
        } finally {
            DBConnection.closeStatement(pstmt);
            DBConnection.closeConnection(conn);
        }
        return createdConstituency;
    }

    /**
     * Retrieves all constituencies.
     * @return A list of Constituency objects.
     */
    public List<Constituency> getAllConstituencies() {
        List<Constituency> constituencies = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String sql = "SELECT CONSTITUENCY_ID, NAME, DESCRIPTION FROM CONSTITUENCIES2 ORDER BY NAME";

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Constituency constituency = new Constituency();
                constituency.setConstituencyId(rs.getInt("CONSTITUENCY_ID"));
                constituency.setName(rs.getString("NAME"));
                constituency.setDescription(rs.getString("DESCRIPTION"));
                constituencies.add(constituency);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching constituencies: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeResultSet(rs);
            DBConnection.closeStatement(pstmt);
            DBConnection.closeConnection(conn);
        }
        return constituencies;
    }

    /**
     * Gets a constituency by its ID.
     * @param constituencyId The ID of the constituency.
     * @return The Constituency object, or null if not found.
     */
    public Constituency getConstituencyById(int constituencyId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Constituency constituency = null;

        String sql = "SELECT CONSTITUENCY_ID, NAME, DESCRIPTION FROM CONSTITUENCIES2 WHERE CONSTITUENCY_ID = ?";

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
            }
        } catch (SQLException e) {
            System.err.println("Error fetching constituency by ID: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeResultSet(rs);
            DBConnection.closeStatement(pstmt);
            DBConnection.closeConnection(conn);
        }
        return constituency;
    }

    // --- Election Management ---

    /**
     * Creates a new election.
     * @param election The Election object to create.
     * @param adminUserId The ID of the admin creating the election.
     * @return The created Election object with its ID, or null if failed.
     */
    public Election createElection(Election election, int adminUserId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Election createdElection = null;

        String sql = "INSERT INTO ELECTIONS2 (ELECTION_ID, ELECTION_NAME, DESCRIPTION, START_DATE_TIME, END_DATE_TIME, STATUS, CREATED_BY, CREATED_DATE) " +
                     "VALUES (ELECTION_SEQ2.NEXTVAL, ?, ?, ?, ?, ?, ?, SYSTIMESTAMP)";

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql, new String[]{"ELECTION_ID"});
            pstmt.setString(1, election.getElectionName());
            pstmt.setString(2, election.getDescription());
            pstmt.setTimestamp(3, election.getStartDateTime());
            pstmt.setTimestamp(4, election.getEndDateTime());
            pstmt.setString(5, election.getStatus());
            pstmt.setInt(6, adminUserId);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                Statement stmt = null;
                try {
                    stmt = conn.createStatement();
                    rs = stmt.executeQuery("SELECT ELECTION_SEQ2.CURRVAL FROM DUAL");
                    if (rs.next()) {
                        election.setElectionId(rs.getInt(1));
                        election.setCreatedBy(adminUserId);
                        election.setCreatedDate(new Timestamp(System.currentTimeMillis()));
                        createdElection = election;
                        authService.addLog(conn, adminUserId, "Election Creation", "Election '" + election.getElectionName() + "' created.");
                    }
                } finally {
                    DBConnection.closeResultSet(rs);
                    DBConnection.closeStatement(stmt);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error creating election: " + e.getMessage());
            e.printStackTrace();
            authService.addLog(conn, adminUserId, "Election Creation Failed", "Failed to create election '" + election.getElectionName() + "': " + e.getMessage());
        } finally {
            DBConnection.closeStatement(pstmt);
            DBConnection.closeConnection(conn);
        }
        return createdElection;
    }

    /**
     * Updates an existing election.
     * @param election The Election object with updated details.
     * @param adminUserId The ID of the admin updating the election.
     * @return true if updated successfully, false otherwise.
     */
    public boolean updateElection(Election election, int adminUserId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;

        String sql = "UPDATE ELECTIONS2 SET ELECTION_NAME = ?, DESCRIPTION = ?, " +
                     " STATUS = ? WHERE ELECTION_ID = ?";

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, election.getElectionName());
            pstmt.setString(2, election.getDescription());
//            pstmt.setTimestamp(3, election.getStartDateTime());
//            pstmt.setTimestamp(4, election.getEndDateTime());
            pstmt.setString(3, election.getStatus());
            pstmt.setInt(4, election.getElectionId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                success = true;
                authService.addLog(conn, adminUserId, "Election Update", "Election ID " + election.getElectionId() + " ('" + election.getElectionName() + "') updated.");
            }
        } catch (SQLException e) {
            System.err.println("Error updating election ID " + election.getElectionId() + ": " + e.getMessage());
            e.printStackTrace();
            authService.addLog(conn, adminUserId, "Election Update Failed", "Failed to update election ID " + election.getElectionId() + ": " + e.getMessage());
        } finally {
            DBConnection.closeStatement(pstmt);
            DBConnection.closeConnection(conn);
        }
        return success;
    }

    /**
     * Retrieves an election by its ID, optionally with the creator's name.
     * @param electionId The ID of the election.
     * @return The Election object, or null if not found.
     */
    public Election getElectionById(int electionId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Election election = null;

        String sql = "SELECT e.ELECTION_ID, e.ELECTION_NAME, e.DESCRIPTION, e.START_DATE_TIME, e.END_DATE_TIME, " +
                     "e.STATUS, e.CREATED_BY, u.FULL_NAME AS CREATED_BY_NAME, e.CREATED_DATE " +
                     "FROM ELECTIONS2 e JOIN USERS2 u ON e.CREATED_BY = u.USER_ID " +
                     "WHERE e.ELECTION_ID = ?";

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, electionId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                election = new Election();
                election.setElectionId(rs.getInt("ELECTION_ID"));
                election.setElectionName(rs.getString("ELECTION_NAME"));
                election.setDescription(rs.getString("DESCRIPTION"));
                election.setStartDateTime(rs.getTimestamp("START_DATE_TIME"));
                election.setEndDateTime(rs.getTimestamp("END_DATE_TIME"));
                election.setStatus(rs.getString("STATUS"));
                election.setCreatedBy(rs.getInt("CREATED_BY"));
                election.setCreatedByName(rs.getString("CREATED_BY_NAME"));
                election.setCreatedDate(rs.getTimestamp("CREATED_DATE"));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching election by ID " + electionId + ": " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeResultSet(rs);
            DBConnection.closeStatement(pstmt);
            DBConnection.closeConnection(conn);
        }
        return election;
    }

    /**
     * Retrieves elections based on their status (e.g., "ACTIVE", "COMPLETED", "SCHEDULED").
     * @param status The status to filter by.
     * @return A list of Election objects.
     */
    public List<Election> getElectionsByStatus(String status) {
        List<Election> elections = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String sql = "SELECT e.ELECTION_ID, e.ELECTION_NAME, e.DESCRIPTION, e.START_DATE_TIME, e.END_DATE_TIME, " +
                     "e.STATUS, e.CREATED_BY, u.FULL_NAME AS CREATED_BY_NAME, e.CREATED_DATE " +
                     "FROM ELECTIONS2 e JOIN USERS2 u ON e.CREATED_BY = u.USER_ID " +
                     "WHERE e.STATUS = ? ORDER BY e.START_DATE_TIME DESC";

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, status);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Election election = new Election();
                election.setElectionId(rs.getInt("ELECTION_ID"));
                election.setElectionName(rs.getString("ELECTION_NAME"));
                election.setDescription(rs.getString("DESCRIPTION"));
                election.setStartDateTime(rs.getTimestamp("START_DATE_TIME"));
                election.setEndDateTime(rs.getTimestamp("END_DATE_TIME"));
                election.setStatus(rs.getString("STATUS"));
                election.setCreatedBy(rs.getInt("CREATED_BY"));
                election.setCreatedByName(rs.getString("CREATED_BY_NAME"));
                election.setCreatedDate(rs.getTimestamp("CREATED_DATE"));
                elections.add(election);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching elections by status '" + status + "': " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeResultSet(rs);
            DBConnection.closeStatement(pstmt);
            DBConnection.closeConnection(conn);
        }
        return elections;
    }

    /**
     * Retrieves all elections regardless of status.
     * @return A list of all Election objects.
     */
    public List<Election> getAllElections() {
        List<Election> elections = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String sql = "SELECT e.ELECTION_ID, e.ELECTION_NAME, e.DESCRIPTION, e.START_DATE_TIME, e.END_DATE_TIME, " +
                     "e.STATUS, e.CREATED_BY, u.FULL_NAME AS CREATED_BY_NAME, e.CREATED_DATE " +
                     "FROM ELECTIONS2 e JOIN USERS2 u ON e.CREATED_BY = u.USER_ID " +
                     "ORDER BY e.START_DATE_TIME DESC";

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Election election = new Election();
                election.setElectionId(rs.getInt("ELECTION_ID"));
                election.setElectionName(rs.getString("ELECTION_NAME"));
                election.setDescription(rs.getString("DESCRIPTION"));
                election.setStartDateTime(rs.getTimestamp("START_DATE_TIME"));
                election.setEndDateTime(rs.getTimestamp("END_DATE_TIME"));
                election.setStatus(rs.getString("STATUS"));
                election.setCreatedBy(rs.getInt("CREATED_BY"));
                election.setCreatedByName(rs.getString("CREATED_BY_NAME"));
                election.setCreatedDate(rs.getTimestamp("CREATED_DATE"));
                elections.add(election);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all elections: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeResultSet(rs);
            DBConnection.closeStatement(pstmt);
            DBConnection.closeConnection(conn);
        }
        return elections;
    }

    // --- Candidate Management ---

    /**
     * Registers a user as a candidate for a specific election.
     * Note: This is usually done by a user with ROLE_ID=3 (Candidate) or by an Admin.
     * @param candidate The Candidate object containing user ID and election ID.
     * @param requestingUserId The ID of the user performing the action (for logging).
     * @return The registered Candidate object with its ID, or null if failed.
     */
    public Candidate registerCandidate(Candidate candidate, int requestingUserId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Candidate registeredCandidate = null;

        // Ensure the user trying to register is actually a candidate role or an admin
        // This check would ideally be in the servlet, but good to have a fallback here too.
        // For simplicity, we assume the requesting user has the right to register a candidate here.

        String sql = "INSERT INTO CANDIDATES2 (CANDIDATE_ID, USER_ID, ELECTION_ID, PARTY_AFFILIATION, MANIFESTO, APPROVAL_STATUS, REGISTRATION_DATE) " +
                     "VALUES (CANDIDATE_SEQ2.NEXTVAL, ?, ?, ?, ?, ?, SYSTIMESTAMP)";

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql, new String[]{"CANDIDATE_ID"});
            pstmt.setInt(1, candidate.getUserId());
            pstmt.setInt(2, candidate.getElectionId());
            pstmt.setString(3, candidate.getPartyAffiliation());
            pstmt.setString(4, candidate.getManifesto());
            pstmt.setString(5, "PENDING"); // Default status

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                Statement stmt = null;
                try {
                    stmt = conn.createStatement();
//                    rs = stmt.executeQuery("SELECT CANDIDATE_SEQ2.CURRVAL FROM DUAL");
                    rs = pstmt.getGeneratedKeys();
                    if (rs.next()) {
                    	int generatedUserId = rs.getInt(1); // Get the first (and usually only) generated key
                    	candidate.setCandidateId(generatedUserId);
//                        candidate.setCandidateId(rs.getInt(1));
                        candidate.setApprovalStatus("PENDING");
                        candidate.setRegistrationDate(new Timestamp(System.currentTimeMillis()));
                        registeredCandidate = candidate;
                        authService.addLog(conn, requestingUserId, "Candidate Registration", "User ID " + candidate.getUserId() + " registered as candidate for Election ID " + candidate.getElectionId() + ".");
                    }
                } finally {
                    DBConnection.closeResultSet(rs);
                    DBConnection.closeStatement(stmt);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error registering candidate: " + e.getMessage());
            e.printStackTrace();
            authService.addLog(conn, requestingUserId, "Candidate Registration Failed", "Failed to register candidate for user ID " + candidate.getUserId() + ", election ID " + candidate.getElectionId() + ": " + e.getMessage());
        } finally {
            DBConnection.closeStatement(pstmt);
            DBConnection.closeConnection(conn);
        }
        return registeredCandidate;
    }

    /**
     * Approves or rejects a candidate's registration.
     * @param candidateId The ID of the candidate registration.
     * @param newStatus The new status ("APPROVED" or "REJECTED").
     * @param adminUserId The ID of the admin performing the action.
     * @return true if updated successfully, false otherwise.
     */
    public boolean updateCandidateApprovalStatus(int candidateId, String newStatus, int adminUserId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;

        String sql = "UPDATE CANDIDATES2 SET APPROVAL_STATUS = ? WHERE CANDIDATE_ID = ?";

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, newStatus);
            pstmt.setInt(2, candidateId);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                success = true;
                authService.addLog(conn, adminUserId, "Candidate Approval Update", "Candidate ID " + candidateId + " approval status set to " + newStatus + ".");
            }
        } catch (SQLException e) {
            System.err.println("Error updating candidate approval status for ID " + candidateId + ": " + e.getMessage());
            e.printStackTrace();
            authService.addLog(conn, adminUserId, "Candidate Approval Failed", "Failed to update candidate ID " + candidateId + " status to " + newStatus + ": " + e.getMessage());
        } finally {
            DBConnection.closeStatement(pstmt);
            DBConnection.closeConnection(conn);
        }
        return success;
    }

    /**
     * Retrieves all candidates for a specific election.
     * @param electionId The ID of the election.
     * @param includePending If true, includes candidates with 'PENDING' status; otherwise, only 'APPROVED'.
     * @return A list of Candidate objects.
     */
    public List<Candidate> getCandidatesForElection(int electionId, boolean includePending) {
        List<Candidate> candidates = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String sql = "SELECT c.CANDIDATE_ID, c.USER_ID, u.USERNAME, u.FULL_NAME, c.ELECTION_ID, e.ELECTION_NAME, " +
                     "c.PARTY_AFFILIATION, c.MANIFESTO, c.APPROVAL_STATUS, c.REGISTRATION_DATE " +
                     "FROM CANDIDATES2 c " +
                     "JOIN USERS2 u ON c.USER_ID = u.USER_ID " +
                     "JOIN ELECTIONS2 e ON c.ELECTION_ID = e.ELECTION_ID " +
                     "WHERE c.ELECTION_ID = ?";
        if (!includePending) {
            sql += " AND c.APPROVAL_STATUS = 'APPROVED'";
        }
        sql += " ORDER BY u.FULL_NAME";

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, electionId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Candidate candidate = new Candidate();
                candidate.setCandidateId(rs.getInt("CANDIDATE_ID"));
                candidate.setUserId(rs.getInt("USER_ID"));
                candidate.setUserName(rs.getString("USERNAME"));
                candidate.setUserFullName(rs.getString("FULL_NAME"));
                candidate.setElectionId(rs.getInt("ELECTION_ID"));
                candidate.setElectionName(rs.getString("ELECTION_NAME"));
                candidate.setPartyAffiliation(rs.getString("PARTY_AFFILIATION"));
                candidate.setManifesto(rs.getString("MANIFESTO"));
                candidate.setApprovalStatus(rs.getString("APPROVAL_STATUS"));
                candidate.setRegistrationDate(rs.getTimestamp("REGISTRATION_DATE"));
                candidates.add(candidate);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching candidates for election " + electionId + ": " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeResultSet(rs);
            DBConnection.closeStatement(pstmt);
            DBConnection.closeConnection(conn);
        }
        return candidates;
    }

    /**
     * Retrieves a single candidate by their candidate ID.
     * @param candidateId The ID of the candidate record.
     * @return The Candidate object, or null if not found.
     */
    public Candidate getCandidateById(int candidateId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Candidate candidate = null;

        String sql = "SELECT c.CANDIDATE_ID, c.USER_ID, u.USERNAME, u.FULL_NAME, c.ELECTION_ID, e.ELECTION_NAME, " +
                     "c.PARTY_AFFILIATION, c.MANIFESTO, c.APPROVAL_STATUS, c.REGISTRATION_DATE " +
                     "FROM CANDIDATES2 c " +
                     "JOIN USERS2 u ON c.USER_ID = u.USER_ID " +
                     "JOIN ELECTIONS2 e ON c.ELECTION_ID = e.ELECTION_ID " +
                     "WHERE c.CANDIDATE_ID = ?";

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, candidateId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                candidate = new Candidate();
                candidate.setCandidateId(rs.getInt("CANDIDATE_ID"));
                candidate.setUserId(rs.getInt("USER_ID"));
                candidate.setUserName(rs.getString("USERNAME"));
                candidate.setUserFullName(rs.getString("FULL_NAME"));
                candidate.setElectionId(rs.getInt("ELECTION_ID"));
                candidate.setElectionName(rs.getString("ELECTION_NAME"));
                candidate.setPartyAffiliation(rs.getString("PARTY_AFFILIATION"));
                candidate.setManifesto(rs.getString("MANIFESTO"));
                candidate.setApprovalStatus(rs.getString("APPROVAL_STATUS"));
                candidate.setRegistrationDate(rs.getTimestamp("REGISTRATION_DATE"));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching candidate by ID " + candidateId + ": " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeResultSet(rs);
            DBConnection.closeStatement(pstmt);
            DBConnection.closeConnection(conn);
        }
        return candidate;
    }

    /**
     * Retrieves all candidates (potentially for admin review).
     * @param includePending If true, includes candidates with 'PENDING' status; otherwise, only 'APPROVED'.
     * @return A list of all Candidate objects.
     */
    public List<Candidate> getAllCandidates(boolean includePending) {
        List<Candidate> candidates = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String sql = "SELECT c.CANDIDATE_ID, c.USER_ID, u.USERNAME, u.FULL_NAME, c.ELECTION_ID, e.ELECTION_NAME, " +
                     "c.PARTY_AFFILIATION, c.MANIFESTO, c.APPROVAL_STATUS, c.REGISTRATION_DATE " +
                     "FROM CANDIDATES2 c " +
                     "JOIN USERS2 u ON c.USER_ID = u.USER_ID " +
                     "JOIN ELECTIONS2 e ON c.ELECTION_ID = e.ELECTION_ID ";
        if (!includePending) {
            sql += "WHERE c.APPROVAL_STATUS = 'APPROVED' ";
        }
        sql += "ORDER BY e.ELECTION_NAME, u.FULL_NAME";

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Candidate candidate = new Candidate();
                candidate.setCandidateId(rs.getInt("CANDIDATE_ID"));
                candidate.setUserId(rs.getInt("USER_ID"));
                candidate.setUserName(rs.getString("USERNAME"));
                candidate.setUserFullName(rs.getString("FULL_NAME"));
                candidate.setElectionId(rs.getInt("ELECTION_ID"));
                candidate.setElectionName(rs.getString("ELECTION_NAME"));
                candidate.setPartyAffiliation(rs.getString("PARTY_AFFILIATION"));
                candidate.setManifesto(rs.getString("MANIFESTO"));
                candidate.setApprovalStatus(rs.getString("APPROVAL_STATUS"));
                candidate.setRegistrationDate(rs.getTimestamp("REGISTRATION_DATE"));
                candidates.add(candidate);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all candidates: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeResultSet(rs);
            DBConnection.closeStatement(pstmt);
            DBConnection.closeConnection(conn);
        }
        return candidates;
    }


    // --- Voting Logic ---

    /**
     * Casts a vote for a candidate in an election.
     * Ensures a voter can only vote once per election.
     * @param voterUserId The ID of the voter.
     * @param electionId The ID of the election.
     * @param candidateId The ID of the candidate being voted for.
     * @param ipAddress The IP address of the voter (for logging/auditing).
     * @return true if the vote was cast successfully, false otherwise (e.g., already voted, invalid election/candidate).
     */
    public boolean castVote(int voterUserId, int electionId, int candidateId, String ipAddress) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // 1. Check if the voter has already voted in this election
            if (hasVoted(conn, voterUserId, electionId)) {
                System.out.println("Voter " + voterUserId + " has already voted in election " + electionId);
                authService.addLog(conn, voterUserId, "Vote Failed", "User " + voterUserId + " tried to vote again in election " + electionId + ".");
                return false;
            }

            // 2. Check if election is active and candidate is approved
            // This is a simplified check. A more robust system would fetch election status
            // and candidate approval status from DB.
            Election election = getElectionById(electionId);
            if (election == null || !election.getStatus().equals("ACTIVE")) {
                System.out.println("Election " + electionId + " is not active or does not exist.");
                authService.addLog(conn, voterUserId, "Vote Failed", "Attempt to vote in inactive/non-existent election " + electionId + ".");
                return false;
            }

            Candidate candidate = getCandidateById(candidateId);
            if (candidate == null || candidate.getElectionId() != electionId || !candidate.getApprovalStatus().equals("APPROVED")) {
                System.out.println("Candidate " + candidateId + " is not valid for election " + electionId + " or not approved.");
                authService.addLog(conn, voterUserId, "Vote Failed", "Attempt to vote for invalid/unapproved candidate " + candidateId + " in election " + electionId + ".");
                return false;
            }


            // 3. Cast the vote
            String sql = "INSERT INTO VOTES2 (VOTE_ID, VOTER_USER_ID, ELECTION_ID, CANDIDATE_ID, VOTE_TIMESTAMP, IP_ADDRESS) " +
                         "VALUES (VOTE_SEQ2.NEXTVAL, ?, ?, ?, SYSTIMESTAMP, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, voterUserId);
            pstmt.setInt(2, electionId);
            pstmt.setInt(3, candidateId);
            pstmt.setString(4, ipAddress);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                conn.commit(); // Commit the transaction
                success = true;
                authService.addLog(conn, voterUserId, "Vote Cast Success", "Voter " + voterUserId + " cast vote for candidate " + candidateId + " in election " + electionId + ".");
            } else {
                conn.rollback(); // Rollback if insert failed
                authService.addLog(conn, voterUserId, "Vote Failed", "Database insert failed for vote by " + voterUserId + " in election " + electionId + ".");
            }
        } catch (SQLException e) {
            System.err.println("Error casting vote for user " + voterUserId + ": " + e.getMessage());
            e.printStackTrace();
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                System.err.println("Rollback failed: " + ex.getMessage());
            }
            authService.addLog(null, voterUserId, "Vote Error", "Database error casting vote for user " + voterUserId + ": " + e.getMessage());
        } finally {
            DBConnection.closeStatement(pstmt);
            DBConnection.closeConnection(conn);
        }
        return success;
    }

    /**
     * Checks if a voter has already cast a vote in a specific election.
     * @param conn An existing database connection.
     * @param voterUserId The ID of the voter.
     * @param electionId The ID of the election.
     * @return true if the voter has already voted, false otherwise.
     */
    public boolean hasVoted(Connection conn, int voterUserId, int electionId) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean voted = false;

        String sql = "SELECT COUNT(*) FROM VOTES2 WHERE VOTER_USER_ID = ? AND ELECTION_ID = ?";
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, voterUserId);
            pstmt.setInt(2, electionId);
            rs = pstmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                voted = true;
            }
        } finally {
            DBConnection.closeResultSet(rs);
            DBConnection.closeStatement(pstmt);
            // Do NOT close connection, it's passed from castVote
        }
        return voted;
    }

    /**
     * Calculates election results (vote counts for each candidate).
     * @param electionId The ID of the election.
     * @return A map where keys are Candidate objects and values are their vote counts,
     * or null if election not found or no votes.
     */
    public Map<Candidate, Integer> getElectionResults(int electionId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Map<Candidate, Integer> results = new LinkedHashMap<>(); // LinkedHashMap to maintain insertion order (useful if you sort later)

        // Ensure election is completed or active for results (depends on requirements, usually completed)
        Election election = getElectionById(electionId);
        if (election == null) {
            System.out.println("Election ID " + electionId + " not found for results.");
            return null;
        }
        System.out.println("Before the query");
        // Only show results for approved candidates
//        String sql = "SELECT c.CANDIDATE_ID, c.USER_ID, u.USERNAME, u.FULL_NAME, c.PARTY_AFFILIATION, c.MANIFESTO, " +
//                     "COUNT(v.VOTE_ID) AS VOTE_COUNT " +
//                     "FROM CANDIDATES2 c " +
//                     "JOIN USERS2 u ON c.USER_ID = u.USER_ID " +
//                     "LEFT JOIN VOTES2 v ON c.CANDIDATE_ID = v.CANDIDATE_ID AND c.ELECTION_ID = v.ELECTION_ID " +
//                     "WHERE c.ELECTION_ID = ? AND c.APPROVAL_STATUS = 'APPROVED' " +
//                     "GROUP BY c.CANDIDATE_ID, c.USER_ID, u.USERNAME, u.FULL_NAME, c.PARTY_AFFILIATION, c.MANIFESTO " +
//                     "ORDER BY VOTE_COUNT DESC, u.FULL_NAME ASC"; // Order by votes, then name
        
        String sql = "WITH CandidateVotes AS (" +
                "    SELECT " +
                "        c.CANDIDATE_ID, " +
                "        COUNT(v.VOTE_ID) AS VOTE_COUNT " +
                "    FROM " +
                "        CANDIDATES2 c " +
                "    LEFT JOIN " +
                "        VOTES2 v ON c.CANDIDATE_ID = v.CANDIDATE_ID AND c.ELECTION_ID = v.ELECTION_ID " +
                "    WHERE " +
                "        c.ELECTION_ID = ? " + // Placeholder for election ID
                "        AND c.APPROVAL_STATUS = 'APPROVED' " +
                "    GROUP BY " +
                "        c.CANDIDATE_ID " +
                ") " +
                "SELECT " +
                "    c.CANDIDATE_ID, " +
                "    c.USER_ID, " +
                "    u.USERNAME, " +
                "    u.FULL_NAME, " +
                "    c.PARTY_AFFILIATION, " +
                "    c.MANIFESTO, " + // MANIFESTO (CLOB) can now be selected directly
                "    cv.VOTE_COUNT " +
                "FROM " +
                "    CANDIDATES2 c " +
                "JOIN " +
                "    USERS2 u ON c.USER_ID = u.USER_ID " +
                "JOIN " +
                "    CandidateVotes cv ON c.CANDIDATE_ID = cv.CANDIDATE_ID " +
                "ORDER BY " +
                "    cv.VOTE_COUNT DESC, u.FULL_NAME ASC";

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, electionId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Candidate candidate = new Candidate();
                candidate.setCandidateId(rs.getInt("CANDIDATE_ID"));
                candidate.setUserId(rs.getInt("USER_ID"));
                candidate.setUserName(rs.getString("USERNAME"));
                candidate.setUserFullName(rs.getString("FULL_NAME"));
                candidate.setPartyAffiliation(rs.getString("PARTY_AFFILIATION"));
                candidate.setManifesto(rs.getString("MANIFESTO"));
                candidate.setVoteCount(rs.getInt("VOTE_COUNT"));
                results.put(candidate, candidate.getVoteCount());
                System.out.println(results);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching election results for election " + electionId + ": " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeResultSet(rs);
            DBConnection.closeStatement(pstmt);
            DBConnection.closeConnection(conn);
        }
        System.out.println(results);
        return results;
    }
}