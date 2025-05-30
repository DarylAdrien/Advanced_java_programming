<%-- src/main/webapp/index.jsp --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %> <%-- Added for date formatting --%>
<%-- Include header --%>
<c:set var="pageTitle" value="Home" scope="request"/>
<c:set var="currentPage" value="home" scope="request"/>
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>

<section class="hero-section text-center">
    <div class="hero-content">
        <h2>Welcome to the E-Voting Portal</h2>
        <p>Your secure and transparent platform for the upcoming 2026 General Elections.</p>
        <p>Participate in democracy from the comfort of your home. Register, become a candidate, or cast your vote with ease and confidence.</p>
        <div class="btn-group mt-20">
            <a href="${pageContext.request.contextPath}/register" class="btn btn-primary">Register Now</a>
            <a href="${pageContext.request.contextPath}/login" class="btn btn-secondary">Login to Vote</a>
        </div>
    </div>
    <div class="hero-image">
        <img src="${pageContext.request.contextPath}/images/election_hero.png" alt="Election Booth" class="responsive-image">
    </div>
</section>

<%-- Scrolling Important Notices Section --%>
<div class="scrolling-text-container">
    <p class="scrolling-text">
        <i class="fas fa-bullhorn"></i> Important Notice: Voter registration for the upcoming elections closes on October 31, 2025. Register now to cast your vote! &bull; Stay tuned for candidate manifestos and election schedules. &bull; Secure your vote with our 2-step verification. &bull; Election results will be published shortly after the voting period concludes.
    </p>
</div>

<%-- New Grid for Upcoming and Recent Elections (STATIC CONTENT) --%>
<div class="election-summary-grid">
    <section class="election-info-section upcoming-elections-section">
        <h3 class="text-center"><i class="fas fa-calendar-alt"></i> Upcoming Elections</h3>
        <%-- Static Upcoming Elections --%>
        <div class="card-grid">
            <div class="card">
                <h3>General Election 2026</h3>
                <p>The main national election for parliamentary seats.</p>
                <p><strong>Starts:</strong> Nov 15,2025 09:00</p>
                <p><strong>Ends:</strong> Nov 20,2025 17:00</p>
                <span class="status-badge status-upcoming">UPCOMING</span>
            </div>
            <div class="card">
                <h3>State Assembly Election</h3>
                <p>Elections for the state legislative assembly.</p>
                <p><strong>Starts:</strong> Dec 01,2025 08:00</p>
                <p><strong>Ends:</strong> Dec 05,2025 18:00</p>
                <span class="status-badge status-upcoming">UPCOMING</span>
            </div>
           
        </div>
    </section>

    <section class="election-info-section recent-results-section">
        <h3 class="text-center"><i class="fas fa-poll"></i> Recent Election Results</h3>
        <%-- Static Recent Election Results --%>
        <div class="results-container">
            <h4>Election: By-Election 2024 (Oct 25,2024)</h4>
            <table class="data-table">
                <thead>
                    <tr>
                        <th>Candidate</th>
                        <th>Party</th>
                        <th>Votes</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td>John Doe</td>
                        <td>Unity Party</td>
                        <td><strong>12,500</strong></td>
                    </tr>
                    <tr>
                        <td>Jane Smith</td>
                        <td>Freedom Alliance</td>
                        <td><strong>9,800</strong></td>
                    </tr>
                    <tr>
                        <td>Robert Johnson</td>
                        <td>Progressive Front</td>
                        <td><strong>7,200</strong></td>
                    </tr>
                </tbody>
            </table>

            <h4 class="mt-20">Election: District Council Election (Sep 10,2024)</h4>
            <table class="data-table">
                <thead>
                    <tr>
                        <th>Candidate</th>
                        <th>Party</th>
                        <th>Votes</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td>Alice Brown</td>
                        <td>Green Initiative</td>
                        <td><strong>5,100</strong></td>
                    </tr>
                    <tr>
                        <td>Bob White</td>
                        <td>Citizens First</td>
                        <td><strong>4,500</strong></td>
                    </tr>
                </tbody>
            </table>
        </div>
    </section>
</div>

<section class="info-section">
    <h3>Key Features</h3>
    <div class="card-grid">
        <div class="card">
            <h3><i class="fas fa-user-shield"></i> Secure Authentication</h3>
            <p>Robust login system with optional 2-step verification for enhanced security.</p>
        </div>
        <div class="card">
            <h3><i class="fas fa-users-cog"></i> Role-Based Access</h3>
            <p>Distinct dashboards and functionalities for Voters, Candidates, and Administrators.</p>
        </div>
        <div class="card">
            <h3><i class="fas fa-map-marker-alt"></i> Constituency Management</h3>
            <p>Organized electoral divisions for accurate voter and candidate mapping.</p>
        </div>
        <div class="card">
            <h3><i class="fas fa-calendar-alt"></i> Election Details</h3>
            <p>Comprehensive information on upcoming, active, and completed elections.</p>
        </div>
        <div class="card">
            <h3><i class="fas fa-chart-bar"></i> Transparent Results</h3>
            <p>View real-time election results once voting concludes.</p>
        </div>
        <div class="card">
            <h3><i class="fas fa-file-alt"></i> System Logs</h3>
            <p>Detailed activity logs for auditing and transparency (Admin only).</p>
        </div>
    </div>
</section>

<%-- Include footer --%>
<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
