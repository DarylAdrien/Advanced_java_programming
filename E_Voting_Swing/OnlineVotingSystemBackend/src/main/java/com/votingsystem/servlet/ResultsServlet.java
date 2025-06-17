// src/com/votingsystem/servlet/ResultsServlet.java
package com.votingsystem.servlet;

import com.votingsystem.model.Candidate;
import com.votingsystem.model.User;
import com.votingsystem.service.ElectionService;
import com.votingsystem.service.AuthService; // For logging

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet("/results")
public class ResultsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ElectionService electionService = new ElectionService();
    private AuthService authService = new AuthService(); // For logging

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject jsonResponse = new JSONObject();

        HttpSession session = request.getSession(false); // Can be null, results are public
        User currentUser = (session != null) ? (User) session.getAttribute("loggedInUser") : null;
        Integer currentUserId = (currentUser != null) ? currentUser.getUserId() : null; // Can be null if not logged in

        String electionIdParam = request.getParameter("electionId");

        if (electionIdParam == null || electionIdParam.trim().isEmpty()) {
            jsonResponse.put("success", false).put("message", "Election ID is required to view results.");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(jsonResponse.toString());
            return;
        }

        try {
            int electionId = Integer.parseInt(electionIdParam);
            Map<Candidate, Integer> results = electionService.getElectionResults(electionId);
            System.out.println(results);

            if (results != null && !results.isEmpty()) {
                JSONArray resultArray = new JSONArray();
                for (Map.Entry<Candidate, Integer> entry : results.entrySet()) {
                    Candidate candidate = entry.getKey();
                    JSONObject candidateResultJson = new JSONObject();
                    candidateResultJson.put("candidateId", candidate.getCandidateId());
                    candidateResultJson.put("userId", candidate.getUserId());
                    candidateResultJson.put("fullName", candidate.getUserFullName());
                    candidateResultJson.put("partyAffiliation", candidate.getPartyAffiliation());
                    candidateResultJson.put("voteCount", entry.getValue());
                    resultArray.put(candidateResultJson);
                }
                jsonResponse.put("success", true);
                jsonResponse.put("electionId", electionId);
                jsonResponse.put("results", resultArray);
                authService.addLog(null, currentUserId, "View Election Results", "User " + (currentUserId != null ? currentUserId : "Guest") + " viewed results for election " + electionId + ".");
            } else {
                jsonResponse.put("success", false);
                jsonResponse.put("message", "No results found for election ID " + electionId + " or election does not exist/has no approved candidates.");
                // Set appropriate status if election not found etc.
                // response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (NumberFormatException e) {
            jsonResponse.put("success", false).put("message", "Invalid Election ID format: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            authService.addLog(null, currentUserId, "API Error", "Invalid Election ID format in ResultsServlet: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error in ResultsServlet: " + e.getMessage());
            e.printStackTrace();
            jsonResponse.put("success", false).put("message", "An unexpected error occurred: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            authService.addLog(null, currentUserId, "Server Error", "Error in ResultsServlet: " + e.getMessage());
        }
        out.print(jsonResponse.toString());
    }
}