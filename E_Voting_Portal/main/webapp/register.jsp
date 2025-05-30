<%-- src/main/webapp/register.jsp --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%-- Include header --%>
<c:set var="pageTitle" value="Register" scope="request"/>
<c:set var="currentPage" value="register" scope="request"/>
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>

<section class="form-container">
    <h2>Register for E-Voting Portal</h2>
    <p class="text-center">Join us to participate in the upcoming elections or to stand as a candidate.</p>

    <form action="${pageContext.request.contextPath}/register" method="post">
        <div class="form-group">
            <label for="username">Username:</label>
            <input type="text" id="username" name="username" required>
        </div>
        <div class="form-group">
            <label for="password">Password:</label>
            <input type="password" id="password" name="password" required>
        </div>
        <div class="form-group">
            <label for="confirmPassword">Confirm Password:</label>
            <input type="password" id="confirmPassword" name="confirmPassword" required>
        </div>
        <div class="form-group">
            <label for="firstName">First Name:</label>
            <input type="text" id="firstName" name="firstName" required>
        </div>
        <div class="form-group">
            <label for="lastName">Last Name:</label>
            <input type="text" id="lastName" name="lastName">
        </div>
        <div class="form-group">
            <label for="email">Email:</label>
            <input type="email" id="email" name="email" required>
        </div>
        <div class="form-group">
            <label for="phoneNumber">Phone Number (Optional):</label>
            <input type="tel" id="phoneNumber" name="phoneNumber" pattern="[0-9]{10}" title="Phone number must be 10 digits">
        </div>	
        <div class="form-group">
            <label for="aadhaarNumber">Voter Number (7 digits):</label>
            <input type="text" id="aadhaarNumber" name="aadhaarNumber" pattern="[0-9]{7}" title="Aadhaar number must be 12 digits" required>
        </div>
        <div class="form-group">
            <label for="constituencyId">Select Your Constituency:</label>
            <select id="constituencyId" name="constituencyId" required>
                <option value="">-- Select Constituency --</option>
                <c:forEach var="constituency" items="${requestScope.constituencies}">
                    <option value="${constituency.constituencyId}">${constituency.name}</option>
                </c:forEach>
            </select>
        </div>
        <div class="form-group">
            <label>Register As:</label>
            <div class="radio-group">
                <label><input type="radio" name="role" value="VOTER" checked> Voter</label>
                <label><input type="radio" name="role" value="CANDIDATE"> Candidate</label>
            </div>
        </div>
        <div class="form-group text-center">
            <button type="submit" class="btn btn-primary">Register</button>
        </div>
        <p class="text-center mt-20">Already have an account? <a href="${pageContext.request.contextPath}/login.jsp">Login here</a></p>
    </form>
</section>

<%-- Include footer --%>
<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
