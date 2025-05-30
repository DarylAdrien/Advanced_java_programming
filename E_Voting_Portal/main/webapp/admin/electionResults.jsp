<%-- src/main/webapp/admin/electionResults.jsp --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%-- Include header --%>
<c:set var="pageTitle" value="Election Results" scope="request"/>
<c:set var="currentPage" value="adminDashboard" scope="request"/> <%-- Keep admin dashboard active --%>
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>

<section class="results-section">
    <h2>Election Results for "${requestScope.selectedElection.name}"</h2>
    <p class="text-center">Start Date: <fmt:formatDate value="${requestScope.selectedElection.startDate}" pattern="yyyy-MM-dd HH:mm"/></p>
    <p class="text-center">End Date: <fmt:formatDate value="${requestScope.selectedElection.endDate}" pattern="yyyy-MM-dd HH:mm"/></p>
    <p class="text-center">Status: <strong>${requestScope.selectedElection.status}</strong></p>

    <h3 class="mt-20"><i class="fas fa-globe"></i> Overall Election Results</h3>
    <c:if test="${empty requestScope.overallResults}">
        <p class="text-center">No votes recorded for this election yet.</p>
    </c:if>
    <c:if test="${not empty requestScope.overallResults}">
        <table class="data-table">
            <thead>
                <tr>
                    <th>Candidate Name</th>
                    <th>Party</th>
                    <th>Total Votes</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="result" items="${requestScope.overallResults}">
                    <tr>
                        <td>${result[2]} ${result[3]}</td> <%-- First Name, Last Name --%>
                        <td>${result[4]}</td> <%-- Party --%>
                        <td><strong>${result[1]}</strong></td> <%-- Vote Count --%>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:if>

    <h3 class="mt-20"><i class="fas fa-map-marked-alt"></i> Results by Constituency</h3>
    <c:if test="${empty requestScope.resultsByConstituency}">
        <p class="text-center">No constituencies with results found for this election.</p>
    </c:if>
    <c:if test="${not empty requestScope.resultsByConstituency}">
        <c:forEach var="entry" items="${requestScope.resultsByConstituency}">
            <h4>Constituency: ${entry.key}</h4>
            <c:if test="${empty entry.value}">
                <p>No votes recorded for this constituency in this election yet.</p>
            </c:if>
            <c:if test="${not empty entry.value}">
                <table class="data-table mb-20">
                    <thead>
                        <tr>
                            <th>Candidate Name</th>
                            <th>Party</th>
                            <th>Votes</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="constResult" items="${entry.value}">
                            <tr>
                                <td>${constResult[2]} ${constResult[3]}</td> <%-- First Name, Last Name --%>
                                <td>${constResult[4]}</td> <%-- Party --%>
                                <td>${constResult[1]}</td> <%-- Vote Count --%>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:if>
        </c:forEach>
    </c:if>

    <div class="text-center mt-20">
        <a href="${pageContext.request.contextPath}/admin" class="btn btn-secondary">Back to Admin Dashboard</a>
    </div>
</section>

<%-- Include footer --%>
<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
