package com.vote;

import java.io.IOException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String voter_id = request.getParameter("voter_id");
        String password = request.getParameter("password");

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Connection con = DriverManager.getConnection(
                "jdbc:oracle:thin:@localhost:1521:XE", "daryl", "daryl");

            PreparedStatement ps = con.prepareStatement("SELECT * FROM users WHERE voter_id=? AND password=?");
            ps.setString(1, voter_id);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                HttpSession session = request.getSession();
                session.setAttribute("user", voter_id);
                session.setMaxInactiveInterval(10 * 60);
                response.sendRedirect("vote.html");
            } else {
                response.sendRedirect("login.html?error=Invalid Credentials");
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
