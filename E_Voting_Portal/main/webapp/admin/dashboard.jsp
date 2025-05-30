<%-- src/main/webapp/admin/dashboard.jsp --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%-- Include header --%>
<c:set var="pageTitle" value="Admin Dashboard" scope="request"/>
<c:set var="currentPage" value="adminDashboard" scope="request"/>
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>
<%@ page isErrorPage="true" %>

<section>
    <h2>Welcome, Administrator ${sessionScope.firstName} ${sessionScope.lastName}!</h2>
    <p class="text-center">Manage users, elections, constituencies, candidates, and view system logs.</p>
    <%-- User Approval Section --%>
   <%--  <h3><i class="fas fa-user-plus"></i> Pending User Registrations</h3>
    <c:if test="${empty requestScope.pendingUsers}">
        <p class="text-center">No pending user registrations at the moment.</p>
    </c:if>
    <c:if test="${not empty requestScope.pendingUsers}">
        <table class="data-table">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Username</th>
                    <th>Name</th>
                    <th>Email</th>
                    <th>Aadhaar</th>
                    <th>Role</th>
                    <th>Constituency</th>
                    <th>Registered On</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="user" items="${requestScope.pendingUsers}">
                    <tr>
                        <td>${user.userId}</td>
                        <td>${user.username}</td>
                        <td>${user.firstName} ${user.lastName}</td>
                        <td>${user.email}</td>
                        <td>${user.aadhaarNumber}</td>
                        <td>${user.role}</td>
                        <td>
                            <c:set var="constituencyName" value="N/A"/>
                            <c:forEach var="constituency" items="${requestScope.constituencies}">
                                <c:if test="${constituency.constituencyId == user.constituencyId}">
                                    <c:set var="constituencyName" value="${constituency.name}"/>
                                </c:if>
                            </c:forEach>
                            ${constituencyName}
                        </td>
                        <td><fmt:formatDate value="${user.createdAt}" pattern="yyyy-MM-dd HH:mm"/></td>
                        <td class="table-actions">
                            <form action="${pageContext.request.contextPath}/admin" method="post" style="display:inline;">
                                <input type="hidden" name="action" value="approveUser">
                                <input type="hidden" name="userId" value="${user.userId}">
                                <button type="submit" class="btn btn-success btn-small">Approve</button>
                            </form>
                            <form action="${pageContext.request.contextPath}/admin" method="post" style="display:inline;">
                                <input type="hidden" name="action" value="rejectUser">
                                <input type="hidden" name="userId" value="${user.userId}">
                                <button type="submit" class="btn btn-danger btn-small">Reject</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:if>  --%>
    		

    <%-- Candidate Approval Section --%>
    <h3 class="mt-20"><i class="fas fa-user-check"></i> Pending Candidate Approvals</h3>
    <c:set var="hasPendingCandidates" value="false"/>
    <c:forEach var="candidate" items="${requestScope.candidatesPendingApproval}">
        <c:if test="${candidate.approvalStatus eq 'PENDING'}">
            <c:set var="hasPendingCandidates" value="true"/>
        </c:if>
    </c:forEach>

    <c:if test="${!hasPendingCandidates}">
        <p class="text-center">No pending candidate applications at the moment.</p>
    </c:if>
    <c:if test="${hasPendingCandidates}">
        <table class="data-table">
            <thead>
                <tr>
                    <th>Candidate ID</th>
                    <th>User</th>
                    <th>Election</th>
                    <th>Constituency</th>
                    <th>Party</th>
                    <th>Status</th>
                    <th>Applied On</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="candidate" items="${requestScope.candidatesPendingApproval}">
                    <c:if test="${candidate.approvalStatus eq 'PENDING'}">
                        <tr>
                            <td>${candidate.candidateId}</td>
                            <td>
                                <c:set var="candidateUser" value="${null}"/>
                                <c:forEach var="user" items="${requestScope.allUsers}">
                                    <c:if test="${user.userId == candidate.userId}">
                                        <c:set var="candidateUser" value="${user}"/>
                                    </c:if>
                                </c:forEach>
                                <c:if test="${candidateUser != null}">
                                    ${candidateUser.firstName} ${candidateUser.lastName} (${candidateUser.username})
                                </c:if>
                            </td>
                            <td>
                                <c:set var="electionName" value="N/A"/>
                                <c:forEach var="election" items="${requestScope.elections}">
                                    <c:if test="${election.electionId == candidate.electionId}">
                                        <c:set var="electionName" value="${election.name}"/>
                                    </c:if>
                                </c:forEach>
                                ${electionName}
                            </td>
                            <td>
                                <c:set var="constituencyName" value="N/A"/>
                                <c:forEach var="constituency" items="${requestScope.constituencies}">
                                    <c:if test="${constituency.constituencyId == candidate.constituencyId}">
                                        <c:set var="constituencyName" value="${constituency.name}"/>
                                    </c:if>
                                </c:forEach>
                                ${constituencyName}
                            </td>
                            <td>${candidate.party}</td>
                            <td>${candidate.approvalStatus}</td>
                            <td><fmt:formatDate value="${candidate.createdAt}" pattern="yyyy-MM-dd HH:mm"/></td>
                            <td class="table-actions">
                                <form action="${pageContext.request.contextPath}/admin" method="post" style="display:inline;">
                                    <input type="hidden" name="action" value="approveCandidate">
                                    <input type="hidden" name="candidateId" value="${candidate.candidateId}">
                                    <button type="submit" class="btn btn-success btn-small">Approve</button>
                                </form>
                                <form action="${pageContext.request.contextPath}/admin" method="post" style="display:inline;">
                                    <input type="hidden" name="action" value="rejectCandidate">
                                    <input type="hidden" name="candidateId" value="${candidate.candidateId}">
                                    <button type="submit" class="btn btn-danger btn-small">Reject</button>
                                </form>
                            </td>
                        </tr>
                    </c:if>
                </c:forEach>
            </tbody>
        </table>
    </c:if>

    <%-- Manage Users Section --%>
    <h3 class="mt-20"><i class="fas fa-users"></i> Manage All Users</h3>
    <table class="data-table">
        <thead>
            <tr>
                <th>ID</th>
                <th>Username</th>
                <th>Name</th>
                <th>Email</th>
                <th>Role</th>
                <th>Registered</th>
                <th>OTP Enabled</th>
                <th>Actions</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="user" items="${requestScope.allUsers}">
                <tr>
                    <td>${user.userId}</td>
                    <td>${user.username}</td>
                    <td>${user.firstName} ${user.lastName}</td>
                    <td>${user.email}</td>
                    <td>${user.role}</td>
                    <td>
					  <c:choose>
					    <c:when test="${user.registered == 1}">
					      <i class="fas fa-check-circle text-success"></i> Yes
					    </c:when>
					    <c:otherwise>
					      <i class="fas fa-times-circle text-danger"></i> No
					    </c:otherwise>
					  </c:choose>
					</td>
                    <td>
					  <c:choose>
					    <c:when test="${user.otpEnabled == 1}">
					      <i class="fas fa-check-circle text-success"></i> Yes
					    </c:when>
					    <c:otherwise>
					      <i class="fas fa-times-circle text-danger"></i> No
					    </c:otherwise>
					  </c:choose>
					</td>
                    <td class="table-actions">
                        <button type="button" class="btn btn-primary btn-small" onclick="openEditUserModal(${user.userId}, '${user.username}', '${user.firstName}', '${user.lastName}', '${user.email}', '${user.phoneNumber}', '${user.aadhaarNumber}', '${user.role}', ${user.registered}, ${user.otpEnabled}, ${user.constituencyId});">Edit</button>
                        <form action="${pageContext.request.contextPath}/admin" method="post" style="display:inline;">
                            <input type="hidden" name="action" value="toggleOtp">
                            <input type="hidden" name="userId" value="${user.userId}">
                            <button type="submit" class="btn btn-secondary btn-small">Toggle OTP</button>
                        </form>
                        <form action="${pageContext.request.contextPath}/admin" method="post" style="display:inline;" onsubmit="return confirm('Are you sure you want to delete user ${user.username}?');">
                            <input type="hidden" name="action" value="deleteUser">
                            <input type="hidden" name="userId" value="${user.userId}">
                            <button type="submit" class="btn btn-danger btn-small">Delete</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>

    <%-- Edit User Modal (Hidden by default) --%>
    <div id="editUserModal" class="modal">
        <div class="modal-content">
            <span class="close-button" onclick="closeEditUserModal()">&times;</span>
            <h3>Edit User</h3>
            <form action="${pageContext.request.contextPath}/admin" method="post">
                <input type="hidden" name="action" value="updateUser">
                <input type="hidden" id="editUserId" name="userId">

                <div class="form-group">
                    <label for="editUsername">Username:</label>
                    <input type="text" id="editUsername" name="username" readonly>
                </div>
                <div class="form-group">
                    <label for="editFirstName">First Name:</label>
                    <input type="text" id="editFirstName" name="firstName" required>
                </div>
                <div class="form-group">
                    <label for="editLastName">Last Name:</label>
                    <input type="text" id="editLastName" name="lastName">
                </div>
                <div class="form-group">
                    <label for="editEmail">Email:</label>
                    <input type="email" id="editEmail" name="email" required>
                </div>
                <div class="form-group">
                    <label for="editPhoneNumber">Phone Number:</label>
                    <input type="tel" id="editPhoneNumber" name="phoneNumber">
                </div>
                <div class="form-group">
                    <label for="editAadhaarNumber">Aadhaar Number:</label>
                    <input type="text" id="editAadhaarNumber" name="aadhaarNumber" required>
                </div>
                <div class="form-group">
                    <label for="editRole">Role:</label>
                    <select id="editRole" name="role" required>
                        <option value="VOTER">VOTER</option>
                        <option value="CANDIDATE">CANDIDATE</option>
                        <option value="ADMIN">ADMIN</option>
                    </select>
                </div>
                <div class="form-group">
                    <label for="editConstituencyId">Constituency:</label>
                    <select id="editConstituencyId" name="constituencyId">
                        <option value="">-- Select Constituency (Optional for Admin) --</option>
                        <c:forEach var="constituency" items="${requestScope.constituencies}">
                            <option value="${constituency.constituencyId}">${constituency.name}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="form-group">
                    <label for="editIsRegistered">Is Registered (Approved):</label>
                    <select id="editIsRegistered" name="isRegistered">
                        <option value="1">Yes</option>
                        <option value="0">No</option>
                    </select>
                </div>
                <div class="form-group">
                    <label for="editIsOtpEnabled">OTP Enabled:</label>
                    <select id="editIsOtpEnabled" name="isOtpEnabled">
                        <option value="1">Yes</option>
                        <option value="0">No</option>
                    </select>
                </div>
                <div class="form-group">
                    <label for="newPassword">New Password (leave blank if not changing):</label>
                    <input type="password" id="newPassword" name="newPassword">
                </div>
                <div class="form-group text-center">
                    <button type="submit" class="btn btn-primary">Update User</button>
                </div>
            </form>
        </div>
    </div>


    <%-- Constituency Management Section --%>
    <h3 class="mt-20"><i class="fas fa-city"></i> Manage Constituencies</h3>
    <form action="${pageContext.request.contextPath}/admin" method="post" class="form-container">
        <h4>Add New Constituency</h4>
        <input type="hidden" name="action" value="addConstituency">
        <div class="form-group">
            <label for="constName">Name:</label>
            <input type="text" id="constName" name="name" required>
        </div>
        <div class="form-group">
            <label for="constDesc">Description:</label>
            <textarea id="constDesc" name="description"></textarea>
        </div>
        <div class="form-group text-center">
            <button type="submit" class="btn btn-primary">Add Constituency</button>
        </div>
    </form>

    <table class="data-table">
        <thead>
            <tr>
                <th>ID</th>
                <th>Name</th>
                <th>Description</th>
                <th>Created At</th>
                <th>Actions</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="constituency" items="${requestScope.constituencies}">
                <tr>
                    <td>${constituency.constituencyId}</td>
                    <td>${constituency.name}</td>
                    <td>${constituency.description}</td>
                    <td><fmt:formatDate value="${constituency.createdAt}" pattern="yyyy-MM-dd HH:mm"/></td>
                    <td class="table-actions">
                        <button type="button" class="btn btn-primary btn-small" onclick="openEditConstituencyModal(${constituency.constituencyId}, '${constituency.name}', '${constituency.description}');">Edit</button>
                        <form action="${pageContext.request.contextPath}/admin" method="post" style="display:inline;" onsubmit="return confirm('Are you sure you want to delete constituency ${constituency.name}? This cannot be undone if linked to users/candidates!');">
                            <input type="hidden" name="action" value="deleteConstituency">
                            <input type="hidden" name="constituencyId" value="${constituency.constituencyId}">
                            <button type="submit" class="btn btn-danger btn-small">Delete</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>

    <%-- Edit Constituency Modal (Hidden by default) --%>
    <div id="editConstituencyModal" class="modal">
        <div class="modal-content">
            <span class="close-button" onclick="closeEditConstituencyModal()">&times;</span>
            <h3>Edit Constituency</h3>
            <form action="${pageContext.request.contextPath}/admin" method="post">
                <input type="hidden" name="action" value="updateConstituency">
                <input type="hidden" id="editConstituencyId" name="constituencyId">

                <div class="form-group">
                    <label for="editConstName">Name:</label>
                    <input type="text" id="editConstName" name="name" required>
                </div>
                <div class="form-group">
                    <label for="editConstDesc">Description:</label>
                    <textarea id="editConstDesc" name="description"></textarea>
                </div>
                <div class="form-group text-center">
                    <button type="submit" class="btn btn-primary">Update Constituency</button>
                </div>
            </form>
        </div>
    </div>

    <%-- Election Management Section --%>
    <h3 class="mt-20"><i class="fas fa-calendar-alt"></i> Manage Elections</h3>
    <form action="${pageContext.request.contextPath}/admin" method="post" class="form-container">
        <h4>Add New Election</h4>
        <input type="hidden" name="action" value="addElection">
        <div class="form-group">
            <label for="electionName">Name:</label>
            <input type="text" id="electionName" name="name" required>
        </div>
        <div class="form-group">
            <label for="electionDesc">Description:</label>
            <textarea id="electionDesc" name="description"></textarea>
        </div>
        <div class="form-group">
            <label for="startDate">Start Date & Time:</label>
            <input type="datetime-local" id="startDate" name="startDate" required>
        </div>
        <div class="form-group">
            <label for="endDate">End Date & Time:</label>
            <input type="datetime-local" id="endDate" name="endDate" required>
        </div>
        <div class="form-group">
            <label for="electionStatus">Status:</label>
            <select id="electionStatus" name="status" required>
                <option value="UPCOMING">UPCOMING</option>
                <option value="ACTIVE">ACTIVE</option>
                <option value="COMPLETED">COMPLETED</option>
                <option value="CANCELLED">CANCELLED</option>
            </select>
        </div>
        <div class="form-group text-center">
            <button type="submit" class="btn btn-primary">Add Election</button>
        </div>
    </form>

    <table class="data-table">
        <thead>
            <tr>
                <th>ID</th>
                <th>Name</th>
                <th>Status</th>
                <th>Start Date</th>
                <th>End Date</th>
                <th>Actions</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="election" items="${requestScope.elections}">
                <tr>
                    <td>${election.electionId}</td>
                    <td>${election.name}</td>
                    <td>${election.status}</td>
                    <td><fmt:formatDate value="${election.startDate}" pattern="yyyy-MM-dd HH:mm"/></td>
                    <td><fmt:formatDate value="${election.endDate}" pattern="yyyy-MM-dd HH:mm"/></td>
                    <td class="table-actions">
                        <button type="button" class="btn btn-primary btn-small" onclick="openEditElectionModal(${election.electionId}, '${election.name}', '${election.description}', '<fmt:formatDate value="${election.startDate}" pattern="yyyy-MM-dd'T'HH:mm"/>', '<fmt:formatDate value="${election.endDate}" pattern="yyyy-MM-dd'T'HH:mm"/>', '${election.status}');">Edit</button>
                        <form action="${pageContext.request.contextPath}/admin" method="post" style="display:inline;" onsubmit="return confirm('Are you sure you want to delete election ${election.name}? This cannot be undone if linked to candidates/votes!');">
                            <input type="hidden" name="action" value="deleteElection">
                            <input type="hidden" name="electionId" value="${election.electionId}">
                            <button type="submit" class="btn btn-danger btn-small">Delete</button>
                        </form>
                        <form action="${pageContext.request.contextPath}/admin" method="post" style="display:inline;">
                            <input type="hidden" name="action" value="viewElectionResults">
                            <input type="hidden" name="electionId" value="${election.electionId}">
                            <button type="submit" class="btn btn-secondary btn-small">View Results</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>

    <%-- Edit Election Modal (Hidden by default) --%>
    <div id="editElectionModal" class="modal">
        <div class="modal-content">
            <span class="close-button" onclick="closeEditElectionModal()">&times;</span>
            <h3>Edit Election</h3>
            <form action="${pageContext.request.contextPath}/admin" method="post">
                <input type="hidden" name="action" value="updateElection">
                <input type="hidden" id="editElectionId" name="electionId">

                <div class="form-group">
                    <label for="editElectionName">Name:</label>
                    <input type="text" id="editElectionName" name="name" required>
                </div>
                <div class="form-group">
                    <label for="editElectionDesc">Description:</label>
                    <textarea id="editElectionDesc" name="description"></textarea>
                </div>
                <div class="form-group">
                    <label for="editStartDate">Start Date & Time:</label>
                    <input type="datetime-local" id="editStartDate" name="startDate" required>
                </div>
                <div class="form-group">
                    <label for="editEndDate">End Date & Time:</label>
                    <input type="datetime-local" id="editEndDate" name="endDate" required>
                </div>
                <div class="form-group">
                    <label for="editElectionStatus">Status:</label>
                    <select id="editElectionStatus" name="status" required>
                        <option value="UPCOMING">UPCOMING</option>
                        <option value="ACTIVE">ACTIVE</option>
                        <option value="COMPLETED">COMPLETED</option>
                        <option value="CANCELLED">CANCELLED</option>
                    </select>
                </div>
                <div class="form-group text-center">
                    <button type="submit" class="btn btn-primary">Update Election</button>
                </div>
            </form>
        </div>
    </div>

    <%-- System Logs Section --%>
    <h3 class="mt-20"><i class="fas fa-clipboard-list"></i> System Logs</h3>
    <table class="data-table">
        <thead>
            <tr>
                <th>Log ID</th>
                <th>Timestamp</th>
                <th>User ID</th>
                <th>Action</th>
                <th>Details</th>
                <th>IP Address</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="log" items="${requestScope.systemLogs}">
                <tr>
                    <td>${log.logId}</td>
                    <td><fmt:formatDate value="${log.logTimestamp}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                    <td>${log.userId != null ? log.userId : 'N/A'}</td>
                    <td>${log.action}</td>
                    <td>${log.details}</td>
                    <td>${log.ipAddress}</td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</section>

<style>
    /* Modal Styles */
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

	
    // JavaScript for Modals
    function openEditUserModal(userId, username, firstName, lastName, email, phoneNumber, aadhaarNumber, role, isRegistered, isOtpEnabled, constituencyId) {
        document.getElementById('editUserId').value = userId;
        document.getElementById('editUsername').value = username;
        document.getElementById('editFirstName').value = firstName;
        document.getElementById('editLastName').value = lastName;
        document.getElementById('editEmail').value = email;
        document.getElementById('editPhoneNumber').value = phoneNumber;
        document.getElementById('editAadhaarNumber').value = aadhaarNumber;
        document.getElementById('editRole').value = role;
        document.getElementById('editIsRegistered').value = isRegistered;
        document.getElementById('editIsOtpEnabled').value = isOtpEnabled;
        document.getElementById('editConstituencyId').value = constituencyId; // Set constituency ID
        document.getElementById('editUserModal').style.display = 'block';
    }

    function closeEditUserModal() {
        document.getElementById('editUserModal').style.display = 'none';
    }

    function openEditConstituencyModal(constituencyId, name, description) {
        document.getElementById('editConstituencyId').value = constituencyId;
        document.getElementById('editConstName').value = name;
        document.getElementById('editConstDesc').value = description;
        document.getElementById('editConstituencyModal').style.display = 'block';
    }

    function closeEditConstituencyModal() {
        document.getElementById('editConstituencyModal').style.display = 'none';
    }

    function openEditElectionModal(electionId, name, description, startDate, endDate, status) {
        document.getElementById('editElectionId').value = electionId;
        document.getElementById('editElectionName').value = name;
        document.getElementById('editElectionDesc').value = description;
        document.getElementById('editStartDate').value = startDate; // Format must match datetime-local
        document.getElementById('editEndDate').value = endDate;     // Format must match datetime-local
        document.getElementById('editElectionStatus').value = status;
        document.getElementById('editElectionModal').style.display = 'block';
    }

    function closeEditElectionModal() {
        document.getElementById('editElectionModal').style.display = 'none';
    }

    // Close modals if user clicks outside of them
    window.onclick = function(event) {
        if (event.target == document.getElementById('editUserModal')) {
            closeEditUserModal();
        }
        if (event.target == document.getElementById('editConstituencyModal')) {
            closeEditConstituencyModal();
        }
        if (event.target == document.getElementById('editElectionModal')) {
            closeEditElectionModal();
        }
    }
</script>

<%-- Include footer --%>
<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
