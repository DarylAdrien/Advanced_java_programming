// src/main/java/com/votingsystem/servlet/LoginServlet.java
package com.votingsystem.servlet;

import com.votingsystem.dao.SystemLogDAO;
import com.votingsystem.dao.UserDAO;
import com.votingsystem.model.SystemLog;
import com.votingsystem.model.User;
import com.votingsystem.util.PasswordUtil; // Import the PasswordUtil

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet; // For modern servlet mapping (Servlet 3.0+)
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Servlet for handling user login requests.
 * Authenticates users and redirects them to their respective dashboards based on role.
 * Also handles logging of login attempts.
 */
@WebServlet("/login") // Maps this servlet to the /login URL pattern
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserDAO userDAO;
    private SystemLogDAO systemLogDAO;

    /**
     * Initializes the servlet and its DAOs.
     */
    @Override
    public void init() throws ServletException {
        super.init();
        userDAO = new UserDAO();
        systemLogDAO = new SystemLogDAO();
    }

    /**
     * Handles POST requests for user login.
     * Retrieves username and password, authenticates the user,
     * sets up the session, and redirects to the appropriate dashboard.
     *
     * @param request The HttpServletRequest object containing login parameters.
     * @param response The HttpServletResponse object for sending responses.
     * @throws ServletException If a servlet-specific error occurs.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String ipAddress = request.getRemoteAddr(); // Get client IP for logging

        User user = userDAO.getUserByUsername(username);
        boolean isAuthenticated = false;

        if (user != null) {
            // Use PasswordUtil to verify the password
            if (PasswordUtil.verifyPassword(password, user.getPasswordHash())) {
                isAuthenticated = true;
                // Check if the user is registered (approved by admin)
                if (user.getRegistered() == 0) {
                    request.setAttribute("errorMessage", "Your account is pending approval. Please wait for an administrator to approve your registration.");
                    systemLogDAO.addSystemLog(new SystemLog(user.getUserId(), "LOGIN_FAILED", "Account pending approval for user: " + username, ipAddress));
                    request.getRequestDispatcher("login.jsp").forward(request, response); // Forward to login.jsp
                    return;
                }

                // Check for OTP enabled and redirect to OTP verification if needed
                if (user.getOtpEnabled() == 0) {
                    HttpSession session = request.getSession();
                    session.setAttribute("tempUserId", user.getUserId()); // Store user ID temporarily
                    session.setAttribute("username", user.getUsername());
                    session.setAttribute("role", user.getRole());
                    System.out.println("User " + username + " requires OTP. Redirecting to OTP verification.");
                    systemLogDAO.addSystemLog(new SystemLog(user.getUserId(), "LOGIN_OTP_REQUIRED", "User requires OTP verification: " + username, ipAddress));
                    response.sendRedirect("otpVerification.jsp"); // Redirect to OTP verification page
                    return;
                }

                // If OTP is not enabled or successfully verified (handled by OTPVerificationServlet)
                // Set session attributes for logged-in user
                HttpSession session = request.getSession();
                session.setAttribute("userId", user.getUserId());
                session.setAttribute("username", user.getUsername());
                session.setAttribute("role", user.getRole());
                session.setAttribute("firstName", user.getFirstName());
                session.setAttribute("lastName", user.getLastName());
                session.setAttribute("email", user.getEmail());
                session.setMaxInactiveInterval(30 * 60); // Session timeout in seconds (30 minutes)

                systemLogDAO.addSystemLog(new SystemLog(user.getUserId(), "LOGIN_SUCCESS", "User logged in: " + username, ipAddress));
                System.out.println("User " + username + " logged in successfully as " + user.getRole());

                // Redirect to appropriate dashboard based on role
                switch (user.getRole()) {
                    case "ADMIN":
                        response.sendRedirect("admin");
                        break;
                    case "VOTER":
                        response.sendRedirect("voter");
                        break;
                    case "CANDIDATE":
                        response.sendRedirect("candidate");
                        break;
                    default:
                        request.setAttribute("errorMessage", "Unknown user role. Please contact support.");
                        request.getRequestDispatcher("login.jsp").forward(request, response); // Forward to login.jsp
                        break;
                }
            } else {
                request.setAttribute("errorMessage", "Invalid username or password.");
                systemLogDAO.addSystemLog(new SystemLog(null, "LOGIN_FAILED", "Invalid password for user: " + username, ipAddress));
                request.getRequestDispatcher("login.jsp").forward(request, response); // Forward to login.jsp
            }
        } else {
            request.setAttribute("errorMessage", "Invalid username or password.");
            systemLogDAO.addSystemLog(new SystemLog(null, "LOGIN_FAILED", "Attempted login with non-existent username: " + username, ipAddress));
            request.getRequestDispatcher("login.jsp").forward(request, response); // Forward to login.jsp
        }
    }

    /**
     * Handles GET requests. For login, GET requests typically just display the login form.
     *
     * @param request The HttpServletRequest object.
     * @param response The HttpServletResponse object.
     * @throws ServletException If a servlet-specific error occurs.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Now forward to the dedicated login.jsp
        request.getRequestDispatcher("login.jsp").forward(request, response);
    }
}
