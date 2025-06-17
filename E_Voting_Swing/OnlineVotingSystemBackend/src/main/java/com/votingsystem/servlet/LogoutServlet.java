// src/com/votingsystem/servlet/LogoutServlet.java
package com.votingsystem.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import org.json.JSONObject;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response); // Allow GET for logout for simplicity, though POST is generally safer
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject jsonResponse = new JSONObject();

        HttpSession session = request.getSession(false); // Do not create if it doesn't exist

        if (session != null) {
            String username = (String) session.getAttribute("username");
            session.invalidate(); // Invalidate the session
            // Optional: Log logout
            // new AuthService().addLog(null, (Integer) session.getAttribute("userId"), "User Logout", "User " + username + " logged out.");
            jsonResponse.put("success", true);
            jsonResponse.put("message", "Logged out successfully.");
        } else {
            jsonResponse.put("success", false);
            jsonResponse.put("message", "No active session to log out from.");
        }
        out.print(jsonResponse.toString());
    }
}