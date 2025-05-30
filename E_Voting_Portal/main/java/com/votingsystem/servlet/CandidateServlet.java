// src/main/java/com/votingsystem/servlet/CandidateServlet.java
package com.votingsystem.servlet;

import com.votingsystem.dao.CandidateDAO;
import com.votingsystem.dao.ConstituencyDAO;
import com.votingsystem.dao.ElectionDAO;
import com.votingsystem.dao.SystemLogDAO;
import com.votingsystem.dao.UserDAO;
import com.votingsystem.model.Candidate;
import com.votingsystem.model.Constituency;
import com.votingsystem.model.Election;
import com.votingsystem.model.SystemLog;
import com.votingsystem.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * Servlet for handling candidate-specific actions in the voting system.
 * This includes viewing their profile, registering for an election as a candidate,
 * and managing their manifesto.
 */
@WebServlet("/candidate")
public class CandidateServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private CandidateDAO candidateDAO;
    private ElectionDAO electionDAO;
    private UserDAO userDAO;
    private SystemLogDAO systemLogDAO;
    private ConstituencyDAO constituencyDAO;
    /**
     * Initializes the servlet and its DAOs.
     */
    @Override
    public void init() throws ServletException {
        super.init();
        candidateDAO = new CandidateDAO();
        electionDAO = new ElectionDAO();
        userDAO = new UserDAO();
        systemLogDAO = new SystemLogDAO();
        constituencyDAO = new ConstituencyDAO();
    }

    /**
     * Helper method to check if the user is a candidate.
     *
     * @param session The current HttpSession.
     * @return true if the user is logged in and has the 'CANDIDATE' role, false otherwise.
     */
    private boolean isCandidate(HttpSession session) {
        return session != null && "CANDIDATE".equals(session.getAttribute("role"));
    }

    /**
     * Handles GET requests to display the candidate dashboard,
     * including their current candidate profile (if any) and available elections to apply for.
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
        Integer candidateUserId = (Integer) session.getAttribute("userId");

        if (!isCandidate(session)) {
            response.sendRedirect("index.jsp"); // Redirect to login if not a candidate
            systemLogDAO.addSystemLog(new SystemLog(candidateUserId, "ACCESS_DENIED", "Unauthorized access attempt to candidate dashboard from IP: " + ipAddress, ipAddress));
            return;
        }

        try {
            // Get the candidate's user details
            User currentUser = userDAO.getUserById(candidateUserId);
            if (currentUser == null) {
                request.setAttribute("errorMessage", "Candidate profile not found. Please re-login.");
                response.sendRedirect("index.jsp");
                systemLogDAO.addSystemLog(new SystemLog(candidateUserId, "CANDIDATE_ERROR", "Candidate user profile not found for ID: " + candidateUserId, ipAddress));
                return;
            }
            request.setAttribute("currentUser", currentUser);

            // Fetch active elections that the candidate can apply for
            List<Election> upcomingElections = electionDAO.getElectionsByStatus("UPCOMING");
            request.setAttribute("upcomingElections", upcomingElections);

            // Fetch any existing candidate profiles for this user
            // A user can be a candidate for multiple elections/constituencies over time
            List<Candidate> existingCandidateProfiles = new java.util.ArrayList<>();
            for (Election election : electionDAO.getAllElections()) { // Check all elections
                Candidate candidateProfile = candidateDAO.getCandidateByUserDetails(candidateUserId, election.getElectionId(), currentUser.getConstituencyId());
                if (candidateProfile != null) {
                    existingCandidateProfiles.add(candidateProfile);
                }
            }
            System.out.println(existingCandidateProfiles);
            request.setAttribute("existingCandidateProfiles", existingCandidateProfiles);
            
            systemLogDAO.addSystemLog(new SystemLog(candidateUserId, "VIEW_DASHBOARD", "Candidate viewed dashboard: " + currentUser.getUsername(), ipAddress));
            List<Election> allElections = electionDAO.getAllElections();
            request.setAttribute("elections", allElections);
            List<Constituency> allConstituency = constituencyDAO.getAllConstituencies();
            request.setAttribute("constituency", allConstituency);
            request.getRequestDispatcher("candidate/dashboard.jsp").forward(request, response);

        } catch (Exception e) {
            System.err.println("Error loading candidate dashboard: " + e.getMessage());
            e.printStackTrace();
            systemLogDAO.addSystemLog(new SystemLog(candidateUserId, "CANDIDATE_ERROR", "Error loading candidate dashboard: " + e.getMessage(), ipAddress));
            request.setAttribute("errorMessage", "An error occurred while loading your dashboard.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }

    /**
     * Handles POST requests for candidate actions, such as applying for an election
     * or updating their manifesto.
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
        Integer candidateUserId = (Integer) session.getAttribute("userId");

        if (!isCandidate(session)) {
            response.sendRedirect("index.jsp"); // Redirect to login if not a candidate
            systemLogDAO.addSystemLog(new SystemLog(candidateUserId, "ACCESS_DENIED", "Unauthorized POST attempt to candidate servlet from IP: " + ipAddress, ipAddress));
            return;
        }

        String action = request.getParameter("action");
        String message = null;
        boolean success = false;

        if (action == null || action.trim().isEmpty()) {
            message = "No action specified.";
            systemLogDAO.addSystemLog(new SystemLog(candidateUserId, "CANDIDATE_ERROR", "No action specified in POST request.", ipAddress));
            request.setAttribute("errorMessage", message);
            doGet(request, response);
            return;
        }

        try {
            User currentUser = userDAO.getUserById(candidateUserId);
            if (currentUser == null) {
                message = "Candidate user profile not found. Please re-login.";
                systemLogDAO.addSystemLog(new SystemLog(candidateUserId, "CANDIDATE_ERROR", "Candidate user profile not found during POST action: " + action, ipAddress));
                request.setAttribute("errorMessage", message);
                response.sendRedirect("index.jsp");
                return;
            }

            switch (action) {
                case "applyForElection":
                    String electionIdStr = request.getParameter("electionId");
                    String party = request.getParameter("party");
                    String symbolUrl = request.getParameter("symbolUrl");
                    String manifesto = request.getParameter("manifesto");

                    if (electionIdStr == null || electionIdStr.isEmpty() ||
                        party == null || party.trim().isEmpty() ||
                        manifesto == null || manifesto.trim().isEmpty()) {
                        message = "All fields are required to apply for an election.";
                        systemLogDAO.addSystemLog(new SystemLog(candidateUserId, "CANDIDATE_APPLICATION_FAILED", "Missing fields for election application.", ipAddress));
                        break;
                    }

                    int electionId = Integer.parseInt(electionIdStr);
                    int constituencyId = currentUser.getConstituencyId(); // Candidate applies for their registered constituency

                    // Check if already applied for this election in this constituency
                    Candidate existingApplication = candidateDAO.getCandidateByUserDetails(candidateUserId, electionId, constituencyId);
                    if (existingApplication != null) {
                        message = "You have already applied for this election in your constituency.";
                        systemLogDAO.addSystemLog(new SystemLog(candidateUserId, "CANDIDATE_APPLICATION_FAILED", "Duplicate application for election ID: " + electionId, ipAddress));
                        break;
                    }

                    Election election = electionDAO.getElectionById(electionId);
                    if (election == null || !"UPCOMING".equals(election.getStatus())) {
                        message = "You can only apply for upcoming elections.";
                        systemLogDAO.addSystemLog(new SystemLog(candidateUserId, "CANDIDATE_APPLICATION_FAILED", "Attempted to apply for non-upcoming election ID: " + electionId, ipAddress));
                        break;
                    }

                    Candidate newCandidate = new Candidate(candidateUserId, electionId, constituencyId, party, symbolUrl, manifesto, "PENDING");
                    int newCandidateId = candidateDAO.addCandidate(newCandidate);

                    if (newCandidateId != -1) {
                        message = "Application for election submitted successfully! Awaiting admin approval.";
                        success = true;
                        systemLogDAO.addSystemLog(new SystemLog(candidateUserId, "CANDIDATE_APPLIED", "Candidate applied for election ID: " + electionId, ipAddress));
                    } else {
                        message = "Failed to submit election application. Please try again.";
                        systemLogDAO.addSystemLog(new SystemLog(candidateUserId, "CANDIDATE_APPLICATION_FAILED", "Database error during election application.", ipAddress));
                    }
                    break;

                case "updateManifesto":
                    String candidateProfileIdStr = request.getParameter("candidateProfileId");
                    String updatedManifesto = request.getParameter("manifesto");
                    String updatedParty = request.getParameter("party");
                    String updatedSymbolUrl = request.getParameter("symbolUrl");

                    if (candidateProfileIdStr == null || candidateProfileIdStr.isEmpty() ||
                        updatedManifesto == null || updatedManifesto.trim().isEmpty() ||
                        updatedParty == null || updatedParty.trim().isEmpty()) {
                        message = "Manifesto and Party cannot be empty.";
                        systemLogDAO.addSystemLog(new SystemLog(candidateUserId, "MANIFESTO_UPDATE_FAILED", "Missing fields for manifesto update.", ipAddress));
                        break;
                    }

                    int candidateProfileId = Integer.parseInt(candidateProfileIdStr);
                    Candidate candidateToUpdate = candidateDAO.getCandidateById(candidateProfileId);

                    if (candidateToUpdate != null && candidateToUpdate.getUserId() == candidateUserId) {
                        candidateToUpdate.setManifesto(updatedManifesto);
                        candidateToUpdate.setParty(updatedParty);
                        candidateToUpdate.setSymbolUrl(updatedSymbolUrl); // Allow updating symbol URL
                        success = candidateDAO.updateCandidate(candidateToUpdate);
                        if (success) {
                            message = "Manifesto and profile updated successfully!";
                            systemLogDAO.addSystemLog(new SystemLog(candidateUserId, "MANIFESTO_UPDATED", "Candidate updated manifesto for profile ID: " + candidateProfileId, ipAddress));
                        } else {
                            message = "Failed to update manifesto. Please try again.";
                            systemLogDAO.addSystemLog(new SystemLog(candidateUserId, "MANIFESTO_UPDATE_FAILED", "Database error during manifesto update.", ipAddress));
                        }
                    } else {
                        message = "Candidate profile not found or you do not have permission to update it.";
                        systemLogDAO.addSystemLog(new SystemLog(candidateUserId, "MANIFESTO_UPDATE_FAILED", "Candidate profile not found or unauthorized update attempt for ID: " + candidateProfileId, ipAddress));
                    }
                    break;

                default:
                    message = "Unknown action: " + action;
                    systemLogDAO.addSystemLog(new SystemLog(candidateUserId, "CANDIDATE_ERROR", "Unknown action received: " + action, ipAddress));
                    break;
            }
        } catch (NumberFormatException e) {
            message = "Invalid ID format provided.";
            System.err.println("NumberFormatException in CandidateServlet: " + e.getMessage());
            systemLogDAO.addSystemLog(new SystemLog(candidateUserId, "CANDIDATE_ERROR", "Invalid ID format in action " + action + ": " + e.getMessage(), ipAddress));
        } catch (Exception e) {
            message = "An unexpected error occurred: " + e.getMessage();
            System.err.println("General Exception in CandidateServlet for action " + action + ": " + e.getMessage());
            e.printStackTrace();
            systemLogDAO.addSystemLog(new SystemLog(candidateUserId, "CANDIDATE_ERROR", "Unexpected error for action " + action + ": " + e.getMessage(), ipAddress));
        }

        // Set message and redirect back to dashboard
        if (success) {
            request.setAttribute("successMessage", message);
        } else {
            request.setAttribute("errorMessage", message);
        }
        doGet(request, response); // Re-fetch data and display dashboard
    }
}
