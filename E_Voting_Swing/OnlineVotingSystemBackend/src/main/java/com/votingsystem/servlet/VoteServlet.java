// src/com/votingsystem/servlet/VoteServlet.java
package com.votingsystem.servlet;

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
import org.json.JSONObject;

@WebServlet("/vote")
public class VoteServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ElectionService electionService = new ElectionService();
    private AuthService authService = new AuthService(); // For logging

    private static final int VOTER_ROLE_ID = 2;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject jsonResponse = new JSONObject();

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loggedInUser") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            jsonResponse.put("success", false).put("message", "User not logged in.");
            out.print(jsonResponse.toString());
            return;
        }

        User currentUser = (User) session.getAttribute("loggedInUser");
        int currentUserId = currentUser.getUserId();
        int currentUserRoleId = currentUser.getRoleId();

        // RBAC Check: Only Voters can cast votes
        if (currentUserRoleId != VOTER_ROLE_ID) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            jsonResponse.put("success", false).put("message", "Access denied. Only registered voters can cast votes.");
            out.print(jsonResponse.toString());
            authService.addLog(null, currentUserId, "Unauthorized Vote Attempt", "User " + currentUserId + " (Role " + currentUserRoleId + ") attempted to cast a vote.");
            return;
        }

        // 2FA Check: Ensure OTP is verified for this session
//        Boolean otpVerified = (Boolean) session.getAttribute("otpVerified");
//        if (otpVerified == null || !otpVerified) {
//            response.setStatus(HttpServletResponse.SC_FORBIDDEN); // Or 403 Forbidden
//            jsonResponse.put("success", false).put("message", "2-Step Verification required. Please verify your identity first.");
//            out.print(jsonResponse.toString());
//            authService.addLog(null, currentUserId, "2FA Required for Vote", "User " + currentUserId + " attempted to vote without OTP verification.");
//            return;
//        }

        String electionIdParam = request.getParameter("electionId");
        String candidateIdParam = request.getParameter("candidateId");

        if (electionIdParam == null || electionIdParam.trim().isEmpty() ||
            candidateIdParam == null || candidateIdParam.trim().isEmpty()) {
            jsonResponse.put("success", false).put("message", "Election ID and Candidate ID are required.");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(jsonResponse.toString());
            return;
        }

        try {
            int electionId = Integer.parseInt(electionIdParam);
            int candidateId = Integer.parseInt(candidateIdParam);
            String ipAddress = request.getRemoteAddr(); // Capture client IP address

            boolean voteSuccess = electionService.castVote(currentUserId, electionId, candidateId, ipAddress);

            if (voteSuccess) {
                jsonResponse.put("success", true);
                jsonResponse.put("message", "Your vote has been cast successfully!");
            } else {
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Failed to cast vote. You might have already voted, or the election/candidate is invalid.");
            }
        } catch (NumberFormatException e) {
            jsonResponse.put("success", false).put("message", "Invalid ID format: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            authService.addLog(null, currentUserId, "API Error", "Invalid ID format in VoteServlet: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error in VoteServlet: " + e.getMessage());
            e.printStackTrace();
            jsonResponse.put("success", false).put("message", "An unexpected error occurred: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            authService.addLog(null, currentUserId, "Server Error", "Error in VoteServlet: " + e.getMessage());
        }
        out.print(jsonResponse.toString());
    }
}