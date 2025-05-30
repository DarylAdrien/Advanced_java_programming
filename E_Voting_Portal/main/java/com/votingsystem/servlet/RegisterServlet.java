// src/main/java/com/votingsystem/servlet/RegisterServlet.java
package com.votingsystem.servlet;

import com.votingsystem.dao.ConstituencyDAO;
import com.votingsystem.dao.SystemLogDAO;
import com.votingsystem.dao.UserDAO;
import com.votingsystem.model.Constituency;
import com.votingsystem.model.SystemLog;
import com.votingsystem.model.User;
import com.votingsystem.util.PasswordUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Servlet for handling user registration requests.
 * Processes new user sign-ups, hashes passwords, and stores user data.
 * Sets initial registration status to 'pending' for admin approval.
 */
@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserDAO userDAO;
    private ConstituencyDAO constituencyDAO;
    private SystemLogDAO systemLogDAO;

    /**
     * Initializes the servlet and its DAOs.
     */
    @Override
    public void init() throws ServletException {
        super.init();
        userDAO = new UserDAO();
        constituencyDAO = new ConstituencyDAO();
        systemLogDAO = new SystemLogDAO();
    }

    /**
     * Handles GET requests to display the registration form.
     * Populates the request with a list of constituencies for the dropdown.
     *
     * @param request The HttpServletRequest object.
     * @param response The HttpServletResponse object.
     * @throws ServletException If a servlet-specific error occurs.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<Constituency> constituencies = constituencyDAO.getAllConstituencies();
            request.setAttribute("constituencies", constituencies);
            request.getRequestDispatcher("register.jsp").forward(request, response);
        } catch (Exception e) {
            System.err.println("Error fetching constituencies for registration: " + e.getMessage());
            systemLogDAO.addSystemLog(new SystemLog(null, "REGISTRATION_ERROR", "Failed to load registration page due to database error: " + e.getMessage(), request.getRemoteAddr()));
            request.setAttribute("errorMessage", "An error occurred while loading the registration form. Please try again later.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }

    /**
     * Handles POST requests for submitting the registration form.
     * Validates input, hashes password, creates a new user, and redirects.
     *
     * @param request The HttpServletRequest object containing registration parameters.
     * @param response The HttpServletResponse object for sending responses.
     * @throws ServletException If a servlet-specific error occurs.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String email = request.getParameter("email");
        String phoneNumber = request.getParameter("phoneNumber");
        String aadhaarNumber = request.getParameter("aadhaarNumber");
        String constituencyIdStr = request.getParameter("constituencyId");
        String role = request.getParameter("role"); // 'VOTER' or 'CANDIDATE' from form
        String ipAddress = request.getRemoteAddr();

        // Basic input validation
        if (username == null || username.trim().isEmpty() ||
            password == null || password.trim().isEmpty() ||
            confirmPassword == null || confirmPassword.trim().isEmpty() ||
            firstName == null || firstName.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            aadhaarNumber == null || aadhaarNumber.trim().isEmpty() ||
            constituencyIdStr == null || constituencyIdStr.trim().isEmpty() ||
            role == null || role.trim().isEmpty()) {

            request.setAttribute("errorMessage", "All required fields must be filled.");
            systemLogDAO.addSystemLog(new SystemLog(null, "REGISTRATION_FAILED", "Missing required fields for username: " + username, ipAddress));
            doGet(request, response); // Re-display form with error
            return;
        }

        if (!password.equals(confirmPassword)) {
            request.setAttribute("errorMessage", "Passwords do not match.");
            systemLogDAO.addSystemLog(new SystemLog(null, "REGISTRATION_FAILED", "Passwords mismatch for username: " + username, ipAddress));
            doGet(request, response); // Re-display form with error
            return;
        }

        int constituencyId = 0;
        
        try {
            constituencyId = Integer.parseInt(constituencyIdStr);
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid constituency selected.");
            systemLogDAO.addSystemLog(new SystemLog(null, "REGISTRATION_FAILED", "Invalid constituency ID for username: " + username, ipAddress));
            doGet(request, response);
            return;
        }

        // Check if username, email, or Aadhaar already exists
        if (userDAO.checkUserExists(username, email, aadhaarNumber)) {
            request.setAttribute("errorMessage", "Username, Email, or Aadhaar Number already exists. Please use different credentials or login.");
            systemLogDAO.addSystemLog(new SystemLog(null, "REGISTRATION_FAILED", "Duplicate credentials for username: " + username, ipAddress));
            doGet(request, response);
            return;
        }

        // Hash the password
        String hashedPassword = PasswordUtil.hashPassword(password);
        if (hashedPassword == null) {
            request.setAttribute("errorMessage", "An error occurred during password processing. Please try again.");
            systemLogDAO.addSystemLog(new SystemLog(null, "REGISTRATION_ERROR", "Password hashing failed for username: " + username, ipAddress));
            doGet(request, response);
            return;
        }

        // Create User object
        
        User newUser = new User(username, hashedPassword, role.toUpperCase(), firstName, lastName, email,
                                phoneNumber, aadhaarNumber, constituencyId, 1, 1);

        int newUserId = userDAO.addUser(newUser);

        if (newUserId != -1) {
            systemLogDAO.addSystemLog(new SystemLog(newUserId, "REGISTRATION_SUCCESS", "New user registered: " + username + " with role: " + role, ipAddress));
            request.setAttribute("successMessage", "Registration successful! Your account is pending admin approval. You will be notified once approved.");
            request.getRequestDispatcher("index.jsp").forward(request, response);
        } else {
            request.setAttribute("errorMessage", "Registration failed. Please try again.");
            systemLogDAO.addSystemLog(new SystemLog(null, "REGISTRATION_FAILED", "Database error during registration for username: " + username, ipAddress));
            doGet(request, response); // Re-display form with error
        }
    }
}
