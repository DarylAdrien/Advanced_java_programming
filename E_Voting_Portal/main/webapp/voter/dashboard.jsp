<%-- src/main/webapp/voter/dashboard.jsp --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%-- Include header --%>
<c:set var="pageTitle" value="Voter Dashboard" scope="request"/>
<c:set var="currentPage" value="voterDashboard" scope="request"/>
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>

<section>
    <h2>Welcome, Voter ${sessionScope.firstName} ${sessionScope.lastName}!</h2>
    <p class="text-center">Your Constituency:
        <c:set var="voterConstituencyName" value="Not Assigned"/>
        <c:forEach var="constituency" items="${requestScope.constituency}">
            <c:if test="${constituency.constituencyId == requestScope.voterConstituencyId}">
                <c:set var="voterConstituencyName" value="${constituency.name}"/>
            </c:if>
        </c:forEach>
        <strong>${voterConstituencyName}</strong>
    

    <h3 class="mt-20"><i class="fas fa-calendar-check"></i> Active Elections in Your Constituency</h3>
    <c:if test="${empty requestScope.activeElections}">
        <p class="text-center">No active elections in your constituency at the moment.</p>
    </c:if>
    <c:if test="${not empty requestScope.activeElections}">
        <table class="data-table">
            <thead>
                <tr>
                    <th>Election Name</th>
                    <th>Start Date</th>
                    <th>End Date</th>
                    <th>Status</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="election" items="${requestScope.activeElections}">
                    <tr>
                        <td>${election.name}</td>
                        <td><fmt:formatDate value="${election.startDate}" pattern="yyyy-MM-dd HH:mm"/></td>
                        <td><fmt:formatDate value="${election.endDate}" pattern="yyyy-MM-dd HH:mm"/></td>
                        <td>${election.status}</td>
                        <td class="table-actions">
                            <a href="${pageContext.request.contextPath}/voter?electionId=${election.electionId}" class="btn btn-primary btn-small">View Candidates</a>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:if>

    <c:if test="${requestScope.selectedElection != null}">
        <h3 class="mt-20"><i class="fas fa-users-line"></i> Candidates for "${requestScope.selectedElection.name}"</h3>
        <c:if test="${requestScope.hasVoted}">
            <div class="message success-message">
                <i class="fas fa-check-circle"></i> You have already cast your vote for this election.
            </div>
        </c:if>
        <c:if test="${empty requestScope.candidatesForConstituency}">
            <p class="text-center">No approved candidates in your constituency for this election yet.</p>
        </c:if>
        <c:if test="${not empty requestScope.candidatesForConstituency}">
            <div class="card-grid">
                <c:forEach var="candidate" items="${requestScope.candidatesForConstituency}">
                    <div class="card">
                        <h3>${candidate.party}</h3>
                        <p>Candidate:
                            <c:set var="candidateUser" value="${null}"/>
                            <c:forEach var="user" items="${requestScope.allUsers}"> <%-- Assuming allUsers is available or fetch here --%>
                                <c:if test="${user.userId == candidate.userId}">
                                    <c:set var="candidateUser" value="${user}"/>
                                </c:if>
                            </c:forEach>
                            <c:if test="${candidateUser != null}">
                                <strong>${candidateUser.firstName} ${candidateUser.lastName}</strong>
                            </c:if>
                        </p>
                        <c:if test="${not empty candidate.symbolUrl}">
                            <img src="${candidate.symbolUrl}" alt="Symbol" style="max-width: 80px; height: auto; border-radius: 5px; margin-bottom: 10px;">
                        </c:if>
                        <p>Manifesto: ${candidate.manifesto}</p>
                        <c:if test="${!requestScope.hasVoted}">
                            <form action="${pageContext.request.contextPath}/voter" method="post" style="width: 100%;">
                                <input type="hidden" name="action" value="castVote">
                                <input type="hidden" name="electionId" value="${requestScope.selectedElection.electionId}">
                                <input type="hidden" name="candidateId" value="${candidate.candidateId}">
                                <button type="submit" class="btn btn-success" onclick="return confirm('Are you sure you want to vote for ${candidateUser.firstName} ${candidateUser.lastName} from ${candidate.party}? You cannot change your vote.');">Cast Vote</button>
                            </form>
                        </c:if>
                    </div>
                </c:forEach>
            </div>
        </c:if>
    </c:if>
</section>

<%-- Include footer --%>
<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
