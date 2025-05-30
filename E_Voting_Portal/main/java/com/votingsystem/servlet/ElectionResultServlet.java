package com.votingsystem.servlet;

import com.votingsystem.dao.ElectionDAO;
import com.votingsystem.dao.UserDAO;
import com.votingsystem.dao.CandidateDAO;
import com.votingsystem.dao.VoteDAO; // You'll need a VoteDAO to get vote counts
import com.votingsystem.dao.SystemLogDAO;
import com.votingsystem.model.Election;
import com.votingsystem.model.SystemLog;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.ArrayList; // Added for list initialization

@WebServlet("/admin/electionResults")
public class ElectionResultServlet extends HttpServlet {
    private ElectionDAO electionDAO;
    private VoteDAO voteDAO; // DAO for handling votes and results
    private SystemLogDAO systemLogDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        electionDAO = new ElectionDAO();
        // You might need to initialize these if they aren't already or are needed by VoteDAO
        // userDAO = new UserDAO();
        // candidateDAO = new CandidateDAO();
        voteDAO = new VoteDAO(); // Initialize VoteDAO
        systemLogDAO = new SystemLogDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        String ipAddress = request.getRemoteAddr();
        Integer adminUserId = (Integer) session.getAttribute("userId"); // Assuming admin is logged in

        // Basic security check: Ensure admin is logged in
        if (session == null || session.getAttribute("role") == null || !"ADMIN".equals(session.getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/index.jsp"); // Redirect to login if not admin
            systemLogDAO.addSystemLog(new SystemLog(adminUserId, "ACCESS_DENIED", "Unauthorized access attempt to election results from IP: " + ipAddress, ipAddress));
            return;
        }

        int electionId = 0;
        int constituencyId = 0;
        try {
            electionId = Integer.parseInt(request.getParameter("electionId"));
            constituencyId = Integer.parseInt(request.getParameter("constituencyId"));
        } catch (NumberFormatException e) {
            // Handle invalid electionId parameter
            System.err.println("Invalid electionId parameter: " + e.getMessage());
            systemLogDAO.addSystemLog(new SystemLog(adminUserId, "INVALID_INPUT", "Invalid electionId parameter for results from IP: " + ipAddress, ipAddress));
            response.sendRedirect(request.getContextPath() + "/admin"); // Redirect back to admin dashboard
            return;
        }

        try {
            // 1. Fetch the selected election details
            Election selectedElection = electionDAO.getElectionById(electionId);
            if (selectedElection == null) {
                // Election not found
                System.err.println("Election with ID " + electionId + " not found.");
                systemLogDAO.addSystemLog(new SystemLog(adminUserId, "DATA_NOT_FOUND", "Election with ID " + electionId + " not found for results from IP: " + ipAddress, ipAddress));
                response.sendRedirect(request.getContextPath() + "/admin"); // Redirect back to admin dashboard
                return;
            }

            // 2. Fetch Overall Election Results
            // This method should return a List of Object arrays or a custom Result POJO
            // Each Object[] might contain: [candidateId, voteCount, firstName, lastName, party]
            List<Object[]> overallResults = voteDAO.getOverallElectionResults(electionId);
            if (overallResults == null) {
                overallResults = new ArrayList<>(); // Ensure it's not null for JSP
            }

            // 3. Fetch Results by Constituency
            // This method should return a Map where key is Constituency Name (String)
            // and value is a List of Object arrays (similar to overallResults)
            List<Object[]> resultsByConstituency = voteDAO.getElectionResultsByConstituency(electionId,constituencyId);
            if (resultsByConstituency == null) {
                resultsByConstituency = new java.util.ArrayList<>(); // Ensure it's not null for JSP
            }

            // 4. Set data as request attributes for the JSP
            request.setAttribute("selectedElection", selectedElection);
            request.setAttribute("overallResults", overallResults);
            request.setAttribute("resultsByConstituency", resultsByConstituency);

            System.out.println("ElectionResultServlet: Displaying results for Election ID " + electionId);
            System.out.println("Overall Results Count: " + overallResults.size());
            System.out.println("Constituencies with Results Count: " + resultsByConstituency.size());

            // 5. Forward to the JSP
            request.getRequestDispatcher("/admin/electionResults.jsp").forward(request, response);

            systemLogDAO.addSystemLog(new SystemLog(adminUserId, "VIEW_ELECTION_RESULTS", "Admin viewed results for Election ID: " + electionId, ipAddress));

        } catch (Exception e) {
            System.err.println("Error loading election results: " + e.getMessage());
            e.printStackTrace();
            systemLogDAO.addSystemLog(new SystemLog(adminUserId, "ADMIN_ERROR", "Error loading election results for ID " + electionId + ": " + e.getMessage(), ipAddress));
            request.setAttribute("errorMessage", "An error occurred while loading election results.");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }
}
