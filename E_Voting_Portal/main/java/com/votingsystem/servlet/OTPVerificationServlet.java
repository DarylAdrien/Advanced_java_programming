// src/main/java/com/votingsystem/servlet/OTPVerificationServlet.java
package com.votingsystem.servlet;

import com.votingsystem.dao.OTPDAO;

import com.votingsystem.dao.SystemLogDAO;
import com.votingsystem.dao.UserDAO;
import com.votingsystem.model.SystemLog;
import com.votingsystem.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Servlet for handling OTP generation and verification for 2-step authentication.
 */
@WebServlet("/otpVerification")
public class OTPVerificationServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private OTPDAO otpDAO;
    private UserDAO userDAO;
    private SystemLogDAO systemLogDAO;

    /**
     * Initializes the servlet and its DAOs.
     */
    @Override
    public void init() throws ServletException {
        super.init();
        otpDAO = new OTPDAO();
        userDAO = new UserDAO();
        systemLogDAO = new SystemLogDAO();
    }

    /**
     * Handles GET requests to display the OTP verification page or regenerate OTP.
     * If a user requires OTP, this servlet will generate and (simulated) send it.
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

        if (session == null || session.getAttribute("tempUserId") == null) {
            // If no temporary user ID in session, redirect to login
            response.sendRedirect("index.jsp");
            return;
        }

        Integer tempUserId = (Integer) session.getAttribute("tempUserId");
        String username = (String) session.getAttribute("username"); // Get username for logging

        // Generate and store a new OTP
        String generatedOtp = otpDAO.generateAndStoreOTP(tempUserId);

        if (generatedOtp != null) {
            // In a real application, you would send this OTP via SMS or Email.
            // For this example, we'll just print it to the console.
            System.out.println("OTP generated for user " + username + " (ID: " + tempUserId + "): " + generatedOtp);
            systemLogDAO.addSystemLog(new SystemLog(tempUserId, "OTP_GENERATED", "OTP generated for user: " + username, ipAddress));
            request.setAttribute("message", "An OTP has been sent to your registered contact. Please enter it below.");
            request.getRequestDispatcher("otpVerification.jsp").forward(request, response);
        } else {
            request.setAttribute("errorMessage", "Failed to generate OTP. Please try again.");
            systemLogDAO.addSystemLog(new SystemLog(tempUserId, "OTP_GENERATION_FAILED", "Failed to generate OTP for user: " + username, ipAddress));
            request.getRequestDispatcher("index.jsp").forward(request, response);
        }
    }

    /**
     * Handles POST requests for OTP verification.
     * Validates the entered OTP and, if successful, completes the login process.
     *
     * @param request The HttpServletRequest object containing the OTP.
     * @param response The HttpServletResponse object.
     * @throws ServletException If a servlet-specific error occurs.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        String ipAddress = request.getRemoteAddr();

        if (session == null || session.getAttribute("tempUserId") == null) {
            response.sendRedirect("index.jsp"); // Redirect to login if session is invalid
            return;
        }

        Integer tempUserId = (Integer) session.getAttribute("tempUserId");
        String enteredOtp = request.getParameter("otp");

        if (enteredOtp == null || enteredOtp.trim().isEmpty()) {
            request.setAttribute("errorMessage", "OTP cannot be empty.");
            systemLogDAO.addSystemLog(new SystemLog(tempUserId, "OTP_VERIFICATION_FAILED", "Empty OTP entered for user ID: " + tempUserId, ipAddress));
            request.getRequestDispatcher("otpVerification.jsp").forward(request, response);
            return;
        }

        if (otpDAO.validateOTP(tempUserId, enteredOtp)) {
            // OTP is valid, complete the login
            User user = userDAO.getUserById(tempUserId);
            if (user != null) {
                // Transfer temporary session attributes to permanent ones
                session.removeAttribute("tempUserId"); // Remove temporary ID
                session.setAttribute("userId", user.getUserId());
                session.setAttribute("username", user.getUsername());
                session.setAttribute("role", user.getRole());
                session.setAttribute("firstName", user.getFirstName());
                session.setAttribute("lastName", user.getLastName());
                session.setAttribute("email", user.getEmail());
                session.setMaxInactiveInterval(30 * 60); // Session timeout in seconds (30 minutes)

                systemLogDAO.addSystemLog(new SystemLog(user.getUserId(), "LOGIN_SUCCESS_OTP", "User logged in with OTP: " + user.getUsername(), ipAddress));
                System.out.println("OTP verified. User " + user.getUsername() + " logged in successfully as " + user.getRole());

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
                        request.getRequestDispatcher("index.jsp").forward(request, response);
                        break;
                }
            } else {
                request.setAttribute("errorMessage", "User not found after OTP verification. Please try logging in again.");
                systemLogDAO.addSystemLog(new SystemLog(tempUserId, "OTP_VERIFICATION_ERROR", "User not found after OTP verification for tempUserId: " + tempUserId, ipAddress));
                response.sendRedirect("index.jsp");
            }
        } else {
            request.setAttribute("errorMessage", "Invalid or expired OTP. Please try again.");
            systemLogDAO.addSystemLog(new SystemLog(tempUserId, "OTP_VERIFICATION_FAILED", "Invalid or expired OTP entered for user ID: " + tempUserId, ipAddress));
            request.getRequestDispatcher("otpVerification.jsp").forward(request, response);
        }
    }
}
