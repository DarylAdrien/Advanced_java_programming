<%-- src/main/webapp/WEB-INF/jsp/common/header.jsp --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${pageTitle} - E-Voting</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
</head>
<body>
    <header id="mainHeader">
        <nav id="navbar">
            <div class="logo">
                <a href="${pageContext.request.contextPath}/index.jsp">
                    <img src="${pageContext.request.contextPath}/images/logo.png" alt="E-Voting Logo" class="header-logo">
                    E-Voting Portal
                </a>
            </div>
            <ul class="nav-links">
                <li class="<c:if test="${currentPage eq 'home'}">active</c:if>"><a href="${pageContext.request.contextPath}/index.jsp">Home</a></li>
                <li class="<c:if test="${currentPage eq 'help'}">active</c:if>"><a href="${pageContext.request.contextPath}/help.jsp">Help</a></li>
                <%-- Show Register/Login if no user is logged in (check userId attribute) --%>
                <c:if test="${sessionScope.userId == null}">
                    <li class="<c:if test="${currentPage eq 'register'}">active</c:if>"><a href="${pageContext.request.contextPath}/register">Register</a></li>
                    <li class="<c:if test="${currentPage eq 'login'}">active</c:if>"><a href="${pageContext.request.contextPath}/login">Login</a></li>
                </c:if>
                <%-- Show Dashboards/Logout if a user is logged in (check userId attribute) --%>
                <c:if test="${sessionScope.userId != null}">
                    <c:choose>
                        <c:when test="${sessionScope.role eq 'ADMIN'}">
                            <li class="<c:if test="${currentPage eq 'adminDashboard'}">active</c:if>"><a href="${pageContext.request.contextPath}/admin">Admin Dashboard</a></li>
                        </c:when>
                        <c:when test="${sessionScope.role eq 'VOTER'}">
                            <li class="<c:if test="${currentPage eq 'voterDashboard'}">active</c:if>"><a href="${pageContext.request.contextPath}/voter">Voter Dashboard</a></li>
                        </c:when>
                        <c:when test="${sessionScope.role eq 'CANDIDATE'}">
                            <li class="<c:if test="${currentPage eq 'candidateDashboard'}">active</c:if>"><a href="${pageContext.request.contextPath}/candidate">Candidate Dashboard</a></li>
                        </c:when>
                    </c:choose>
                    <li><a href="${pageContext.request.contextPath}/logout">Logout</a></li>
                </c:if>
            </ul>
        </nav>
    </header>

    <script>
        var prevScrollpos = window.pageYOffset;
        var mainHeader = document.getElementById("mainHeader");

        window.onscroll = function() {
            var currentScrollPos = window.pageYOffset;
            if (prevScrollpos > currentScrollPos) {
                // Scrolling Up
                mainHeader.style.top = "0";
            } else {
                // Scrolling Down
                if (currentScrollPos > mainHeader.offsetHeight) {
                    mainHeader.style.top = "-" + mainHeader.offsetHeight + "px";
                }
            }
            prevScrollpos = currentScrollPos;
        }
    </script>
