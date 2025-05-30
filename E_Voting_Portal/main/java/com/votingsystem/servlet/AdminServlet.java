// src/main/java/com/votingsystem/servlet/AdminServlet.java
package com.votingsystem.servlet;

import com.votingsystem.dao.*;

import com.votingsystem.model.*;
import com.votingsystem.util.PasswordUtil; // For hashing passwords if admin changes them

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Servlet for handling administrative actions in the voting system.
 * This includes managing users, constituencies, elections, candidates, and viewing system logs.
 */
@WebServlet("/admin")
public class AdminServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserDAO userDAO;
    private ConstituencyDAO constituencyDAO;
    private ElectionDAO electionDAO;
    private CandidateDAO candidateDAO;
    private SystemLogDAO systemLogDAO;
    private VoteDAO voteDAO; // For election results

    /**
     * Initializes the servlet and its DAOs.
     */
    @Override
    public void init() throws ServletException {
        super.init();
        userDAO = new UserDAO();
        constituencyDAO = new ConstituencyDAO();
        electionDAO = new ElectionDAO();
        candidateDAO = new CandidateDAO();
        systemLogDAO = new SystemLogDAO();
        voteDAO = new VoteDAO();
    }

    /**
     * Helper method to check if the user is an admin.
     *
     * @param session The current HttpSession.
     * @return true if the user is logged in and has the 'ADMIN' role, false otherwise.
     */
    private boolean isAdmin(HttpSession session) {
        return session != null && "ADMIN".equals(session.getAttribute("role"));
    }

    /**
     * Handles GET requests to display the admin dashboard and fetch initial data.
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
        Integer adminUserId = (Integer) session.getAttribute("userId");

        if (!isAdmin(session)) {
            response.sendRedirect("index.jsp"); // Redirect to login if not admin
            systemLogDAO.addSystemLog(new SystemLog(adminUserId, "ACCESS_DENIED", "Unauthorized access attempt to admin dashboard from IP: " + ipAddress, ipAddress));
            return;
        }

        try {
            // Fetch data for various sections of the admin dashboard
            List<User> pendingUsers = userDAO.getUnregisteredUsers(); // Or filter by isRegistered = 0
            List<User> allUsers = userDAO.getAllUsers();
            List<Constituency> constituencies = constituencyDAO.getAllConstituencies();
            List<Election> elections = electionDAO.getAllElections();
            List<Candidate> candidates = candidateDAO.getAllCandidatesWithUserDetails(); // Includes pending and approved
            List<SystemLog> systemLogs = systemLogDAO.getAllSystemLogs();

            // Filter pending users (those with isRegistered = 0)
//            List<User> usersPendingApproval = new java.util.ArrayList<>();
//            for (User user : pendingUsers) {
//                
//                    usersPendingApproval.add(user);
//                
//            }

            // Filter pending candidates (those with approvalStatus = 'PENDING')
            List<Candidate> candidatesPendingApproval = new java.util.ArrayList<>();
            for (Candidate candidate : candidates) {
                if ("PENDING".equals(candidate.getApprovalStatus())) {
                    candidatesPendingApproval.add(candidate);
                }
            }

            request.setAttribute("pendingUsers", pendingUsers);
//            request.setAttribute("usersPendingApproval", usersPendingApproval);
            request.setAttribute("allUsers", allUsers);
            request.setAttribute("constituencies", constituencies);
            request.setAttribute("elections", elections);
            request.setAttribute("candidates", candidates); // All candidates
            request.setAttribute("candidatesPendingApproval", candidatesPendingApproval);
            request.setAttribute("systemLogs", systemLogs);

            System.out.println("Pending users: " + pendingUsers.size());
            System.out.println("All users: " + allUsers.size());
            // Forward to the admin dashboard JSP
            for (User u : pendingUsers) {
                System.out.println("Pending User: " + u.getUsername() + " (Registered: " + u.getRegistered() + ")");
            }
            request.getRequestDispatcher("admin/dashboard.jsp").forward(request, response);
        } catch (Exception e) {
            System.err.println("Error loading admin dashboard: " + e.getMessage());
            e.printStackTrace();
            systemLogDAO.addSystemLog(new SystemLog(adminUserId, "ADMIN_ERROR", "Error loading admin dashboard: " + e.getMessage(), ipAddress));
            request.setAttribute("errorMessage", "An error occurred while loading the admin dashboard.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }

    /**
     * Handles POST requests for various administrative actions.
     * Uses a 'action' parameter to determine the specific operation.
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
        Integer adminUserId = (Integer) session.getAttribute("userId");

        if (!isAdmin(session)) {
            response.sendRedirect("index.jsp");
            systemLogDAO.addSystemLog(new SystemLog(adminUserId, "ACCESS_DENIED", "Unauthorized POST attempt to admin servlet from IP: " + ipAddress, ipAddress));
            return;
        }

        String action = request.getParameter("action");
        String message = null;
        boolean success = false;

        if (action == null || action.trim().isEmpty()) {
            message = "No action specified.";
            systemLogDAO.addSystemLog(new SystemLog(adminUserId, "ADMIN_ERROR", "No action specified in POST request.", ipAddress));
            request.setAttribute("errorMessage", message);
            doGet(request, response);
            return;
        }

        try {
            switch (action) {
                case "approveUser":
                    int approveUserId = Integer.parseInt(request.getParameter("userId"));
                    User userToApprove = userDAO.getUserById(approveUserId);
                    if (userToApprove != null) {
                        userToApprove.setRegistered(1); // Set to approved
                        success = userDAO.updateUser(userToApprove);
                        if (success) {
                            message = "User " + userToApprove.getUsername() + " approved successfully.";
                            systemLogDAO.addSystemLog(new SystemLog(adminUserId, "USER_APPROVED", "Admin approved user: " + userToApprove.getUsername(), ipAddress));
                        } else {
                            message = "Failed to approve user " + userToApprove.getUsername() + ".";
                            systemLogDAO.addSystemLog(new SystemLog(adminUserId, "USER_APPROVAL_FAILED", "Failed to approve user: " + userToApprove.getUsername(), ipAddress));
                        }
                    } else {
                        message = "User not found for approval.";
                        systemLogDAO.addSystemLog(new SystemLog(adminUserId, "USER_APPROVAL_FAILED", "User not found for approval (ID: " + approveUserId + ").", ipAddress));
                    }
                    break;

                case "rejectUser":
                    int rejectUserId = Integer.parseInt(request.getParameter("userId"));
                    User userToReject = userDAO.getUserById(rejectUserId);
                    if (userToReject != null) {
                        success = userDAO.deleteUser(rejectUserId); // Or set a 'rejected' status
                        if (success) {
                            message = "User " + userToReject.getUsername() + " rejected and deleted successfully.";
                            systemLogDAO.addSystemLog(new SystemLog(adminUserId, "USER_REJECTED", "Admin rejected and deleted user: " + userToReject.getUsername(), ipAddress));
                        } else {
                            message = "Failed to reject and delete user " + userToReject.getUsername() + ".";
                            systemLogDAO.addSystemLog(new SystemLog(adminUserId, "USER_REJECTION_FAILED", "Failed to reject user: " + userToReject.getUsername(), ipAddress));
                        }
                    } else {
                        message = "User not found for rejection.";
                        systemLogDAO.addSystemLog(new SystemLog(adminUserId, "USER_REJECTION_FAILED", "User not found for rejection (ID: " + rejectUserId + ").", ipAddress));
                    }
                    break;

                case "toggleOtp":
                	int toggleOtpUserId = Integer.parseInt(request.getParameter("userId"));
                	User userToToggleOtp = userDAO.getUserById(toggleOtpUserId);

                	if (userToToggleOtp != null) {
                	    int currentOtpStatus = userToToggleOtp.getOtpEnabled(); // 0 or 1
                	    int newOtpStatus = (currentOtpStatus == 1) ? 0 : 1; // toggle

                	    userToToggleOtp.setOtpEnabled(newOtpStatus); // set new status
                	    boolean success1 = userDAO.updateUser(userToToggleOtp);

                	    if (success1) {
                	        message = "OTP status for user " + userToToggleOtp.getUsername() + " updated to " + newOtpStatus + ".";
                	        systemLogDAO.addSystemLog(new SystemLog(
                	            adminUserId,
                	            "OTP_TOGGLED",
                	            "Admin toggled OTP for user: " + userToToggleOtp.getUsername() + " to " + newOtpStatus,
                	            ipAddress
                	        ));
                	    } else {
                	        message = "Failed to update OTP status for user " + userToToggleOtp.getUsername() + ".";
                	        systemLogDAO.addSystemLog(new SystemLog(
                	            adminUserId,
                	            "OTP_TOGGLE_FAILED",
                	            "Failed to toggle OTP for user: " + userToToggleOtp.getUsername(),
                	            ipAddress
                	        ));
                	    }
                	} else {
                	    message = "User not found for OTP toggle.";
                	    systemLogDAO.addSystemLog(new SystemLog(
                	        adminUserId,
                	        "OTP_TOGGLE_FAILED",
                	        "User not found for OTP toggle (ID: " + toggleOtpUserId + ").",
                	        ipAddress
                	    ));
                	}
                    break;

//                case "updateUser":
//                    int updateUserId = Integer.parseInt(request.getParameter("userId"));
//                    User userToUpdate = userDAO.getUserById(updateUserId);
//                    if (userToUpdate != null) {
//                        // Update fields that an admin might change
//                        userToUpdate.setFirstName(request.getParameter("firstName"));
//                        userToUpdate.setLastName(request.getParameter("lastName"));
//                        userToUpdate.setEmail(request.getParameter("email"));
//                        userToUpdate.setPhoneNumber(request.getParameter("phoneNumber"));
//                        userToUpdate.setAadhaarNumber(request.getParameter("aadhaarNumber"));
//                        userToUpdate.setRole(request.getParameter("role")); // Admin can change roles
//                        userToUpdate.setRegistered(Integer.parseInt(request.getParameter("isRegistered")));
//                        userToUpdate.setOtpEnabled(Integer.parseInt(request.getParameter("isOtpEnabled")));
//
//                        // Handle constituency ID, can be null for admin
//                        String constIdParam = request.getParameter("constituencyId");
//                        if (constIdParam != null && !constIdParam.isEmpty()) {
//                            userToUpdate.setConstituencyId(Integer.parseInt(constIdParam));
//                        } else {
//                            userToUpdate.setConstituencyId(0); // Or set to a default/null equivalent
//                        }
//
//                        // Handle password change if provided
//                        String newPassword = request.getParameter("newPassword");
//                        if (newPassword != null && !newPassword.trim().isEmpty()) {
//                            userToUpdate.setPasswordHash(PasswordUtil.hashPassword(newPassword));
//                        }
//
//                        success = userDAO.updateUser(userToUpdate);
//                        if (success) {
//                            message = "User " + userToUpdate.getUsername() + " updated successfully.";
//                            systemLogDAO.addSystemLog(new SystemLog(adminUserId, "USER_UPDATED", "Admin updated user: " + userToUpdate.getUsername(), ipAddress));
//                        } else {
//                            message = "Failed to update user " + userToUpdate.getUsername() + ".";
//                            systemLogDAO.addSystemLog(new SystemLog(adminUserId, "USER_UPDATE_FAILED", "Failed to update user: " + userToUpdate.getUsername(), ipAddress));
//                        }
//                    } else {
//                        message = "User not found for update.";
//                        systemLogDAO.addSystemLog(new SystemLog(adminUserId, "USER_UPDATE_FAILED", "User not found for update (ID: " + updateUserId + ").", ipAddress));
//                    }
//                    break;
                    
                case "updateUser":
                    int updateUserId = Integer.parseInt(request.getParameter("userId"));
                    User userToUpdate = userDAO.getUserById(updateUserId);
                    if (userToUpdate != null) {
                        int oldConstituencyId = userToUpdate.getConstituencyId(); // Get old constituency ID

                        // Update fields that an admin might change
                        userToUpdate.setFirstName(request.getParameter("firstName"));
                        userToUpdate.setLastName(request.getParameter("lastName"));
                        userToUpdate.setEmail(request.getParameter("email"));
                        userToUpdate.setPhoneNumber(request.getParameter("phoneNumber"));
                        userToUpdate.setAadhaarNumber(request.getParameter("aadhaarNumber"));
                        userToUpdate.setRole(request.getParameter("role")); // Admin can change roles
                        userToUpdate.setRegistered(Integer.parseInt(request.getParameter("isRegistered")));
                        userToUpdate.setOtpEnabled(Integer.parseInt(request.getParameter("isOtpEnabled")));

                        // Handle new constituency ID
                        int newConstituencyId = 0; // Default or null equivalent
                        String constIdParam = request.getParameter("constituencyId");
                        if (constIdParam != null && !constIdParam.isEmpty()) {
                            newConstituencyId = Integer.parseInt(constIdParam);
                        }
                        userToUpdate.setConstituencyId(newConstituencyId);

                        // Handle password change if provided
                        String newPassword = request.getParameter("newPassword");
                        if (newPassword != null && !newPassword.trim().isEmpty()) {
                            userToUpdate.setPasswordHash(PasswordUtil.hashPassword(newPassword));
                        }

                        success = userDAO.updateUser(userToUpdate);
                        if (success) {
                            message = "User " + userToUpdate.getUsername() + " updated successfully.";
                            systemLogDAO.addSystemLog(new SystemLog(adminUserId, "USER_UPDATED", "Admin updated user: " + userToUpdate.getUsername(), ipAddress));

                            // If the user's role is CANDIDATE and constituency changed, update candidate profiles
                            if ("CANDIDATE".equals(userToUpdate.getRole()) && oldConstituencyId != newConstituencyId) {
                                boolean candidateUpdateSuccess = candidateDAO.updateCandidatesConstituencyByUserId(updateUserId, newConstituencyId);
                                if (candidateUpdateSuccess) {
                                    systemLogDAO.addSystemLog(new SystemLog(adminUserId, "CANDIDATE_CONSTITUENCY_UPDATED", "Candidate " + userToUpdate.getUsername() + "'s constituency updated from " + oldConstituencyId + " to " + newConstituencyId, ipAddress));
                                    message += " Candidate profiles updated.";
                                } else {
                                    systemLogDAO.addSystemLog(new SystemLog(adminUserId, "CANDIDATE_CONSTITUENCY_UPDATE_FAILED", "Failed to update candidate " + userToUpdate.getUsername() + "'s constituency after user update.", ipAddress));
                                    message += " Failed to update associated candidate profiles.";
                                }
                            }
                        } else {
                            message = "Failed to update user " + userToUpdate.getUsername() + ".";
                            systemLogDAO.addSystemLog(new SystemLog(adminUserId, "USER_UPDATE_FAILED", "Failed to update user: " + userToUpdate.getUsername(), ipAddress));
                        }
                    } else {
                        message = "User not found for update.";
                        systemLogDAO.addSystemLog(new SystemLog(adminUserId, "USER_UPDATE_FAILED", "User not found for update (ID: " + updateUserId + ").", ipAddress));
                    }
                    break;

                case "deleteUser":
                    int deleteUserId = Integer.parseInt(request.getParameter("userId"));
                    User userToDelete = userDAO.getUserById(deleteUserId);
                    if (userToDelete != null) {
                        success = userDAO.deleteUser(deleteUserId);
                        if (success) {
                            message = "User " + userToDelete.getUsername() + " deleted successfully.";
                            systemLogDAO.addSystemLog(new SystemLog(adminUserId, "USER_DELETED", "Admin deleted user: " + userToDelete.getUsername(), ipAddress));
                        } else {
                            message = "Failed to delete user " + userToDelete.getUsername() + ".";
                            systemLogDAO.addSystemLog(new SystemLog(adminUserId, "USER_DELETION_FAILED", "Failed to delete user: " + userToDelete.getUsername(), ipAddress));
                        }
                    } else {
                        message = "User not found for deletion.";
                        systemLogDAO.addSystemLog(new SystemLog(adminUserId, "USER_DELETION_FAILED", "User not found for deletion (ID: " + deleteUserId + ").", ipAddress));
                    }
                    break;

                // Constituency Management
                case "addConstituency":
                    String constName = request.getParameter("name");
                    String constDesc = request.getParameter("description");
                    if (constName != null && !constName.trim().isEmpty()) {
                        Constituency newConst = new Constituency(constName, constDesc);
                        int newConstId = constituencyDAO.addConstituency(newConst);
                        if (newConstId != -1) {
                            message = "Constituency '" + constName + "' added successfully.";
                            systemLogDAO.addSystemLog(new SystemLog(adminUserId, "CONSTITUENCY_ADDED", "Admin added constituency: " + constName, ipAddress));
                        } else {
                            message = "Failed to add constituency '" + constName + "'. It might already exist.";
                            systemLogDAO.addSystemLog(new SystemLog(adminUserId, "CONSTITUENCY_ADD_FAILED", "Failed to add constituency: " + constName, ipAddress));
                        }
                    } else {
                        message = "Constituency name cannot be empty.";
                        systemLogDAO.addSystemLog(new SystemLog(adminUserId, "CONSTITUENCY_ADD_FAILED", "Attempted to add constituency with empty name.", ipAddress));
                    }
                    break;

                case "updateConstituency":
                    int updateConstId = Integer.parseInt(request.getParameter("constituencyId"));
                    String updatedConstName = request.getParameter("name");
                    String updatedConstDesc = request.getParameter("description");
                    Constituency constToUpdate = constituencyDAO.getConstituencyById(updateConstId);
                    if (constToUpdate != null) {
                        constToUpdate.setName(updatedConstName);
                        constToUpdate.setDescription(updatedConstDesc);
                        success = constituencyDAO.updateConstituency(constToUpdate);
                        if (success) {
                            message = "Constituency '" + updatedConstName + "' updated successfully.";
                            systemLogDAO.addSystemLog(new SystemLog(adminUserId, "CONSTITUENCY_UPDATED", "Admin updated constituency: " + updatedConstName, ipAddress));
                        } else {
                            message = "Failed to update constituency '" + updatedConstName + "'.";
                            systemLogDAO.addSystemLog(new SystemLog(adminUserId, "CONSTITUENCY_UPDATE_FAILED", "Failed to update constituency: " + updatedConstName, ipAddress));
                        }
                    } else {
                        message = "Constituency not found for update.";
                        systemLogDAO.addSystemLog(new SystemLog(adminUserId, "CONSTITUENCY_UPDATE_FAILED", "Constituency not found for update (ID: " + updateConstId + ").", ipAddress));
                    }
                    break;

                case "deleteConstituency":
                    int deleteConstId = Integer.parseInt(request.getParameter("constituencyId"));
                    Constituency constToDelete = constituencyDAO.getConstituencyById(deleteConstId);
                    if (constToDelete != null) {
                        success = constituencyDAO.deleteConstituency(deleteConstId);
                        if (success) {
                            message = "Constituency '" + constToDelete.getName() + "' deleted successfully.";
                            systemLogDAO.addSystemLog(new SystemLog(adminUserId, "CONSTITUENCY_DELETED", "Admin deleted constituency: " + constToDelete.getName(), ipAddress));
                        } else {
                            message = "Failed to delete constituency '" + constToDelete.getName() + "'. Ensure no users or candidates are linked.";
                            systemLogDAO.addSystemLog(new SystemLog(adminUserId, "CONSTITUENCY_DELETION_FAILED", "Failed to delete constituency: " + constToDelete.getName(), ipAddress));
                        }
                    } else {
                        message = "Constituency not found for deletion.";
                        systemLogDAO.addSystemLog(new SystemLog(adminUserId, "CONSTITUENCY_DELETION_FAILED", "Constituency not found for deletion (ID: " + deleteConstId + ").", ipAddress));
                    }
                    break;

                // Election Management
                case "addElection":
                    String electionName = request.getParameter("name");
                    String electionDesc = request.getParameter("description");
                    String startDateStr = request.getParameter("startDate");
                    String endDateStr = request.getParameter("endDate");
                    String electionStatus = request.getParameter("status");

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
                    Timestamp startDate = null;
                    Timestamp endDate = null;
                    try {
                        startDate = new Timestamp(dateFormat.parse(startDateStr).getTime());
                        endDate = new Timestamp(dateFormat.parse(endDateStr).getTime());
                    } catch (ParseException e) {
                        message = "Invalid date/time format for election dates.";
                        systemLogDAO.addSystemLog(new SystemLog(adminUserId, "ELECTION_ADD_FAILED", "Invalid date format for new election: " + e.getMessage(), ipAddress));
                        request.setAttribute("errorMessage", message);
                        doGet(request, response);
                        return;
                    }

                    if (electionName != null && !electionName.trim().isEmpty() && startDate != null && endDate != null) {
                        Election newElection = new Election(electionName, electionDesc, startDate, endDate, electionStatus);
                        int newElectionId = electionDAO.addElection(newElection);
                        if (newElectionId != -1) {
                            message = "Election '" + electionName + "' added successfully.";
                            systemLogDAO.addSystemLog(new SystemLog(adminUserId, "ELECTION_ADDED", "Admin added election: " + electionName, ipAddress));
                        } else {
                            message = "Failed to add election '" + electionName + "'. It might already exist or dates are invalid.";
                            systemLogDAO.addSystemLog(new SystemLog(adminUserId, "ELECTION_ADD_FAILED", "Failed to add election: " + electionName, ipAddress));
                        }
                    } else {
                        message = "Election name and dates cannot be empty.";
                        systemLogDAO.addSystemLog(new SystemLog(adminUserId, "ELECTION_ADD_FAILED", "Attempted to add election with empty fields.", ipAddress));
                    }
                    break;

                case "updateElection":
                    int updateElectionId = Integer.parseInt(request.getParameter("electionId"));
                    String updatedElectionName = request.getParameter("name");
                    String updatedElectionDesc = request.getParameter("description");
                    String updatedStartDateStr = request.getParameter("startDate");
                    String updatedEndDateStr = request.getParameter("endDate");
                    String updatedElectionStatus = request.getParameter("status");

                    Timestamp updatedStartDate = null;
                    Timestamp updatedEndDate = null;
                    try {
                        SimpleDateFormat updateDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
                        updatedStartDate = new Timestamp(updateDateFormat.parse(updatedStartDateStr).getTime());
                        updatedEndDate = new Timestamp(updateDateFormat.parse(updatedEndDateStr).getTime());
                    } catch (ParseException e) {
                        message = "Invalid date/time format for election dates.";
                        systemLogDAO.addSystemLog(new SystemLog(adminUserId, "ELECTION_UPDATE_FAILED", "Invalid date format for election update: " + e.getMessage(), ipAddress));
                        request.setAttribute("errorMessage", message);
                        doGet(request, response);
                        return;
                    }

                    Election electionToUpdate = electionDAO.getElectionById(updateElectionId);
                    if (electionToUpdate != null) {
                        electionToUpdate.setName(updatedElectionName);
                        electionToUpdate.setDescription(updatedElectionDesc);
                        electionToUpdate.setStartDate(updatedStartDate);
                        electionToUpdate.setEndDate(updatedEndDate);
                        electionToUpdate.setStatus(updatedElectionStatus);
                        success = electionDAO.updateElection(electionToUpdate);
                        if (success) {
                            message = "Election '" + updatedElectionName + "' updated successfully.";
                            systemLogDAO.addSystemLog(new SystemLog(adminUserId, "ELECTION_UPDATED", "Admin updated election: " + updatedElectionName, ipAddress));
                        } else {
                            message = "Failed to update election '" + updatedElectionName + "'.";
                            systemLogDAO.addSystemLog(new SystemLog(adminUserId, "ELECTION_UPDATE_FAILED", "Failed to update election: " + updatedElectionName, ipAddress));
                        }
                    } else {
                        message = "Election not found for update.";
                        systemLogDAO.addSystemLog(new SystemLog(adminUserId, "ELECTION_UPDATE_FAILED", "Election not found for update (ID: " + updateElectionId + ").", ipAddress));
                    }
                    break;

                case "deleteElection":
                    int deleteElectionId = Integer.parseInt(request.getParameter("electionId"));
                    Election electionToDelete = electionDAO.getElectionById(deleteElectionId);
                    if (electionToDelete != null) {
                        success = electionDAO.deleteElection(deleteElectionId);
                        if (success) {
                            message = "Election '" + electionToDelete.getName() + "' deleted successfully.";
                            systemLogDAO.addSystemLog(new SystemLog(adminUserId, "ELECTION_DELETED", "Admin deleted election: " + electionToDelete.getName(), ipAddress));
                        } else {
                            message = "Failed to delete election '" + electionToDelete.getName() + "'. Ensure no candidates or votes are linked.";
                            systemLogDAO.addSystemLog(new SystemLog(adminUserId, "ELECTION_DELETION_FAILED", "Failed to delete election: " + electionToDelete.getName(), ipAddress));
                        }
                    } else {
                        message = "Election not found for deletion.";
                        systemLogDAO.addSystemLog(new SystemLog(adminUserId, "ELECTION_DELETION_FAILED", "Election not found for deletion (ID: " + deleteElectionId + ").", ipAddress));
                    }
                    break;

                // Candidate Approval/Rejection
                case "approveCandidate":
                    int approveCandidateId = Integer.parseInt(request.getParameter("candidateId"));
                    Candidate candidateToApprove = candidateDAO.getCandidateById(approveCandidateId);
                    if (candidateToApprove != null) {
                        success = candidateDAO.updateCandidateApprovalStatus(approveCandidateId, "APPROVED");
                        if (success) {
                            message = "Candidate ID " + approveCandidateId + " approved successfully.";
                            systemLogDAO.addSystemLog(new SystemLog(adminUserId, "CANDIDATE_APPROVED", "Admin approved candidate ID: " + approveCandidateId, ipAddress));
                        } else {
                            message = "Failed to approve candidate ID " + approveCandidateId + ".";
                            systemLogDAO.addSystemLog(new SystemLog(adminUserId, "CANDIDATE_APPROVAL_FAILED", "Failed to approve candidate ID: " + approveCandidateId, ipAddress));
                        }
                    } else {
                        message = "Candidate not found for approval.";
                        systemLogDAO.addSystemLog(new SystemLog(adminUserId, "CANDIDATE_APPROVAL_FAILED", "Candidate not found for approval (ID: " + approveCandidateId + ").", ipAddress));
                    }
                    break;

                case "rejectCandidate":
                    int rejectCandidateId = Integer.parseInt(request.getParameter("candidateId"));
                    Candidate candidateToReject = candidateDAO.getCandidateById(rejectCandidateId);
                    if (candidateToReject != null) {
                        success = candidateDAO.updateCandidateApprovalStatus(rejectCandidateId, "REJECTED");
                        if (success) {
                            message = "Candidate ID " + rejectCandidateId + " rejected successfully.";
                            systemLogDAO.addSystemLog(new SystemLog(adminUserId, "CANDIDATE_REJECTED", "Admin rejected candidate ID: " + rejectCandidateId, ipAddress));
                        } else {
                            message = "Failed to reject candidate ID " + rejectCandidateId + ".";
                            systemLogDAO.addSystemLog(new SystemLog(adminUserId, "CANDIDATE_REJECTION_FAILED", "Failed to reject candidate ID: " + rejectCandidateId, ipAddress));
                        }
                    } else {
                        message = "Candidate not found for rejection.";
                        systemLogDAO.addSystemLog(new SystemLog(adminUserId, "CANDIDATE_REJECTION_FAILED", "Candidate not found for rejection (ID: " + rejectCandidateId + ").", ipAddress));
                    }
                    break;

                case "viewElectionResults":
                    int electionId = Integer.parseInt(request.getParameter("electionId"));
                    Election election = electionDAO.getElectionById(electionId);
                    if (election != null) {
                        List<Constituency> allConstituencies = constituencyDAO.getAllConstituencies();
                        request.setAttribute("selectedElection", election);
                        request.setAttribute("allConstituencies", allConstituencies);

                        // Fetch results per constituency
                        java.util.Map<String, List<Object[]>> resultsByConstituency = new java.util.HashMap<>();
                        for (Constituency c : allConstituencies) {
                            List<Object[]> constituencyResults = voteDAO.getElectionResultsByConstituency(electionId, c.getConstituencyId());
                            resultsByConstituency.put(c.getName(), constituencyResults);
                        }
                        System.out.println(resultsByConstituency);
                        request.setAttribute("resultsByConstituency", resultsByConstituency);

                        // Fetch overall results
                        List<Object[]> overallResults = voteDAO.getOverallElectionResults(electionId);
                        request.setAttribute("overallResults", overallResults);

                        request.getRequestDispatcher("admin/electionResults.jsp").forward(request, response);
                        systemLogDAO.addSystemLog(new SystemLog(adminUserId, "VIEW_RESULTS", "Admin viewed results for election ID: " + electionId, ipAddress));
                        return; // Important: return after forwarding to a new JSP
                    } else {
                        message = "Election not found for viewing results.";
                        systemLogDAO.addSystemLog(new SystemLog(adminUserId, "VIEW_RESULTS_FAILED", "Election not found for viewing results (ID: " + electionId + ").", ipAddress));
                    }
                    break;

                default:
                    message = "Unknown action: " + action;
                    systemLogDAO.addSystemLog(new SystemLog(adminUserId, "ADMIN_ERROR", "Unknown action received: " + action, ipAddress));
                    break;
            }
        } catch (NumberFormatException e) {
            message = "Invalid ID format provided.";
            System.err.println("NumberFormatException in AdminServlet: " + e.getMessage());
            systemLogDAO.addSystemLog(new SystemLog(adminUserId, "ADMIN_ERROR", "Invalid ID format in action " + action + ": " + e.getMessage(), ipAddress));
        } catch (Exception e) {
            message = "An unexpected error occurred: " + e.getMessage();
            System.err.println("General Exception in AdminServlet for action " + action + ": " + e.getMessage());
            e.printStackTrace();
            systemLogDAO.addSystemLog(new SystemLog(adminUserId, "ADMIN_ERROR", "Unexpected error for action " + action + ": " + e.getMessage(), ipAddress));
        }

        // Set message and redirect back to dashboard for most actions
        if (success) {
            request.setAttribute("successMessage", message);
        } else {
            request.setAttribute("errorMessage", message);
        }
        doGet(request, response); // Re-fetch data and display dashboard
    }
}
