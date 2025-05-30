<%-- src/main/webapp/help.jsp --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%-- Include header --%>
<c:set var="pageTitle" value="Help & Support" scope="request"/>
<c:set var="currentPage" value="help" scope="request"/>
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>

<section>
    <h2>Help & Support</h2>
    <p>Welcome to the E-Voting Portal Help Center. Here you can find answers to common questions and get assistance with using our platform.</p>

    <h3>Frequently Asked Questions (FAQs)</h3>

    <div class="card-grid">
        <div class="card">
            <h3><i class="fas fa-question-circle"></i> How do I register?</h3>
            <p>Click on the "Register" link in the navigation bar. Fill out the required details, including your Aadhaar number and constituency. Your registration will be pending admin approval.</p>
        </div>
        <div class="card">
            <h3><i class="fas fa-key"></i> What is 2-Step Verification (OTP)?</h3>
            <p>2-Step Verification adds an extra layer of security to your account. When enabled, after entering your password, you'll receive a One-Time Password (OTP) on your registered contact, which you need to enter to log in.</p>
        </div>
        <div class="card">
            <h3><i class="fas fa-vote-yea"></i> How do I cast my vote?</h3>
            <p>Once logged in as a Voter, navigate to your dashboard. If there's an active election in your constituency and you haven't voted yet, you will see the list of candidates. Select your preferred candidate and click "Cast Vote".</p>
        </div>
        <div class="card">
            <h3><i class="fas fa-user-tie"></i> How can I become a candidate?</h3>
            <p>Register as a "Candidate" during sign-up. After admin approval, you can log in to your Candidate Dashboard and apply for upcoming elections in your constituency by submitting your party, symbol, and manifesto.</p>
        </div>
        <div class="card">
            <h3><i class="fas fa-poll"></i> Where can I see election results?</h3>
            <p>Election results will be available on the portal once an election has concluded and results are officially declared by the administrators. Admins have a dedicated section to view and manage results.</p>
        </div>
        <div class="card">
            <h3><i class="fas fa-shield-alt"></i> Is my vote secure?</h3>
            <p>Yes, our system employs secure authentication, data encryption, and logging mechanisms to ensure the integrity and privacy of your vote. Each vote is recorded securely and anonymously linked to prevent double voting.</p>
        </div>
    </div>

    <h3>Contact Support</h3>
    <p>If you have further questions or encounter issues not covered here, please contact our support team:</p>
    <ul>
        <li><strong>Email:</strong> support@evoting2026.com</li>
        <li><strong>Phone:</strong> +91 12345 67890 (Mon-Fri, 9 AM - 5 PM IST)</li>
    </ul>
</section>

<%-- Include footer --%>
<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
