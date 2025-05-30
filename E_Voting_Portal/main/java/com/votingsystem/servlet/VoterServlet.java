// src/main/java/com/votingsystem/servlet/VoterServlet.java
package com.votingsystem.servlet;

import com.votingsystem.dao.CandidateDAO;
import com.votingsystem.dao.ConstituencyDAO;
import com.votingsystem.dao.ElectionDAO;
import com.votingsystem.dao.SystemLogDAO;
import com.votingsystem.dao.UserDAO;
import com.votingsystem.dao.VoteDAO;
import com.votingsystem.model.Candidate;
import com.votingsystem.model.Constituency;
import com.votingsystem.model.Election;
import com.votingsystem.model.SystemLog;
import com.votingsystem.model.User;
import com.votingsystem.model.Vote;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

/**
 * Servlet for handling voter-specific actions in the voting system.
 * This includes viewing active elections and casting votes.
 */
@WebServlet("/voter")
public class VoterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ElectionDAO electionDAO;
    private CandidateDAO candidateDAO;
    private ConstituencyDAO constituencyDAO;
    private VoteDAO voteDAO;
    private UserDAO userDAO; // To get voter's constituency
    private SystemLogDAO systemLogDAO;

    /**
     * Initializes the servlet and its DAOs.
     */
    @Override
    public void init() throws ServletException {
        super.init();
        electionDAO = new ElectionDAO();
        candidateDAO = new CandidateDAO();
        voteDAO = new VoteDAO();
        userDAO = new UserDAO();
        systemLogDAO = new SystemLogDAO();
        constituencyDAO = new ConstituencyDAO();
    }

    /**
     * Helper method to check if the user is a voter.
     *
     * @param session The current HttpSession.
     * @return true if the user is logged in and has the 'VOTER' role, false otherwise.
     */
    private boolean isVoter(HttpSession session) {
        return session != null && "VOTER".equals(session.getAttribute("role"));
    }

    /**
     * Handles GET requests to display the voter dashboard, active elections,
     * and candidates for a selected election.
     *
     * @param request The HttpServletRequest object.
     * @param response The HttpServletResponse object.
     * @throws ServletException If a servlet-specific error occurs.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        String ipAddress = request.getRemoteAddr();
        Integer voterUserId = (Integer) session.getAttribute("userId");

        if (!isVoter(session)) {
            response.sendRedirect("index.jsp"); // Redirect to login if not a voter
            systemLogDAO.addSystemLog(new SystemLog(voterUserId, "ACCESS_DENIED", "Unauthorized access attempt to voter dashboard from IP: " + ipAddress, ipAddress));
            return;
        }

        try {
            // Fetch active elections
            List<Election> activeElections = electionDAO.getElectionsByStatus("ACTIVE");
            request.setAttribute("activeElections", activeElections);

            // Get the voter's constituency
            User voter = userDAO.getUserById(voterUserId);
            if (voter == null) {
                request.setAttribute("errorMessage", "Voter profile not found. Please re-login.");
                response.sendRedirect("index.jsp");
                systemLogDAO.addSystemLog(new SystemLog(voterUserId, "VOTER_ERROR", "Voter profile not found for ID: " + voterUserId, ipAddress));
                return;
            }
            int voterConstituencyId = voter.getConstituencyId();
            request.setAttribute("voterConstituencyId", voterConstituencyId);
            List<Constituency> allConstituency = constituencyDAO.getAllConstituencies();
            request.setAttribute("constituency", allConstituency);

            // Check if an election ID is selected to view candidates
            String selectedElectionIdStr = request.getParameter("electionId");
            if (selectedElectionIdStr != null && !selectedElectionIdStr.isEmpty()) {
                int selectedElectionId = Integer.parseInt(selectedElectionIdStr);
                Election selectedElection = electionDAO.getElectionById(selectedElectionId);

                if (selectedElection != null && "ACTIVE".equals(selectedElection.getStatus())) {
                    // Check if voter has already voted in this election
                    boolean hasVoted = voteDAO.hasVoted(voterUserId, selectedElectionId);
                    request.setAttribute("hasVoted", hasVoted);

                    // Fetch candidates for the selected election and the voter's constituency
                    List<Candidate> candidatesForConstituency = candidateDAO.getCandidatesByElectionAndConstituency(selectedElectionId, voterConstituencyId);
                    request.setAttribute("selectedElection", selectedElection);
                    request.setAttribute("candidatesForConstituency", candidatesForConstituency);

                    systemLogDAO.addSystemLog(new SystemLog(voterUserId, "VIEW_ELECTION", "Voter viewed election ID: " + selectedElectionId + " in constituency: " + voterConstituencyId, ipAddress));
                } else {
                    request.setAttribute("errorMessage", "Selected election is not active or does not exist.");
                    systemLogDAO.addSystemLog(new SystemLog(voterUserId, "VIEW_ELECTION_FAILED", "Voter attempted to view invalid election ID: " + selectedElectionId, ipAddress));
                }
            }

            request.getRequestDispatcher("voter/dashboard.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            System.err.println("Invalid election ID format: " + e.getMessage());
            systemLogDAO.addSystemLog(new SystemLog(voterUserId, "VOTER_ERROR", "Invalid election ID format: " + e.getMessage(), ipAddress));
            request.setAttribute("errorMessage", "Invalid election ID provided.");
            request.getRequestDispatcher("voter/dashboard.jsp").forward(request, response);
        } catch (Exception e) {
            System.err.println("Error loading voter dashboard: " + e.getMessage());
            e.printStackTrace();
            systemLogDAO.addSystemLog(new SystemLog(voterUserId, "VOTER_ERROR", "Error loading voter dashboard: " + e.getMessage(), ipAddress));
            request.setAttribute("errorMessage", "An error occurred while loading your dashboard.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }

    /**
     * Handles POST requests for casting a vote.
     *
     * @param request The HttpServletRequest object.
     * @param response The HttpServletResponse object.
     * @throws ServletException If a servlet-specific error occurs.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        String ipAddress = request.getRemoteAddr();
        Integer voterUserId = (Integer) session.getAttribute("userId");

        if (!isVoter(session)) {
            response.sendRedirect("index.jsp"); // Redirect to login if not a voter
            systemLogDAO.addSystemLog(new SystemLog(voterUserId, "ACCESS_DENIED", "Unauthorized POST attempt to voter servlet from IP: " + ipAddress, ipAddress));
            return;
        }

        String action = request.getParameter("action");
        String message = null;

        if ("castVote".equals(action)) {
            String candidateIdStr = request.getParameter("candidateId");
            String electionIdStr = request.getParameter("electionId");

            if (candidateIdStr == null || candidateIdStr.isEmpty() || electionIdStr == null || electionIdStr.isEmpty()) {
                message = "Invalid vote submission. Please select a candidate and election.";
                systemLogDAO.addSystemLog(new SystemLog(voterUserId, "VOTE_FAILED", "Missing candidate or election ID for voter: " + voterUserId, ipAddress));
                request.setAttribute("errorMessage", message);
                doGet(request, response);
                return;
            }

            try {
                int candidateId = Integer.parseInt(candidateIdStr);
                int electionId = Integer.parseInt(electionIdStr);

                // Re-check if the election is active and voter hasn't voted
                Election election = electionDAO.getElectionById(electionId);
                if (election == null || !"ACTIVE".equals(election.getStatus())) {
                    message = "Voting is not currently active for this election.";
                    systemLogDAO.addSystemLog(new SystemLog(voterUserId, "VOTE_FAILED", "Attempted to vote in inactive election ID: " + electionId, ipAddress));
                    request.setAttribute("errorMessage", message);
                    doGet(request, response);
                    return;
                }

                if (voteDAO.hasVoted(voterUserId, electionId)) {
                    message = "You have already voted in this election.";
                    systemLogDAO.addSystemLog(new SystemLog(voterUserId, "VOTE_FAILED", "Duplicate vote attempt for voter ID: " + voterUserId + " in election ID: " + electionId, ipAddress));
                    request.setAttribute("errorMessage", message);
                    doGet(request, response);
                    return;
                }

                // Verify candidate belongs to the voter's constituency for this election
                User voter = userDAO.getUserById(voterUserId);
                Candidate chosenCandidate = candidateDAO.getCandidateById(candidateId);

                if (voter == null || chosenCandidate == null ||
                    voter.getConstituencyId() != chosenCandidate.getConstituencyId() ||
                    chosenCandidate.getElectionId() != electionId) {
                    message = "Invalid candidate or constituency mismatch. Your vote could not be recorded.";
                    systemLogDAO.addSystemLog(new SystemLog(voterUserId, "VOTE_FAILED", "Invalid candidate or constituency mismatch for voter ID: " + voterUserId + ", candidate ID: " + candidateId, ipAddress));
                    request.setAttribute("errorMessage", message);
                    doGet(request, response);
                    return;
                }

                Vote newVote = new Vote(voterUserId, candidateId, electionId, ipAddress);
                int voteId = voteDAO.addVote(newVote);

                if (voteId != -1) {
                    message = "Your vote has been cast successfully!";
                    systemLogDAO.addSystemLog(new SystemLog(voterUserId, "VOTE_CAST", "Voter ID: " + voterUserId + " cast vote for candidate ID: " + candidateId + " in election ID: " + electionId, ipAddress));
                    request.setAttribute("successMessage", message);
                } else {
                    message = "Failed to cast your vote. Please try again.";
                    systemLogDAO.addSystemLog(new SystemLog(voterUserId, "VOTE_FAILED", "Database error while casting vote for voter ID: " + voterUserId, ipAddress));
                    request.setAttribute("errorMessage", message);
                }
            } catch (NumberFormatException e) {
                message = "Invalid candidate or election ID format.";
                System.err.println("NumberFormatException in VoterServlet (castVote): " + e.getMessage());
                systemLogDAO.addSystemLog(new SystemLog(voterUserId, "VOTE_FAILED", "Invalid ID format in castVote: " + e.getMessage(), ipAddress));
                request.setAttribute("errorMessage", message);
            } catch (Exception e) {
                message = "An unexpected error occurred while casting your vote: " + e.getMessage();
                System.err.println("General Exception in VoterServlet (castVote): " + e.getMessage());
                e.printStackTrace();
                systemLogDAO.addSystemLog(new SystemLog(voterUserId, "VOTE_FAILED", "Unexpected error in castVote: " + e.getMessage(), ipAddress));
                request.setAttribute("errorMessage", message);
            }
        } else {
            message = "Unknown action for voter: " + action;
            systemLogDAO.addSystemLog(new SystemLog(voterUserId, "VOTER_ERROR", "Unknown action received: " + action, ipAddress));
            request.setAttribute("errorMessage", message);
        }

        // Always redirect back to the dashboard after a POST action
        response.sendRedirect(request.getContextPath() + "/voter?electionId=" + request.getParameter("electionId") + (request.getAttribute("successMessage") != null ? "&message=" + request.getAttribute("successMessage") : "") + (request.getAttribute("errorMessage") != null ? "&error=" + request.getAttribute("errorMessage") : ""));
        // Note: Using sendRedirect for POST-redirect-GET pattern to prevent double submission
        // and passing messages via URL parameters (or session attributes if messages are complex).
        // For simplicity, we'll rely on doGet to re-fetch and display messages set as attributes.
        // A better approach for messages across redirects is to use session attributes and then remove them.
    }
}
