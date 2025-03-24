package com.vote;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/VoteServlet")
public class VoteServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login.html");
            return;
        }

        String voter_id = (String) session.getAttribute("user");
        String candidate = request.getParameter("candidate");

        try {
            Connection con = DriverManager.getConnection(
                "jdbc:oracle:thin:@localhost:1521:XE", "daryl", "daryl");

            // Check if user has already voted
//            PreparedStatement checkVote = con.prepareStatement("SELECT * FROM votes1 WHERE voter_id=?");
            PreparedStatement checkVote = con.prepareStatement("SELECT 1 FROM votes1 WHERE voter_id=?");

            checkVote.setString(1, voter_id);
            if (checkVote.executeQuery().next()) {	
                response.getWriter().println("You have already voted!");
                return;
            }

            // Insert vote
            PreparedStatement ps = con.prepareStatement("INSERT INTO votes1 (voter_id, candidate) VALUES (?, ?)");
            ps.setString(1, voter_id);
            ps.setString(2, candidate);
            ps.executeUpdate();

            session.setAttribute("voted", true);
            response.getWriter().println("Vote submitted successfully!");
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
