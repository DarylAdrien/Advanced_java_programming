<%-- src/main/webapp/otpVerification.jsp --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%-- Include header --%>
<c:set var="pageTitle" value="OTP Verification" scope="request"/>
<c:set var="currentPage" value="otpVerification" scope="request"/>
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>

<section class="form-container">
    <h2>OTP Verification</h2>
    <p class="text-center">An OTP has been sent to your registered contact number/email. Please enter it below to complete your login.</p>

    <form action="${pageContext.request.contextPath}/otpVerification" method="post">
        <div class="form-group">
            <label for="otp">Enter OTP:</label>
            <input type="text" id="otp" name="otp" pattern="[0-9]{6}" title="OTP must be 6 digits" required autofocus>
        </div>
        <div class="form-group text-center">
            <button type="submit" class="btn btn-primary">Verify OTP</button>
        </div>
        <p class="text-center mt-20">
            Didn't receive the OTP? <a href="${pageContext.request.contextPath}/otpVerification">Resend OTP</a>
        </p>
    </form>
</section>

<script>
    window.onload = function () {
        fetch("${pageContext.request.contextPath}/otpVerification")
            .then(response => response.text())
            .then(data => {
                console.log("OTP generated successfully.");
            })
            .catch(error => {
                console.error("Error generating OTP:", error);
            });
    };
</script>


<%-- Include footer --%>
<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
