<%-- src/main/webapp/candidate/dashboard.jsp --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%-- Include header --%>
<c:set var="pageTitle" value="Candidate Dashboard" scope="request"/>
<c:set var="currentPage" value="candidateDashboard" scope="request"/>
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>

<section>
    <h2>Welcome, Candidate ${sessionScope.firstName} ${sessionScope.lastName}!</h2>
<p class="text-center">Your Constituency:
    <c:set var="candidateConstituencyName" value="Not Assigned" scope="page" />
    <c:forEach var="constituency" items="${requestScope.constituency}">
        <c:if test="${constituency.constituencyId.toString() == requestScope.currentUser.constituencyId.toString()}">
            <c:set var="candidateConstituencyName" value="${constituency.name}" scope="page" />
        </c:if>
    </c:forEach>
    <strong>${candidateConstituencyName}</strong>
</p>

    <h3 class="mt-20"><i class="fas fa-file-signature"></i> Apply for Election</h3>
    <p class="text-center">Apply to be a candidate in upcoming elections in your constituency.</p>
    <form action="${pageContext.request.contextPath}/candidate" method="post" class="form-container">
        <input type="hidden" name="action" value="applyForElection">
        <div class="form-group">
            <label for="electionId">Select Election:</label>
            <select id="electionId" name="electionId" required>
                <option value="">-- Select an Upcoming Election --</option>
                <c:forEach var="election" items="${requestScope.upcomingElections}">
                    <option value="${election.electionId}">${election.name} (<fmt:formatDate value="${election.startDate}" pattern="MMM dd, yyyy"/>)</option>
                </c:forEach>
            </select>
        </div>
        <div class="form-group">
            <label for="party">Your Political Party:</label>
            <input type="text" id="party" name="party" required>
        </div>
        <div class="form-group">
            <label for="symbolUrl">Symbol URL (e.g., a small image URL):</label>
            <input type="text" id="symbolUrl" name="symbolUrl" placeholder="https://placehold.co/50x50/000/FFF?text=Symbol">
        </div>
        <div class="form-group">
            <label for="manifesto">Your Manifesto:</label>
            <textarea id="manifesto" name="manifesto" rows="5" required></textarea>
        </div>
        <div class="form-group text-center">
            <button type="submit" class="btn btn-primary">Submit Application</button>
        </div>
    </form>

    <h3 class="mt-20"><i class="fas fa-id-card"></i> Candidate Profile</h3>
    <c:if test="${empty requestScope.existingCandidateProfiles}">
        <p class="text-center">You have not applied for any elections yet.</p>
    </c:if>
    <c:if test="${not empty requestScope.existingCandidateProfiles}">
        <table class="data-table">
            <thead>
                <tr>
                    <th>Election</th>
                    <th>Party</th>
                    <th>Symbol</th>
                    <th>Manifesto</th>
                    <th>Approval Status</th>
                    <th>Applied On</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="profile" items="${requestScope.existingCandidateProfiles}">
                    <tr>
                        <td>
						    <c:set var="electionName" value="N/A"/>
						    <c:forEach var="election" items="${requestScope.elections}">
						        <c:if test="${election.electionId eq profile.electionId}">
						            <c:set var="electionName" value="${election.name}"/>
						        </c:if>
						    </c:forEach>
						    ${electionName}
						</td>
                        <td>${profile.party}</td>
                        <td>
                            <c:if test="${not empty profile.symbolUrl}">
                                <img src="${profile.symbolUrl}" alt="Symbol" style="max-width: 40px; height: auto; border-radius: 3px;">
                            </c:if>
                            <c:if test="${empty profile.symbolUrl}">N/A</c:if>
                        </td>
                        <td>${profile.manifesto}</td>
                        <td>
                            <c:choose>
                                <c:when test="${profile.approvalStatus eq 'APPROVED'}">
                                    <span style="color: green; font-weight: bold;">APPROVED <i class="fas fa-check-circle"></i></span>
                                </c:when>
                                <c:when test="${profile.approvalStatus eq 'PENDING'}">
                                    <span style="color: orange; font-weight: bold;">PENDING <i class="fas fa-hourglass-half"></i></span>
                                </c:when>
                                <c:when test="${profile.approvalStatus eq 'REJECTED'}">
                                    <span style="color: red; font-weight: bold;">REJECTED <i class="fas fa-times-circle"></i></span>
                                </c:when>
                                <c:otherwise>${profile.approvalStatus}</c:otherwise>
                            </c:choose>
                        </td>
                        <td><fmt:formatDate value="${profile.createdAt}" pattern="yyyy-MM-dd HH:mm"/></td>
                        <td class="table-actions">
                            <c:if test="${profile.approvalStatus eq 'PENDING' || profile.approvalStatus eq 'REJECTED'}">
							    <button
								    class="btn btn-primary btn-small"
								    data-id="${profile.candidateId}"
								    data-party="${profile.party}"
								    data-symbol="${profile.symbolUrl}"
								    data-manifesto="${fn:replace(profile.manifesto, '\'', '\\\\\'')}"
								    onclick="openEditManifestoFromButton(this)">
								    Edit
								</button>
							</c:if>
							<c:if test="${profile.approvalStatus eq 'APPROVED'}">
							    <button
								    class="btn btn-primary btn-small"
								    data-id="${profile.candidateId}"
								    data-party="${profile.party}"
								    data-symbol="${profile.symbolUrl}"
								    data-manifesto="${fn:replace(profile.manifesto, '\'', '\\\\\'')}"
								    onclick="openEditManifestoFromButton(this)">
								    Edit
								</button>
							</c:if>

                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:if>

    <%-- Edit Manifesto Modal (Hidden by default) --%>
    <div id="editManifestoModal" class="modal">
        <div class="modal-content">
            <span class="close-button" onclick="closeEditManifestoModal()">&times;</span>
            <h3>Update Candidate Profile</h3>
            <form action="${pageContext.request.contextPath}/candidate" method="post">
                <input type="hidden" name="action" value="updateManifesto">
                <input type="hidden" id="editCandidateProfileId" name="candidateProfileId">

                <div class="form-group">
                    <label for="editParty">Political Party:</label>
                    <input type="text" id="editParty" name="party" required>
                </div>
                <div class="form-group">
                    <label for="editSymbolUrl">Symbol URL:</label>
                    <input type="text" id="editSymbolUrl" name="symbolUrl" placeholder="https://placehold.co/50x50/000/FFF?text=Symbol">
                </div>
                <div class="form-group">
                    <label for="editManifesto">Manifesto:</label>
                    <textarea id="editManifesto" name="manifesto" rows="7" required></textarea>
                </div>
                <div class="form-group text-center">
                    <button type="submit" class="btn btn-primary">Update Profile</button>
                </div>
            </form>
        </div>
    </div>
</section>

<style>
    /* Modal Styles (copied from admin dashboard for consistency) */
    .modal {
        display: none; /* Hidden by default */
        position: fixed; /* Stay in place */
        z-index: 1001; /* Sit on top */
        left: 0;
        top: 0;
        width: 100%; /* Full width */
        height: 100%; /* Full height */
        overflow: auto; /* Enable scroll if needed */
        background-color: rgba(0,0,0,0.4); /* Black w/ opacity */
        padding-top: 60px;
    }

    .modal-content {
        background-color: #fefefe;
        margin: 5% auto; /* 15% from the top and centered */
        padding: 30px;
        border: 1px solid #888;
        width: 80%; /* Could be more responsive */
        max-width: 600px;
        border-radius: 12px;
        box-shadow: 0 8px 16px rgba(0,0,0,0.2);
        position: relative;
    }

    .close-button {
        color: #aaa;
        float: right;
        font-size: 28px;
        font-weight: bold;
        position: absolute;
        top: 10px;
        right: 20px;
    }

    .close-button:hover,
    .close-button:focus {
        color: black;
        text-decoration: none;
        cursor: pointer;
    }
</style>

<script>
function openEditManifestoFromButton(button) {
    const candidateId = button.getAttribute('data-id');
    const party = button.getAttribute('data-party');
    const symbolUrl = button.getAttribute('data-symbol');
    const manifesto = button.getAttribute('data-manifesto');

    openEditManifestoModal(candidateId, party, symbolUrl, manifesto);
}
    // JavaScript for Edit Manifesto Modal
    function openEditManifestoModal(candidateId, party, symbolUrl, manifesto) {
        document.getElementById('editCandidateProfileId').value = candidateId;
        document.getElementById('editParty').value = party;
        document.getElementById('editSymbolUrl').value = symbolUrl;
        document.getElementById('editManifesto').value = manifesto;
        document.getElementById('editManifestoModal').style.display = 'block';
    }

    function closeEditManifestoModal() {
        document.getElementById('editManifestoModal').style.display = 'none';
    }

    // Close modal if user clicks outside of it
    window.onclick = function(event) {
        if (event.target == document.getElementById('editManifestoModal')) {
            closeEditManifestoModal();
        }
    }
</script>

<%-- Include footer --%>
<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
