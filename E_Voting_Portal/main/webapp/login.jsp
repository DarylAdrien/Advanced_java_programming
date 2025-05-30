<%-- src/main/webapp/login.jsp --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%-- Include header --%>
<c:set var="pageTitle" value="Login" scope="request"/>
<c:set var="currentPage" value="login" scope="request"/>
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>

<div class="main-content-wrapper"> <%-- New wrapper for centering --%>
    <section id="login-section" class="form-container">
        <h3>Login to Your Account</h3>
        <form action="${pageContext.request.contextPath}/login" method="post">
            <div class="form-group">
                <label for="username">Username:</label>
                <input type="text" id="username" name="username" required>
            </div>
            <div class="form-group">
                <label for="password">Password:</label>
                <input type="password" id="password" name="password" required>
            </div>
            <div class="form-group text-center">
                <button type="submit" class="btn btn-primary">Login</button>
            </div>
            <p class="text-center mt-20">Don't have an account? <a href="${pageContext.request.contextPath}/register">Register here</a></p>
        </form>
    </section>
</div>

<%-- Include footer --%>
<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>