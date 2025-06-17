// src/com/votingsystem/servlet/RegisterServlet.java
package com.votingsystem.servlet;

import com.votingsystem.model.User;
import com.votingsystem.service.AuthService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date; // Use java.sql.Date for DB insertion
import org.json.JSONObject; // For JSON response

// Note: @WebServlet is a Servlet 3.0+ feature. If your web.xml is older (2.5), you might need to map it there.
// Since we set up for 3.0, this annotation is fine.
@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private AuthService authService = new AuthService();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json"); // Indicate JSON response
        PrintWriter out = response.getWriter();
        JSONObject jsonResponse = new JSONObject();

        // 1. Get parameters from the request
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String email = request.getParameter("email");
        String phoneNumber = request.getParameter("phoneNumber");
        String fullName = request.getParameter("fullName");
        String dobString = request.getParameter("dateOfBirth");
        String address = request.getParameter("address");
        String aadhaarNumber = request.getParameter("aadhaarNumber");
        String roleParam = request.getParameter("roleId"); // Expected to be 2 for Voter, 3 for Candidate
        String constituencyParam = request.getParameter("constituencyId"); // Optional

        // 2. Input Validation (Basic)
        if (username == null || username.trim().isEmpty() ||
            password == null || password.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            fullName == null || fullName.trim().isEmpty() ||
            aadhaarNumber == null || aadhaarNumber.trim().isEmpty() ||
            roleParam == null || roleParam.trim().isEmpty()) {
            jsonResponse.put("success", false);
            jsonResponse.put("message", "All required fields (username, password, email, full name, Aadhaar, role) must be provided.");
            out.print(jsonResponse.toString());
            return;
        }

        int roleId;
        try {
            roleId = Integer.parseInt(roleParam);
            // Basic role validation: only allow Voter (2) or Candidate (3) for self-registration
            if (roleId != 2 && roleId != 3) {
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Invalid role specified for registration. Only Voter or Candidate roles allowed.");
                out.print(jsonResponse.toString());
                return;
            }
        } catch (NumberFormatException e) {
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Invalid role ID format.");
            out.print(jsonResponse.toString());
            return;
        }

        Integer constituencyId = null;
        if (constituencyParam != null && !constituencyParam.trim().isEmpty()) {
            try {
                constituencyId = Integer.parseInt(constituencyParam);
            } catch (NumberFormatException e) {
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Invalid constituency ID format.");
                out.print(jsonResponse.toString());
                return;
            }
        }

        Date dateOfBirth = null;
        if (dobString != null && !dobString.trim().isEmpty()) {
            try {
                dateOfBirth = Date.valueOf(dobString); // Assumes YYYY-MM-DD format
            } catch (IllegalArgumentException e) {
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Invalid date of birth format. Use YYYY-MM-DD.");
                out.print(jsonResponse.toString());
                return;
            }
        }

        // 3. Check if username, email, or Aadhaar already exists
        if (authService.checkIfUserExists(username, email, aadhaarNumber)) {
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Username, Email, or Aadhaar Number already registered.");
            out.print(jsonResponse.toString());
            return;
        }

        // 4. Create User object and attempt registration
        User newUser = new User(username, email, phoneNumber, fullName,
                                dateOfBirth, address, aadhaarNumber, roleId, constituencyId);

        User registeredUser = authService.registerUser(newUser, password);

        // 5. Prepare JSON response
        if (registeredUser != null) {
            jsonResponse.put("success", true);
            jsonResponse.put("message", "Registration successful. Welcome, " + registeredUser.getFullName() + "!");
            jsonResponse.put("userId", registeredUser.getUserId());
            jsonResponse.put("username", registeredUser.getUsername());
            jsonResponse.put("roleId", registeredUser.getRoleId());
            // Do NOT send password hash or salt back to the client
        } else {
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Registration failed. Please try again.");
        }
        out.print(jsonResponse.toString());
    }
}