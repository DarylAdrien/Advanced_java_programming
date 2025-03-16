package com.test;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/PageServlet")
public class PageServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String page = request.getParameter("page");

        if ("page1".equalsIgnoreCase(page)) {
            response.sendRedirect("page1.html");
        } else if ("page2".equalsIgnoreCase(page)) {
            response.sendRedirect("page2.html");
        } else {
            response.getWriter().println("<h2>Invalid Page Selection!</h2>");
        }
    }
}