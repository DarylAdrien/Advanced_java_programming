// src/com/votingsystem/servlet/LoginServlet.java
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
import org.json.JSONObject; // For JSON response

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private AuthService authService = new AuthService();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject jsonResponse = new JSONObject();

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (username == null || username.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Username and password are required.");
            out.print(jsonResponse.toString());
            return;
        }

        User authenticatedUser = authService.authenticateUser(username, password);

        if (authenticatedUser != null) {
            // Create or get session
            HttpSession session = request.getSession(true);
            session.setAttribute("loggedInUser", authenticatedUser); // Store the entire User object
            session.setAttribute("userId", authenticatedUser.getUserId());
            session.setAttribute("username", authenticatedUser.getUsername());
            session.setAttribute("roleId", authenticatedUser.getRoleId());
            session.setAttribute("roleName", authenticatedUser.getRoleName()); // Store role name for easy access

            jsonResponse.put("success", true);
            jsonResponse.put("message", "Login successful!");
            jsonResponse.put("userId", authenticatedUser.getUserId());
            jsonResponse.put("username", authenticatedUser.getUsername());
            jsonResponse.put("roleId", authenticatedUser.getRoleId());
            jsonResponse.put("roleName", authenticatedUser.getRoleName()); // Send role name to client
            jsonResponse.put("fullName", authenticatedUser.getFullName());
        } else {
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Invalid username or password.");
        }
        out.print(jsonResponse.toString());
    }
}