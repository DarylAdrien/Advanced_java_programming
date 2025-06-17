// src/com/votingsystem/servlet/CandidateManagementServlet.java
package com.votingsystem.servlet;

import com.votingsystem.model.Candidate;
import com.votingsystem.model.User;
import com.votingsystem.service.ElectionService;
import com.votingsystem.service.AuthService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet("/candidate")
public class CandidateManagementServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ElectionService electionService = new ElectionService();
    private AuthService authService = new AuthService(); // For logging

    private static final int ADMIN_ROLE_ID = 1;
    private static final int VOTER_ROLE_ID = 2;
    private static final int CANDIDATE_ROLE_ID = 3;

    // Helper to check user roles for specific actions
    private boolean checkRole(HttpSession session, HttpServletResponse response, PrintWriter out, int requiredRole) throws IOException {
        if (session == null || session.getAttribute("loggedInUser") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print(new JSONObject().put("success", false).put("message", "User not logged in.").toString());
            return false;
        }
        User currentUser = (User) session.getAttribute("loggedInUser");
        if (currentUser.getRoleId() != requiredRole && currentUser.getRoleId() != ADMIN_ROLE_ID) { // Admin can do anything
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            out.print(new JSONObject().put("success", false).put("message", "Access denied. Insufficient privileges.").toString());
            authService.addLog(null, currentUser.getUserId(), "Unauthorized Access", "User " + currentUser.getUsername() + " (Role " + currentUser.getRoleId() + ") attempted candidate action requiring role " + requiredRole + ".");
            return false;
        }
        return true;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject jsonResponse = new JSONObject();

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loggedInUser") == null) {
             response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
             out.print(new JSONObject().put("success", false).put("message", "User not logged in.").toString());
             return;
        }
        User currentUser = (User) session.getAttribute("loggedInUser");
        int currentUserId = currentUser.getUserId();
        int currentUserRoleId = currentUser.getRoleId();


        String action = request.getParameter("action"); // "listByElection", "listAll", "getById"

        try {
            if ("listByElection".equalsIgnoreCase(action)) {
                String electionIdParam = request.getParameter("electionId");
                if (electionIdParam == null || electionIdParam.trim().isEmpty()) {
                    jsonResponse.put("success", false).put("message", "Election ID is required to list candidates by election.");
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print(jsonResponse.toString());
                    return;
                }
                int electionId = Integer.parseInt(electionIdParam);
                boolean includePending = (currentUserRoleId == VOTER_ROLE_ID); // Admins see all, others only approved
                List<Candidate> candidates = electionService.getCandidatesForElection(electionId, includePending);
                JSONArray candidateArray = new JSONArray();
                for (Candidate c : candidates) {
                    JSONObject cJson = new JSONObject();
                    cJson.put("candidateId", c.getCandidateId());
                    cJson.put("userId", c.getUserId());
                    cJson.put("userName", c.getUserName());
                    cJson.put("userFullName", c.getUserFullName());
                    cJson.put("electionId", c.getElectionId());
                    cJson.put("electionName", c.getElectionName());
                    cJson.put("partyAffiliation", c.getPartyAffiliation());
                    cJson.put("manifesto", c.getManifesto());
                    cJson.put("approvalStatus", c.getApprovalStatus());
                    cJson.put("registrationDate", c.getRegistrationDate().toString());
                    cJson.put("voteCount", c.getVoteCount()); // This will be 0 unless populated by results
                    candidateArray.put(cJson);
                }
                jsonResponse.put("success", true);
                jsonResponse.put("candidates", candidateArray);
                authService.addLog(null, currentUserId, "List Candidates By Election", "User " + currentUserId + " listed candidates for election " + electionId + " (include pending: " + includePending + ").");

            } else if ("listAll".equalsIgnoreCase(action)) { // Typically for admin dashboard
//                if (currentUserRoleId != ADMIN_ROLE_ID) {
//                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
//                    out.print(new JSONObject().put("success", false).put("message", "Access denied. Admin privileges required to list all candidates.").toString());
//                    authService.addLog(null, currentUserId, "Unauthorized Access", "User " + currentUserId + " attempted to list all candidates.");
//                    return;
//                }
                List<Candidate> candidates = electionService.getAllCandidates(true); // Admin sees all including pending
                JSONArray candidateArray = new JSONArray();
                for (Candidate c : candidates) {
                    JSONObject cJson = new JSONObject();
                    cJson.put("candidateId", c.getCandidateId());
                    cJson.put("userId", c.getUserId());
                    cJson.put("userName", c.getUserName());
                    cJson.put("userFullName", c.getUserFullName());
                    cJson.put("electionId", c.getElectionId());
                    cJson.put("electionName", c.getElectionName());
                    cJson.put("partyAffiliation", c.getPartyAffiliation());
                    cJson.put("manifesto", c.getManifesto());
                    cJson.put("approvalStatus", c.getApprovalStatus());
                    cJson.put("registrationDate", c.getRegistrationDate().toString());
                    cJson.put("voteCount", c.getVoteCount());
                    candidateArray.put(cJson);
                }
                jsonResponse.put("success", true);
                jsonResponse.put("candidates", candidateArray);
                authService.addLog(null, currentUserId, "List All Candidates", "Admin " + currentUserId + " listed all candidates.");

            } else if ("getById".equalsIgnoreCase(action)) {
                String candidateIdParam = request.getParameter("candidateId");
                if (candidateIdParam == null || candidateIdParam.trim().isEmpty()) {
                    jsonResponse.put("success", false).put("message", "Candidate ID is required.");
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print(jsonResponse.toString());
                    return;
                }
                int candidateId = Integer.parseInt(candidateIdParam);
                Candidate candidate = electionService.getCandidateById(candidateId);
                System.out.println(candidate);
                if (candidate != null) {
                    JSONObject cJson = new JSONObject();
                    cJson.put("candidateId", candidate.getCandidateId());
                    cJson.put("userId", candidate.getUserId());
                    cJson.put("userName", candidate.getUserName());
                    cJson.put("userFullName", candidate.getUserFullName());
                    cJson.put("electionId", candidate.getElectionId());
                    cJson.put("electionName", candidate.getElectionName());
                    cJson.put("partyAffiliation", candidate.getPartyAffiliation());
                    cJson.put("manifesto", candidate.getManifesto());
                    cJson.put("approvalStatus", candidate.getApprovalStatus());
                    cJson.put("registrationDate", candidate.getRegistrationDate().toString());
                    cJson.put("voteCount", candidate.getVoteCount());
                    jsonResponse.put("success", true);
                    jsonResponse.put("candidate", cJson);
                    authService.addLog(null, currentUserId, "Get Candidate By ID", "User " + currentUserId + " viewed candidate ID: " + candidateId);
                } else {
                    jsonResponse.put("success", false);
                    jsonResponse.put("message", "Candidate not found.");
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                }
            }
            else {
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Invalid action for GET request.");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (NumberFormatException e) {
            jsonResponse.put("success", false).put("message", "Invalid ID format: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            authService.addLog(null, currentUserId, "API Error", "Invalid ID format in CandidateManagementServlet GET: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error in CandidateManagementServlet (GET): " + e.getMessage());
            e.printStackTrace();
            jsonResponse.put("success", false).put("message", "An unexpected error occurred: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            authService.addLog(null, currentUserId, "Server Error", "Error in CandidateManagementServlet GET: " + e.getMessage());
        }
        out.print(jsonResponse.toString());
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject jsonResponse = new JSONObject();

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loggedInUser") == null) {
             response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
             out.print(new JSONObject().put("success", false).put("message", "User not logged in.").toString());
             return;
        }
        User currentUser = (User) session.getAttribute("loggedInUser");
        int currentUserId = currentUser.getUserId();
        int currentUserRoleId = currentUser.getRoleId();

        String action = request.getParameter("action"); // "register", "approve", "reject"

        try {
            if ("register".equalsIgnoreCase(action)) {
                // Check if the current user is a Candidate or Admin trying to register someone
//                if (currentUserRoleId != CANDIDATE_ROLE_ID && currentUserRoleId != ADMIN_ROLE_ID) {
//                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
//                    out.print(new JSONObject().put("success", false).put("message", "Access denied. Only Candidates or Admins can register candidates.").toString());
//                    authService.addLog(null, currentUserId, "Unauthorized Candidate Registration", "User " + currentUserId + " (Role " + currentUserRoleId + ") attempted candidate registration.");
//                    return;
//                }

                String electionIdParam = request.getParameter("electionId");
                String partyAffiliation = request.getParameter("partyAffiliation");
                String manifesto = request.getParameter("manifesto");
                String candidateUserIdParam = request.getParameter("candidateUserId"); // Optional: for admin registering another user

                int userIdToRegister = currentUserId; // Default to self-registration
                if (currentUserRoleId == ADMIN_ROLE_ID && candidateUserIdParam != null && !candidateUserIdParam.trim().isEmpty()) {
                    userIdToRegister = Integer.parseInt(candidateUserIdParam); // Admin can specify which user to register
                } else if (currentUserRoleId != ADMIN_ROLE_ID && candidateUserIdParam != null && !candidateUserIdParam.trim().isEmpty() && Integer.parseInt(candidateUserIdParam) != currentUserId) {
                    jsonResponse.put("success", false).put("message", "You can only register yourself as a candidate.");
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    out.print(jsonResponse.toString());
                    return;
                }

                if (electionIdParam == null || electionIdParam.trim().isEmpty()) {
                    jsonResponse.put("success", false).put("message", "Election ID, party affiliation, and manifesto are required.");
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print(jsonResponse.toString());
                    return;
                }
                int electionId = Integer.parseInt(electionIdParam);


                Candidate newCandidate = new Candidate();
                newCandidate.setUserId(userIdToRegister);
                newCandidate.setElectionId(electionId);
                newCandidate.setPartyAffiliation(partyAffiliation);
                newCandidate.setManifesto(manifesto);

                Candidate registeredCandidate = electionService.registerCandidate(newCandidate, currentUserId);
                if (registeredCandidate != null) {
                    jsonResponse.put("success", true);
                    jsonResponse.put("message", "Candidate registration successful. Awaiting admin approval.");
                    jsonResponse.put("candidateId", registeredCandidate.getCandidateId());
                } else {
                    jsonResponse.put("success", false);
                    jsonResponse.put("message", "Candidate registration failed. User might already be a candidate for this election.");
                }

            } else if ("approve".equalsIgnoreCase(action) || "reject".equalsIgnoreCase(action)) {
                // Only Admins can approve/reject
//                if (currentUserRoleId != ADMIN_ROLE_ID) {
//                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
//                    out.print(new JSONObject().put("success", false).put("message", "Access denied. Admin privileges required for candidate approval/rejection.").toString());
//                    authService.addLog(null, currentUserId, "Unauthorized Candidate Approval", "User " + currentUserId + " attempted candidate approval/rejection.");
//                    return;
//                }

                String candidateIdParam = request.getParameter("candidateId");
                if (candidateIdParam == null || candidateIdParam.trim().isEmpty()) {
                    jsonResponse.put("success", false).put("message", "Candidate ID is required for approval/rejection.");
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print(jsonResponse.toString());
                    return;
                }
                int candidateId = Integer.parseInt(candidateIdParam);
                String newStatus = "approve".equalsIgnoreCase(action) ? "APPROVED" : "REJECTED";

                boolean updated = electionService.updateCandidateApprovalStatus(candidateId, newStatus, currentUserId);
                if (updated) {
                    jsonResponse.put("success", true);
                    jsonResponse.put("message", "Candidate ID " + candidateId + " status updated to " + newStatus + ".");
                } else {
                    jsonResponse.put("success", false);
                    jsonResponse.put("message", "Failed to update candidate ID " + candidateId + " status.");
                }

            } else {
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Invalid action for POST request.");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (NumberFormatException e) {
            jsonResponse.put("success", false).put("message", "Invalid ID format: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            authService.addLog(null, currentUserId, "API Error", "Invalid ID format in CandidateManagementServlet POST: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error in CandidateManagementServlet (POST): " + e.getMessage());
            e.printStackTrace();
            jsonResponse.put("success", false).put("message", "An unexpected error occurred: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            authService.addLog(null, currentUserId, "Server Error", "Error in CandidateManagementServlet POST: " + e.getMessage());
        }
        out.print(jsonResponse.toString());
    }
}