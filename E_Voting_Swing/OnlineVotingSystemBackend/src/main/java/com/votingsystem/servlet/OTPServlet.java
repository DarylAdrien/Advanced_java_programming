// src/com/votingsystem/servlet/OTPServlet.java
package com.votingsystem.servlet;

import com.votingsystem.model.User;
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

@WebServlet("/otp")
public class OTPServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private AuthService authService = new AuthService();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject jsonResponse = new JSONObject();

        String action = request.getParameter("action"); // "generate" or "verify"

        HttpSession session = request.getSession(false); // Do not create a new session if none exists

        if (session == null || session.getAttribute("loggedInUser") == null) {
            jsonResponse.put("success", false);
            jsonResponse.put("message", "User not logged in or session expired.");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
            out.print(jsonResponse.toString());
            return;
        }

        User currentUser = (User) session.getAttribute("loggedInUser");
        int userId = currentUser.getUserId();

        if ("generate".equals(action)) {
            // Logic to generate and send OTP
            String otpCode = authService.generateAndStoreOTP(userId);
            if (otpCode != null) {
                // In a real app, you'd send this OTP via SMS/Email here.
                // For this project, we'll assume the frontend will prompt for it.
                jsonResponse.put("success", true);
                jsonResponse.put("message", "OTP generated and sent (conceptually) to your registered contact. Please enter it to proceed.");
                // For development/testing, you might want to return the OTP to console or directly here for debugging.
                // jsonResponse.put("otp", otpCode); // REMOVE THIS IN PRODUCTION!
            } else {
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Failed to generate OTP. Please try again.");
            }
        } else if ("verify".equals(action)) {
            // Logic to verify OTP
            String otpEntered = request.getParameter("otpCode");

            if (otpEntered == null || otpEntered.trim().isEmpty()) {
                jsonResponse.put("success", false);
                jsonResponse.put("message", "OTP is required for verification.");
                out.print(jsonResponse.toString());
                return;
            }

            if (authService.validateOTP(userId, otpEntered)) {
                // Mark user as OTP-verified in session for current session
                session.setAttribute("otpVerified", true);
                jsonResponse.put("success", true);
                jsonResponse.put("message", "OTP verified successfully. You can now access full features.");
            } else {
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Invalid or expired OTP. Please try again.");
            }
        } else {
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Invalid action specified for OTP Servlet.");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400 Bad Request
        }
        out.print(jsonResponse.toString());
    }
}