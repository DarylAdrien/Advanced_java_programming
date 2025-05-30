// src/main/java/com/votingsystem/filter/AuthenticationFilter.java
package com.votingsystem.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Servlet Filter to ensure that users are authenticated before accessing protected resources.
 * It redirects unauthenticated users to the login page.
 */
@WebFilter(urlPatterns = {"/admin/*", "/voter/*", "/candidate/*", "/dashboard.jsp"})
public class AuthenticationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization code if needed
        System.out.println("AuthenticationFilter initialized.");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false); // Do not create a new session if one doesn't exist

        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();

        // Allow access to login, registration, OTP verification, and static resources
        // Adjust these patterns based on your actual application structure
        boolean isLoginOrRegisterPage = requestURI.endsWith("index.jsp") ||
                                       requestURI.endsWith("register.jsp") ||
                                       requestURI.endsWith("otpVerification.jsp") ||
                                       requestURI.endsWith("/login") ||
                                       requestURI.endsWith("/register") ||
                                       requestURI.endsWith("/otpVerification");

        boolean isStaticResource = requestURI.contains("/css/") ||
                                   requestURI.contains("/js/") ||
                                   requestURI.contains("/images/");

        // If the user is trying to access a login/register page or a static resource, let them through
        if (isLoginOrRegisterPage || isStaticResource) {
            chain.doFilter(request, response);
            return;
        }

        // Check if the user is logged in (session attribute "userId" exists)
        // For OTP verification, "tempUserId" is used, so we need to allow it.
        boolean isLoggedIn = (session != null && session.getAttribute("userId") != null);
        boolean isOtpPending = (session != null && session.getAttribute("tempUserId") != null && requestURI.endsWith("otpVerification.jsp"));


        if (isLoggedIn || isOtpPending) {
            // User is logged in or is in the process of OTP verification, proceed
            chain.doFilter(request, response);
        } else {
            // User is not logged in, redirect to login page
            System.out.println("AuthenticationFilter: Unauthenticated access to " + requestURI + ". Redirecting to login.");
            httpResponse.sendRedirect(contextPath + "/index.jsp"); // Redirect to your main login page
        }
    }

    @Override
    public void destroy() {
        // Cleanup code if needed
        System.out.println("AuthenticationFilter destroyed.");
    }
}
