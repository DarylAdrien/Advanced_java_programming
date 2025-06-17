// src/com/votingsystem/servlet/ElectionManagementServlet.java
package com.votingsystem.servlet;

import com.votingsystem.model.Constituency;
import com.votingsystem.model.Election;
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
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet("/admin/election")
public class ElectionManagementServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ElectionService electionService = new ElectionService();
    private AuthService authService = new AuthService(); // For logging

    private static final int ADMIN_ROLE_ID = 1;

    // Helper to check admin privileges
    private boolean isAdmin(HttpSession session, HttpServletResponse response, PrintWriter out) throws IOException {
        if (session == null || session.getAttribute("loggedInUser") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
            out.print(new JSONObject().put("success", false).put("message", "User not logged in.").toString());
            return false;
        }
        User currentUser = (User) session.getAttribute("loggedInUser");
//        if (currentUser.getRoleId() != ADMIN_ROLE_ID) {
//            response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403 Forbidden
//            out.print(new JSONObject().put("success", false).put("message", "Access denied. Admin privileges required.").toString());
//            // Log unauthorized access
//            authService.addLog(null, currentUser.getUserId(), "Unauthorized Access", "User " + currentUser.getUsername() + " attempted admin election action.");
//            return false;
//        }
        return true;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject jsonResponse = new JSONObject();

        HttpSession session = request.getSession(false);
//        if (!isAdmin(session, response, out)) {
//            return; // Response already sent by isAdmin
//        }
        User currentUser = (User) session.getAttribute("loggedInUser");
        int adminUserId = currentUser.getUserId();

        String action = request.getParameter("action"); // "listElections", "getElection", "listConstituencies", "getConstituency"

        try {
            if ("listElections".equalsIgnoreCase(action)) {
                String statusFilter = request.getParameter("status"); // Optional: "ACTIVE", "COMPLETED", "SCHEDULED"
                List<Election> elections;
                if (statusFilter != null && !statusFilter.trim().isEmpty()) {
                    elections = electionService.getElectionsByStatus(statusFilter);
                } else {
                    elections = electionService.getAllElections(); // Get all if no status filter
                }

                JSONArray electionArray = new JSONArray();
                for (Election election : elections) {
                    JSONObject electionJson = new JSONObject();
                    electionJson.put("electionId", election.getElectionId());
                    electionJson.put("electionName", election.getElectionName());
                    electionJson.put("description", election.getDescription());
                    electionJson.put("startDateTime", election.getStartDateTime().toString());
                    electionJson.put("endDateTime", election.getEndDateTime().toString());
                    electionJson.put("status", election.getStatus());
                    electionJson.put("createdBy", election.getCreatedBy());
                    electionJson.put("createdByName", election.getCreatedByName());
                    electionJson.put("createdDate", election.getCreatedDate().toString());
                    electionArray.put(electionJson);
                }
                jsonResponse.put("success", true);
                jsonResponse.put("elections", electionArray);
                authService.addLog(null, adminUserId, "List Elections", "Admin listed elections. Filter: " + (statusFilter == null ? "All" : statusFilter));

            } else if ("getElection".equalsIgnoreCase(action)) {
                String electionIdParam = request.getParameter("electionId");
                if (electionIdParam == null || electionIdParam.trim().isEmpty()) {
                    jsonResponse.put("success", false).put("message", "Election ID is required.");
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print(jsonResponse.toString());
                    return;
                }
                int electionId = Integer.parseInt(electionIdParam);
                Election election = electionService.getElectionById(electionId);
                if (election != null) {
                    JSONObject electionJson = new JSONObject();
                    electionJson.put("electionId", election.getElectionId());
                    electionJson.put("electionName", election.getElectionName());
                    electionJson.put("description", election.getDescription());
                    electionJson.put("startDateTime", election.getStartDateTime().toString());
                    electionJson.put("endDateTime", election.getEndDateTime().toString());
                    electionJson.put("status", election.getStatus());
                    electionJson.put("createdBy", election.getCreatedBy());
                    electionJson.put("createdByName", election.getCreatedByName());
                    electionJson.put("createdDate", election.getCreatedDate().toString());
                    jsonResponse.put("success", true);
                    jsonResponse.put("election", electionJson);
                    authService.addLog(null, adminUserId, "Get Election", "Admin viewed election ID: " + electionId);
                } else {
                    jsonResponse.put("success", false);
                    jsonResponse.put("message", "Election not found.");
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                }

            } else if ("listConstituencies".equalsIgnoreCase(action)) {
                List<Constituency> constituencies = electionService.getAllConstituencies();
                JSONArray constituencyArray = new JSONArray();
                for (Constituency c : constituencies) {
                    JSONObject cJson = new JSONObject();
                    cJson.put("constituencyId", c.getConstituencyId());
                    cJson.put("name", c.getName());
                    cJson.put("description", c.getDescription());
                    constituencyArray.put(cJson);
                }
                jsonResponse.put("success", true);
                jsonResponse.put("constituencies", constituencyArray);
                authService.addLog(null, adminUserId, "List Constituencies", "Admin listed constituencies.");

            } else if ("getConstituency".equalsIgnoreCase(action)) {
                 String constituencyIdParam = request.getParameter("constituencyId");
                if (constituencyIdParam == null || constituencyIdParam.trim().isEmpty()) {
                    jsonResponse.put("success", false).put("message", "Constituency ID is required.");
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print(jsonResponse.toString());
                    return;
                }
                int constituencyId = Integer.parseInt(constituencyIdParam);
                Constituency constituency = electionService.getConstituencyById(constituencyId);
                if (constituency != null) {
                    JSONObject cJson = new JSONObject();
                    cJson.put("constituencyId", constituency.getConstituencyId());
                    cJson.put("name", constituency.getName());
                    cJson.put("description", constituency.getDescription());
                    jsonResponse.put("success", true);
                    jsonResponse.put("constituency", cJson);
                    authService.addLog(null, adminUserId, "Get Constituency", "Admin viewed constituency ID: " + constituencyId);
                } else {
                    jsonResponse.put("success", false);
                    jsonResponse.put("message", "Constituency not found.");
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                }
            } else {
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Invalid action for GET request.");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (NumberFormatException e) {
            jsonResponse.put("success", false).put("message", "Invalid ID format: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            authService.addLog(null, adminUserId, "API Error", "Invalid ID format in ElectionManagementServlet GET: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error in ElectionManagementServlet (GET): " + e.getMessage());
            e.printStackTrace();
            jsonResponse.put("success", false).put("message", "An unexpected error occurred: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            authService.addLog(null, adminUserId, "Server Error", "Error in ElectionManagementServlet GET: " + e.getMessage());
        }
        out.print(jsonResponse.toString());
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject jsonResponse = new JSONObject();

        HttpSession session = request.getSession(false);
//        if (!isAdmin(session, response, out)) {
//            return; // Response already sent by isAdmin
//        }
        User currentUser = (User) session.getAttribute("loggedInUser");
        int adminUserId = currentUser.getUserId();

        String action = request.getParameter("action"); // "createElection", "updateElection", "createConstituency", "updateConstituency"

        try {
            if ("createElection".equalsIgnoreCase(action)) {
                String electionName = request.getParameter("electionName");
                String description = request.getParameter("description");
                String startDateTimeStr = request.getParameter("startDateTime"); // YYYY-MM-DDTHH:MM:SS format
                String endDateTimeStr = request.getParameter("endDateTime");     // YYYY-MM-DDTHH:MM:SS format

                if (electionName == null || electionName.trim().isEmpty() ||
                    startDateTimeStr == null || startDateTimeStr.trim().isEmpty() ||
                    endDateTimeStr == null || endDateTimeStr.trim().isEmpty()) {
                    jsonResponse.put("success", false).put("message", "Election name, start time, and end time are required.");
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print(jsonResponse.toString());
                    return;
                }

                Timestamp startDateTime = Timestamp.valueOf(LocalDateTime.parse(startDateTimeStr));
                Timestamp endDateTime = Timestamp.valueOf(LocalDateTime.parse(endDateTimeStr));

                if (endDateTime.before(startDateTime)) {
                    jsonResponse.put("success", false).put("message", "End date/time must be after start date/time.");
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print(jsonResponse.toString());
                    return;
                }

                Election newElection = new Election();
                newElection.setElectionName(electionName);
                newElection.setDescription(description);
                newElection.setStartDateTime(startDateTime);
                newElection.setEndDateTime(endDateTime);
                newElection.setStatus("SCHEDULED"); // Default status upon creation

                Election createdElection = electionService.createElection(newElection, adminUserId);
//                System.out.println(createdElection);
                if (createdElection != null) {
                    jsonResponse.put("success", true);
                    jsonResponse.put("message", "Election '" + createdElection.getElectionName() + "' created successfully.");
                    jsonResponse.put("electionId", createdElection.getElectionId());
                } else {
                    jsonResponse.put("success", false);
                    jsonResponse.put("message", "Failed to create election. It might already exist or a database error occurred.");
                }

            } else if ("updateElection".equalsIgnoreCase(action)) {
                String electionIdParam = request.getParameter("electionId");
                String electionName = request.getParameter("electionName");
                String description = request.getParameter("description");
//                String startDateTimeStr = request.getParameter("startDateTime");
//                String endDateTimeStr = request.getParameter("endDateTime");
                String status = request.getParameter("status"); // Allow admin to change status

                if (electionIdParam == null || electionIdParam.trim().isEmpty() ||
                    electionName == null || electionName.trim().isEmpty()  ||
                    status == null || status.trim().isEmpty()) {
                    jsonResponse.put("success", false).put("message", "All election fields (ID, name, status) are required for update.");
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print(jsonResponse.toString());
                    return;
                }

                int electionId = Integer.parseInt(electionIdParam);
//                Timestamp startDateTime = Timestamp.valueOf(LocalDateTime.parse(startDateTimeStr));
//                Timestamp endDateTime = Timestamp.valueOf(LocalDateTime.parse(endDateTimeStr));

//                if (endDateTime.before(startDateTime)) {
//                    jsonResponse.put("success", false).put("message", "End date/time must be after start date/time.");
//                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//                    out.print(jsonResponse.toString());
//                    return;
//                }

                Election existingElection = electionService.getElectionById(electionId);
                if(existingElection == null) {
                     jsonResponse.put("success", false).put("message", "Election with ID " + electionId + " not found for update.");
                     response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                     out.print(jsonResponse.toString());
                     return;
                }

                existingElection.setElectionName(electionName);
                existingElection.setDescription(description);
//                existingElection.setStartDateTime(startDateTime);
//                existingElection.setEndDateTime(endDateTime);
                existingElection.setStatus(status); // Admin can change status

                boolean updated = electionService.updateElection(existingElection, adminUserId);
                if (updated) {
                    jsonResponse.put("success", true);
                    jsonResponse.put("message", "Election '" + electionName + "' updated successfully.");
                } else {
                    jsonResponse.put("success", false);
                    jsonResponse.put("message", "Failed to update election.");
                }

            } else if ("createConstituency".equalsIgnoreCase(action)) {
                String name = request.getParameter("name");
                String description = request.getParameter("description");

                if (name == null || name.trim().isEmpty()) {
                    jsonResponse.put("success", false).put("message", "Constituency name is required.");
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print(jsonResponse.toString());
                    return;
                }

                Constituency newConstituency = new Constituency();
                newConstituency.setName(name);
                newConstituency.setDescription(description);

                Constituency createdConstituency = electionService.createConstituency(newConstituency, adminUserId);
                System.out.println(createdConstituency);
                if (createdConstituency != null) {
                    jsonResponse.put("success", true);
                    jsonResponse.put("message", "Constituency '" + createdConstituency.getName() + "' created successfully.");
                    jsonResponse.put("constituencyId", createdConstituency.getConstituencyId());
                } else {
                    jsonResponse.put("success", false);
                    jsonResponse.put("message", "Failed to create constituency. It might already exist or a database error occurred.");
                }

            } else if ("updateConstituency".equalsIgnoreCase(action)) {
                String constituencyIdParam = request.getParameter("constituencyId");
                String name = request.getParameter("name");
                String description = request.getParameter("description");

                if (constituencyIdParam == null || constituencyIdParam.trim().isEmpty() ||
                    name == null || name.trim().isEmpty()) {
                    jsonResponse.put("success", false).put("message", "Constituency ID and name are required for update.");
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print(jsonResponse.toString());
                    return;
                }

                int constituencyId = Integer.parseInt(constituencyIdParam);
                Constituency existingConstituency = electionService.getConstituencyById(constituencyId);
                if(existingConstituency == null) {
                    jsonResponse.put("success", false).put("message", "Constituency with ID " + constituencyId + " not found for update.");
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print(jsonResponse.toString());
                    return;
                }

                existingConstituency.setName(name);
                existingConstituency.setDescription(description);

                // No direct update method for constituency in ElectionService, using a placeholder for now
                // You would need to add an updateConstituency method to ElectionService
                // For demonstration, let's assume updateConstituency exists and is similar to updateElection
                // boolean updated = electionService.updateConstituency(existingConstituency, adminUserId);
                jsonResponse.put("success", false); // Placeholder
                jsonResponse.put("message", "Update constituency functionality not fully implemented yet."); // Placeholder

            } else {
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Invalid action for POST request.");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (NumberFormatException e) {
            jsonResponse.put("success", false).put("message", "Invalid ID format: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            authService.addLog(null, adminUserId, "API Error", "Invalid ID format in ElectionManagementServlet POST: " + e.getMessage());
        } catch (DateTimeParseException e) {
            jsonResponse.put("success", false).put("message", "Invalid date/time format. Use YYYY-MM-DDTHH:MM:SS (e.g., 2025-12-31T23:59:59).");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            authService.addLog(null, adminUserId, "API Error", "Invalid date/time format in ElectionManagementServlet POST: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error in ElectionManagementServlet (POST): " + e.getMessage());
            e.printStackTrace();
            jsonResponse.put("success", false).put("message", "An unexpected error occurred: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            authService.addLog(null, adminUserId, "Server Error", "Error in ElectionManagementServlet POST: " + e.getMessage());
        }
        out.print(jsonResponse.toString());
    }
}