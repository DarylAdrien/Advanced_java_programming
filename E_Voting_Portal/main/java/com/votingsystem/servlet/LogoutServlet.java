// src/main/java/com/votingsystem/servlet/LogoutServlet.java
package com.votingsystem.servlet;

import com.votingsystem.dao.SystemLogDAO;
import com.votingsystem.model.SystemLog;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Servlet for handling user logout requests.
 * Invalidates the current user session and redirects to the login page.
 */
@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private SystemLogDAO systemLogDAO;

    /**
     * Initializes the servlet and its DAOs.
     */
    @Override
    public void init() throws ServletException {
        super.init();
        systemLogDAO = new SystemLogDAO();
    }

    /**
     * Handles GET requests for user logout.
     * Invalidates the session and logs the logout event.
     *
     * @param request The HttpServletRequest object.
     * @param response The HttpServletResponse object.
     * @throws ServletException If a servlet-specific error occurs.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false); // Do not create a new session if one doesn't exist
        String username = null;
        Integer userId = null;
        String ipAddress = request.getRemoteAddr();

        if (session != null) {
            username = (String) session.getAttribute("username");
            userId = (Integer) session.getAttribute("userId"); // userId might be null if temp session for OTP
            session.invalidate(); // Invalidate the session
            System.out.println("User " + username + " (ID: " + userId + ") logged out successfully.");
        }

        // Log the logout action
        systemLogDAO.addSystemLog(new SystemLog(userId, "LOGOUT_SUCCESS", "User logged out: " + (username != null ? username : "unknown"), ipAddress));

        // Redirect to the index.jsp (login page)
        response.sendRedirect("index.jsp");
    }

    /**
     * Handles POST requests for user logout (though GET is more common for logout).
     * Delegates to doGet method.
     *
     * @param request The HttpServletRequest object.
     * @param response The HttpServletResponse object.
     * @throws ServletException If a servlet-specific error occurs.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
