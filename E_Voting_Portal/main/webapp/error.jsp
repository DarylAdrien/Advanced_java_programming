<%-- src/main/webapp/error.jsp --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%-- Include header --%>
<c:set var="pageTitle" value="Error" scope="request"/>
<c:set var="currentPage" value="error" scope="request"/>
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>

<section class="text-center" style="padding: 50px 0;">
    <h2 style="color: #e74c3c;"><i class="fas fa-exclamation-circle"></i> An Error Occurred!</h2>
    <p style="font-size: 1.2rem; margin-bottom: 30px;">We're sorry, but something went wrong.</p>

    <c:choose>
        <c:when test="${not empty requestScope.errorMessage}">
            <div class="message error-message">
                <p>${requestScope.errorMessage}</p>
            </div>
        </c:when>
        <c:when test="${pageContext.exception != null}">
            <div class="message error-message">
                <p><strong>Exception:</strong> ${pageContext.exception.message}</p>
                <%-- Optionally display stack trace for debugging in development --%>
                <%--
                <pre style="text-align: left; background-color: #fdd; padding: 15px; border-radius: 8px; overflow-x: auto;">
                    <c:forEach var="ste" items="${pageContext.exception.stackTrace}">
                        ${ste}<br/>
                    </c:forEach>
                </pre>
                --%>
            </div>
        </c:when>
        <c:when test="${requestScope['javax.servlet.error.status_code'] != null}">
            <div class="message error-message">
                <p><strong>Error Code:</strong> ${requestScope['javax.servlet.error.status_code']}</p>
                <p><strong>Error Message:</strong> ${requestScope['javax.servlet.error.message']}</p>
            </div>
        </c:when>
        <c:otherwise>
            <div class="message error-message">
                <p>An unknown error has occurred. Please try again or contact support.</p>
            </div>
        </c:otherwise>
    </c:choose>

    <div class="btn-group mt-20">
        <a href="${pageContext.request.contextPath}/index.jsp" class="btn btn-primary">Go to Home Page</a>
        <a href="${pageContext.request.contextPath}/help.jsp" class="btn btn-secondary">Get Help</a>
    </div>
</section>

<%-- Include footer --%>
<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
